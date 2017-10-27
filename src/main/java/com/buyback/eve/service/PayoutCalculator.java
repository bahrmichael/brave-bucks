package com.buyback.eve.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

import javax.annotation.PostConstruct;

import com.buyback.eve.domain.Donation;
import com.buyback.eve.domain.Killmail;
import com.buyback.eve.domain.Transaction;
import com.buyback.eve.domain.User;
import com.buyback.eve.repository.DonationRepository;
import com.buyback.eve.repository.KillmailRepository;
import com.buyback.eve.repository.TransactionRepository;
import com.buyback.eve.repository.UserRepository;
import com.codahale.metrics.annotation.Timed;

import static com.buyback.eve.domain.enumeration.TransactionType.KILL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PayoutCalculator {

    public static final long FINAL_BLOW_BONUS = 2;
    private final KillmailRepository killmailRepository;
    private final UserRepository userRepository;
    private final DonationRepository donationRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public PayoutCalculator(final KillmailRepository killmailRepository,
                            final UserRepository userRepository,
                            final DonationRepository donationRepository,
                            final TransactionRepository transactionRepository) {
        this.killmailRepository = killmailRepository;
        this.userRepository = userRepository;
        this.donationRepository = donationRepository;
        this.transactionRepository = transactionRepository;
    }

    @PostConstruct
    public void init() {
        calculatePayouts();
    }

    @Async
    @Timed
    @Scheduled(cron = "0 0 11 * * *")
    public void calculatePayouts() {
        final List<User> users = userRepository.findAll().stream()
                                               .filter(user -> user.getCharacterId() != null)
                                               .collect(toList());
        final List<Long> userIds = new ArrayList<>();
        users.stream().mapToLong(User::getCharacterId).forEach(userIds::add);

        final List<Killmail> pendingKillmails = killmailRepository.findPending();
        final Collection<Transaction> transactions = getTransactions(users, userIds, pendingKillmails);

        transactionRepository.save(transactions);
        pendingKillmails.forEach(km -> {
            km.setPayoutCalculated(true);
            killmailRepository.save(km);
        });
    }

    private Collection<Transaction> getTransactions(final List<User> users, final List<Long> userIds,
                                                    final List<Killmail> pendingKillmails) {
        final long totalPoints = getTotalPoints(pendingKillmails, userIds);
        final double todayPayable = getPayable();

        final Collection<Transaction> transactions = new ArrayList<>();

        for (final Long userId : userIds) {
            final long pointsForUser = getPointsForUser(pendingKillmails, userId);
            if (pointsForUser == 0 || totalPoints == 0) {
                continue;
            }
            final double factor = (double) pointsForUser / totalPoints;
            final double userPayable = todayPayable * factor;
            final String user = getUserName(users, userId);
            transactions.add(new Transaction(user, userPayable, KILL));
        }
        return transactions;
    }

    private long getTotalPoints(final Iterable<Killmail> killmails, final Collection<Long> userIds) {
        return userIds.parallelStream().mapToLong(id -> getPointsForUser(killmails, id)).sum();
    }

    private long getPointsForUser(final Iterable<Killmail> killmails, final Long userId) {
        long sum = 0;
        for (final Killmail killmail : killmails) {
            final long points = killmail.getPoints();
            for (final Long attackerId : killmail.getAttackerIds()) {
                if (Objects.equals(userId, attackerId)) {
                    sum += points;
                }

                if (userId == killmail.getFinalBlowAttackerId()) {
                    sum += FINAL_BLOW_BONUS;
                }
            }

        }

        return sum;
    }

    private double getPayable() {
        final LocalDate now = LocalDate.now();
        return donationRepository.findByMonth(now.getYear() + "-" + now.getMonthValue()).stream()
                                 .mapToDouble(this::getRemainingWorth).sum();
    }

    private boolean isToday(final Transaction transaction) {
        return transaction.getInstant().toEpochMilli() >= LocalDate.now().toEpochDay();
    }

    private String getUserName(final Iterable<User> users, final Long userId) {
        // fallback with userId
        String user = String.valueOf(userId);
        for (final User u : users) {
            if (Objects.equals(u.getCharacterId(), userId)) {
                user = u.getLogin();
                break;
            }
        }
        return user;
    }

    private double getRemainingWorth(final Donation donation) {
        final Instant monthBorder = getMonthBorder();
        final int monthLength = LocalDate.now().getMonth().maxLength();
        if (monthBorder.isAfter(donation.getCreated())) {
            return donation.getAmount() / monthLength;
        } else {
            // add 1, so we don't div by 0 at the end of the month
            return donation.getAmount() / (1 + monthLength -
                                           LocalDateTime.ofInstant(donation.getCreated(), ZoneId.systemDefault())
                                                        .getDayOfMonth());
        }
    }

    private Instant getMonthBorder() {
        final LocalDate now = LocalDate.now();
        final LocalDate of = LocalDate.of(now.getYear(), now.getMonth(), 1);
        return of.atStartOfDay().toInstant(ZoneOffset.UTC);
    }
}
