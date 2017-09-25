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

    public boolean hasPlayer(final long characterId, final Pool pool) {
        for (PoolPlayer poolPlayer : pool.getPoolPlayers()) {
            if (characterId == poolPlayer.getCharacterId()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasKillmail(final long needle, final Pool pool) {
        for (PoolPlayer poolPlayer : pool.getPoolPlayers()) {
            for (Long killmailId : poolPlayer.getKillmailIds()) {
                if (killmailId == needle) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addKillmailIfNotExists(final Killmail killmail) {
        // todo: change killmail entity to take actual java date
        String yearMonth = getYearMonth(DateUtil.getLocalDate(killmail.getKillTime()));
        Pool pool = getPool(yearMonth);
        if (!hasKillmail(killmail.getKillId(), pool)) {
            killmail.getAttackerIds().stream().filter(this::attackerSignedUp).forEach(attackerId -> addPoolDataForAttacker(killmail, pool, attackerId));
        }
        repository.save(pool);
    }

    public Pool getPool(final String yearMonth) {
        Optional<Pool> optional = repository.findByYearMonth(yearMonth);
        Pool pool;
        if (!optional.isPresent()) {
            pool = new Pool();
            pool.setBalance(0L);
            pool.setYearMonth(yearMonth);
            repository.save(pool);
        } else {
            pool = optional.get();
        }
        return pool;
    }

    public void addPoolDataForAttacker(final Killmail killmail, final Pool pool, final Long attackerId) {
        long coins = calculateCoins(killmail, attackerId);
        if (hasPlayer(attackerId, pool)) {
            for (PoolPlayer poolPlayer : pool.getPoolPlayers()) {
                if (Objects.equals(attackerId, poolPlayer.getCharacterId())) {
                    poolPlayer.setCoins(poolPlayer.getCoins() + coins);
                    poolPlayer.addKillmailId(killmail.getKillId());
                    break;
                }
            }
        } else {
            final PoolPlayer poolPlayer = new PoolPlayer();
            poolPlayer.setCharacterId(attackerId);
            poolPlayer.setCoins(coins);
            poolPlayer.addKillmailId(killmail.getKillId());
            pool.getPoolPlayers().add(poolPlayer);
        }
        pool.setClaimedCoins(pool.getClaimedCoins() + coins);
    }

    public boolean attackerSignedUp(final Long attackerId) {
        return userRepository.findOneByCharacterId(attackerId).isPresent();
    }
}
