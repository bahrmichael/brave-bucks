package com.buyback.eve.domain;

import java.util.List;

import org.springframework.data.annotation.Id;

public class Pool {

    @Id
    private String yearMonth;
    private Long balance;
    private Long claimedCoins;
    private List<PoolPlayer> poolPlayers;

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
}
