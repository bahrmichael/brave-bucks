package com.buyback.eve.domain;

import java.util.ArrayList;
import java.util.List;

import static com.buyback.eve.service.KillmailParser.calculateCoins;

import org.springframework.data.annotation.Id;

public class Pool {

    @Id
    private String yearMonth;
    private Long balance;
    private Long claimedCoins;
    private List<PoolPlayer> poolPlayers;

    public Pool() {
        poolPlayers = new ArrayList<>();
        balance = 0L;
        claimedCoins = 0L;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(final String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(final Long balance) {
        this.balance = balance;
    }

    public Long getClaimedCoins() {
        return claimedCoins;
    }

    public void setClaimedCoins(final Long claimedCoins) {
        this.claimedCoins = claimedCoins;
    }

    public List<PoolPlayer> getPoolPlayers() {
        return poolPlayers;
    }

    public void setPoolPlayers(final List<PoolPlayer> poolPlayers) {
        this.poolPlayers = poolPlayers;
    }

    public boolean hasPlayer(final long characterId) {
        for (PoolPlayer poolPlayer : poolPlayers) {
            if (characterId == poolPlayer.getCharacterId()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasKillmail(final long needle) {
        for (PoolPlayer poolPlayer : poolPlayers) {
            for (Long killmailId : poolPlayer.getKillmailIds()) {
                if (killmailId == needle) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addKillmailIfNotExists(final Killmail killmail) {
        if (!hasKillmail(killmail.getKillId())) {
            long coins = calculateCoins(killmail);
            if (hasPlayer(killmail.getCharacterId())) {
                for (PoolPlayer poolPlayer : poolPlayers) {
                    if (killmail.getCharacterId() == poolPlayer.getCharacterId()
                        && !poolPlayer.getKillmailIds().contains(killmail.getKillId())) {
                            poolPlayer.setCoins(poolPlayer.getCoins() + coins);
                            poolPlayer.addKillmailId(killmail.getKillId());
                            break;
                    }
                }
            } else {
                final PoolPlayer poolPlayer = new PoolPlayer();
                poolPlayer.setCharacterId(killmail.getCharacterId());
                poolPlayer.setCoins(coins);
                poolPlayer.addKillmailId(killmail.getKillId());
                poolPlayers.add(poolPlayer);
            }
            claimedCoins += coins;
        }
    }
}
