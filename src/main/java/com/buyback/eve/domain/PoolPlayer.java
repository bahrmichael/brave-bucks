package com.buyback.eve.domain;

import java.util.List;

public class PoolPlayer {
    private String player;
    private Long coins;
    private List<Long> killmailIds;

    public String getPlayer() {
        return player;
    }

    public void setPlayer(final String player) {
        this.player = player;
    }

    public Long getCoins() {
        return coins;
    }

    public void setCoins(final Long coins) {
        this.coins = coins;
    }

    public List<Long> getKillmailIds() {
        return killmailIds;
    }

    public void setKillmailIds(final List<Long> killmailIds) {
        this.killmailIds = killmailIds;
    }
}
