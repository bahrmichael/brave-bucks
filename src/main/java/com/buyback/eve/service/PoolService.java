package com.buyback.eve.service;

import java.util.Objects;
import java.util.Optional;

import com.buyback.eve.domain.Killmail;
import com.buyback.eve.domain.Pool;
import com.buyback.eve.domain.PoolPlayer;
import com.buyback.eve.repository.PoolRepository;
import com.buyback.eve.repository.UserRepository;
import static com.buyback.eve.service.DateUtil.getYearMonth;
import static com.buyback.eve.service.KillmailParser.calculateCoins;

import org.springframework.stereotype.Service;

@Service
public class PoolService {

    private final PoolRepository repository;
    private final UserRepository userRepository;

    public PoolService(final PoolRepository repository, final UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    boolean hasPlayer(final long characterId, final Pool pool) {
        for (PoolPlayer poolPlayer : pool.getPoolPlayers()) {
            if (characterId == poolPlayer.getCharacterId()) {
                return true;
            }
        }
        return false;
    }

    boolean hasKillmail(final long needle, final Pool pool) {
        for (PoolPlayer poolPlayer : pool.getPoolPlayers()) {
            for (Long killmailId : poolPlayer.getKillmailIds()) {
                if (killmailId == needle) {
                    return true;
                }
            }
        }
        return false;
    }

    void addKillmailIfNotExists(final Killmail killmail) {
        // todo: change killmail entity to take actual java date
        String yearMonth = getYearMonth(DateUtil.getLocalDate(killmail.getKillTime()));
        Pool pool = getPool(yearMonth);
        if (!hasKillmail(killmail.getKillId(), pool)) {
            killmail.getAttackerIds().stream().filter(this::attackerSignedUp).forEach(attackerId -> addPoolDataForAttacker(killmail, pool, attackerId));
        }
        repository.save(pool);
    }

    Pool getPool(final String yearMonth) {
        Optional<Pool> optional = repository.findByYearMonth(yearMonth);
        Pool pool;
        if (!optional.isPresent()) {
            pool = new Pool();
            pool.setYearMonth(yearMonth);
            repository.save(pool);
        } else {
            pool = optional.get();
        }
        return pool;
    }

    void addPoolDataForAttacker(final Killmail killmail, final Pool pool, final Long attackerId) {
        long coins = calculateCoins(killmail, attackerId);
        if (hasPlayer(attackerId, pool)) {
            addKillmailToExistingPlayers(killmail, pool, attackerId, coins);
        } else {
            addNewPlayerToPool(killmail, pool, attackerId, coins);
        }
        pool.setClaimedCoins(pool.getClaimedCoins() + coins);
    }

    void addKillmailToExistingPlayers(final Killmail killmail, final Pool pool, final Long attackerId,
                                      final long coins) {
        for (PoolPlayer poolPlayer : pool.getPoolPlayers()) {
            if (Objects.equals(attackerId, poolPlayer.getCharacterId())) {
                poolPlayer.setCoins(poolPlayer.getCoins() + coins);
                poolPlayer.addKillmailId(killmail.getKillId());
                break;
            }
        }
    }

    void addNewPlayerToPool(final Killmail killmail, final Pool pool, final Long attackerId, final long coins) {
        final PoolPlayer poolPlayer = new PoolPlayer();
        poolPlayer.setCharacterId(attackerId);
        poolPlayer.setCoins(coins);
        poolPlayer.addKillmailId(killmail.getKillId());
        pool.addPoolPlayer(poolPlayer);
    }

    boolean attackerSignedUp(final Long attackerId) {
        return userRepository.findOneByCharacterId(attackerId).isPresent();
    }
}
