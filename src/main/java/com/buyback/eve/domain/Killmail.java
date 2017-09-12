package com.buyback.eve.domain;

import org.springframework.data.annotation.Id;

public class Killmail {
    @Id
    private long killId;
    private long characterId;
    private long solarSystemId;
    private String killTime;
    private long attackerCount;
    private long totalValue;
    private long points;
    private boolean npc;
    private long victimId;
    private String victimAlliance;

    public long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(final long characterId) {
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

    public long getVictimId() {
        return victimId;
    }

    public void setVictimId(final long victimId) {
        this.victimId = victimId;
    }

    public void setVictimAlliance(final String victimAlliance) {
        this.victimAlliance = victimAlliance;
    }

    public String getVictimAlliance() {
        return victimAlliance;
    }
}
