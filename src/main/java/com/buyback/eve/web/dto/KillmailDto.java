package com.buyback.eve.web.dto;

public class KillmailDto {
    private long coins;
    private long killmailId;
    private String killTime;
    private String victimAlliance;

    public void setCoins(final long coins) {
        this.coins = coins;
    }

    public long getCoins() {
        return coins;
    }

    public void setKillmailId(final long killmailId) {
        this.killmailId = killmailId;
    }

    public long getKillmailId() {
        return killmailId;
    }

    public void setKillTime(final String killTime) {
        this.killTime = killTime;
    }

    public void setVictimAlliance(final String victimAlliance) {
        this.victimAlliance = victimAlliance;
    }

    public String getVictimAlliance() {
        return victimAlliance;
    }

    public String getKillTime() {
        return killTime;
    }
}
