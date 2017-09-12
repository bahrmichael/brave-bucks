package com.buyback.eve.domain;

public class Killmail {
    private Long characterId;
    private long killId;
    private long solarSystemId;
    private String killTime;
    private long attackerCount;
    private long totalValue;
    private long points;
    private boolean npc;

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(final Long characterId) {
        this.characterId = characterId;
    }

    public long getKillId() {
        return killId;
    }

    public void setKillId(final long killId) {
        this.killId = killId;
    }

    public long getSolarSystemId() {
        return solarSystemId;
    }

    public void setSolarSystemId(final long solarSystemId) {
        this.solarSystemId = solarSystemId;
    }

    public String getKillTime() {
        return killTime;
    }

    public void setKillTime(final String killTime) {
        this.killTime = killTime;
    }

    public long getAttackerCount() {
        return attackerCount;
    }

    public void setAttackerCount(final long attackerCount) {
        this.attackerCount = attackerCount;
    }

    public long getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(final long totalValue) {
        this.totalValue = totalValue;
    }

    public long getPoints() {
        return points;
    }

    public void setPoints(final long points) {
        this.points = points;
    }

    public boolean isNpc() {
        return npc;
    }

    public void setNpc(final boolean npc) {
        this.npc = npc;
    }
}
