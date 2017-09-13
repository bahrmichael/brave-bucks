package com.buyback.eve.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.data.annotation.Id;

public class Pool {

    @Id
    private String yearMonth;
    private Long balance;
    private Long claimedCoins;
    private List<PoolPlayer> poolPlayers = new ArrayList<>();

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

    public void addPlayerWithKillmail(final Killmail killmail) {
        final PoolPlayer poolPlayer = new PoolPlayer();
        poolPlayer.setCharacterId(killmail.getCharacterId());
        poolPlayer.setCoins(killmail.getPoints());
        poolPlayer.addKillmailId(killmail.getKillId());
        poolPlayers.add(poolPlayer);
    }

    public void addKillmailWithExistingPlayer(final Killmail killmail) {
        for (PoolPlayer poolPlayer : poolPlayers) {
            if (killmail.getCharacterId() == poolPlayer.getCharacterId()) {
                poolPlayer.setCoins(poolPlayer.getCoins() + killmail.getPoints());
                poolPlayer.addKillmailId(killmail.getKillId());
            }
        }
    }
}
