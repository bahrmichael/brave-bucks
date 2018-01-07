package com.bravebucks.eve.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

import javax.annotation.PostConstruct;

import com.bravebucks.eve.domain.Transaction;
import com.bravebucks.eve.domain.User;
import com.bravebucks.eve.domain.Donation;
import com.bravebucks.eve.domain.Killmail;
import com.bravebucks.eve.repository.DonationRepository;
import com.bravebucks.eve.repository.KillmailRepository;
import com.bravebucks.eve.repository.TransactionRepository;
import com.bravebucks.eve.repository.UserRepository;
import com.codahale.metrics.annotation.Timed;

import static com.bravebucks.eve.domain.enumeration.TransactionType.KILL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.github.jhipster.config.JHipsterConstants;

@Service
public class PayoutCalculator {

    private static final long FINAL_BLOW_BONUS = 2;
    private final KillmailRepository killmailRepository;
    private final UserRepository userRepository;
    private final DonationRepository donationRepository;
    private final TransactionRepository transactionRepository;
    private final Environment env;

    @Autowired
    public PayoutCalculator(final KillmailRepository killmailRepository,
                            final UserRepository userRepository,
                            final DonationRepository donationRepository,
                            final TransactionRepository transactionRepository,
                            final Environment env) {
        this.killmailRepository = killmailRepository;
        this.userRepository = userRepository;
        this.donationRepository = donationRepository;
        this.transactionRepository = transactionRepository;
        this.env = env;
    }

    @PostConstruct
    public void init() {
        // dev only
        if (Arrays.asList(env.getActiveProfiles()).contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)) {
            calculatePayouts();
        }
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
        final String month = now.getYear() + "-" + String.format("%02d", now.getMonthValue());
        return donationRepository.findByMonth(month)
                                 .stream()
                                 .mapToDouble(donation -> getRemainingWorth(donation, LocalDate.now()))
                                 .sum();
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

    double getRemainingWorth(final Donation donation, final LocalDate date) {
        final Instant monthBorder = getMonthBorder();
        final int monthLength = date.getMonth().maxLength();
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
