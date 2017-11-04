package com.bravebucks.eve.web.dto;

import java.time.Instant;

public class KillmailDto {
    private long killmailId;
    private Instant killTime;
    private String victimAlliance;
    private long shipTypeId;

    public long getShipTypeId() {
        return shipTypeId;
    }

    public void setShipTypeId(final long shipTypeId) {
        this.shipTypeId = shipTypeId;
    }

    public void setKillmailId(final long killmailId) {
        this.killmailId = killmailId;
    }

    public long getKillmailId() {
        return killmailId;
    }

    public void setKillTime(final Instant killTime) {
        this.killTime = killTime;
    }

    public void setVictimAlliance(final String victimAlliance) {
        this.victimAlliance = victimAlliance;
    }

    public String getVictimAlliance() {
        return victimAlliance;
    }

    public Instant getKillTime() {
        return killTime;
    }
}
