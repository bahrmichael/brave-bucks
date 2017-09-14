package com.buyback.eve.domain;

public class PlayerStats {
    private long coins;
    private long defenseKills;
    private long finalBlows;
    private long potentialPayout;

    public PlayerStats(final long coins, final long defenseKills, final long finalBlows, final long potentialPayout) {
        this.coins = coins;
        this.defenseKills = defenseKills;
        this.finalBlows = finalBlows;
        this.potentialPayout = potentialPayout;
    }

    public long getCoins() {
        return coins;
    }

    public void setCoins(final long coins) {
        this.coins = coins;
    }

    public long getDefenseKills() {
        return defenseKills;
    }

    public void setDefenseKills(final long defenseKills) {
        this.defenseKills = defenseKills;
    }

    public long getFinalBlows() {
        return finalBlows;
    }

    public void setFinalBlows(final long finalBlows) {
        this.finalBlows = finalBlows;
    }

    public long getPotentialPayout() {
        return potentialPayout;
    }

    public void setPotentialPayout(final long potentialPayout) {
        this.potentialPayout = potentialPayout;
    }
}
