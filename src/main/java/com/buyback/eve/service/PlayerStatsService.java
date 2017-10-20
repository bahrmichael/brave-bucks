package com.buyback.eve.service;

import java.time.LocalDate;
import java.util.Optional;

import com.buyback.eve.domain.Payout;
import com.buyback.eve.domain.PlayerStats;
import com.buyback.eve.domain.Pool;
import com.buyback.eve.domain.Transaction;
import com.buyback.eve.domain.User;
import com.buyback.eve.domain.enumeration.PayoutStatus;
import com.buyback.eve.repository.KillmailRepository;
import com.buyback.eve.repository.PayoutRepository;
import com.buyback.eve.repository.PoolRepository;
import com.buyback.eve.repository.TransactionRepository;
import com.buyback.eve.repository.UserRepository;
import com.buyback.eve.security.SecurityUtils;

import static com.buyback.eve.service.DateUtil.getYearMonth;
import static com.buyback.eve.service.KillmailParser.calculateCoins;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerStatsService {

    private final UserRepository userRepository;
    private final KillmailRepository killmailRepository;
    private final PoolRepository poolRepository;
    private final TransactionRepository transactionRepository;
    private final PayoutRepository payoutRepository;

    @Autowired
    public PlayerStatsService(final UserRepository userRepository,
                              final KillmailRepository killmailRepository,
                              final PoolRepository poolRepository,
                              final TransactionRepository transactionRepository,
                              final PayoutRepository payoutRepository) {
        this.userRepository = userRepository;
        this.killmailRepository = killmailRepository;
        this.poolRepository = poolRepository;
        this.transactionRepository = transactionRepository;
        this.payoutRepository = payoutRepository;
    }

    public PlayerStats getStatsForCurrentUser() {
        return getStatsForUser(SecurityUtils.getCurrentUserLogin());
    }

    PlayerStats getStatsForUser(String login) {
        Optional<User> user = userRepository.findOneByLogin(login);
        Optional<Pool> poolOptional = poolRepository.findByYearMonth(getYearMonth(LocalDate.now()));
        if (user.isPresent() && poolOptional.isPresent()) {
            final long[] coins = {0};
            final long[] defenseKills = {0};
            final long[] finalBlows = {0};
            Long characterId = user.get().getCharacterId();
            killmailRepository.findByAttackerId(characterId).stream()
                              .filter(DateUtil::isCurrentMonth)
                              .forEach(killmail -> {
                                  coins[0] += calculateCoins(killmail, characterId);
                                  finalBlows[0] += killmail.getFinalBlowAttackerId() == characterId ? 1 : 0;
                                  defenseKills[0]++;
                              });
            Pool pool = poolOptional.get();
            Long potentialPayout;
            if (pool.getClaimedCoins() != 0) {
                double factor = coins[0] / (double) pool.getClaimedCoins();
                potentialPayout = (long) (pool.getBalance() * factor);
            } else {
                potentialPayout = 0L;
            }
            return new PlayerStats(coins[0], defenseKills[0], finalBlows[0], potentialPayout);
        } else {
            return null;
        }
    }

    public Double getPotentialPayout() {
        return transactionRepository.findAllByUser(SecurityUtils.getCurrentUserLogin()).stream()
            .mapToDouble(Transaction::getAmount).sum()
            - payoutRepository.findAllByUserAndStatus(SecurityUtils.getCurrentUserLogin(), PayoutStatus.REQUESTED)
                        .stream().mapToDouble(Payout::getAmount).sum();
    }
}
