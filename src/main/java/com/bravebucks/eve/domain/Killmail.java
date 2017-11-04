package com.bravebucks.eve.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "killmail")
public class Killmail {
    @Id
    private long killId;
    private long solarSystemId;
    private String killTime;
    private long totalValue;
    private long points;
    private boolean npc;
    private long victimId;
    private String victimGroupName;
    private List<Long> attackerIds = new ArrayList<>();
    private long finalBlowAttackerId;
    private boolean payoutCalculated;
    private long shipTypeId;

    public Killmail() {
    }

    public void setPayoutCalculated(final boolean payoutCalculated) {
        this.payoutCalculated = payoutCalculated;
    }

    public boolean isPayoutCalculated() {
        return payoutCalculated;
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

    public List<Long> getAttackerIds() {
        return attackerIds;
    }

    public void setAttackerIds(final List<Long> attackerIds) {
        this.attackerIds = attackerIds;
    }

    public void addAttackerId(long attackerId) {
        this.attackerIds.add(attackerId);
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

    public void setVictimGroupName(final String victimGroupName) {
        this.victimGroupName = victimGroupName;
    }

    public String getVictimGroupName() {
        return victimGroupName;
    }

    public long getFinalBlowAttackerId() {
        return finalBlowAttackerId;
    }

    public void setFinalBlowAttackerId(final long finalBlowAttackerId) {
        this.finalBlowAttackerId = finalBlowAttackerId;
    }

    public void setShipTypeId(final long shipTypeId) {
        this.shipTypeId = shipTypeId;
    }

    public long getShipTypeId() {
        return shipTypeId;
    }
}
