package com.bravebucks.eve.domain.zkb;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Killmail {
    @JsonProperty("killmail_id")
    private Long killmailId;
    @JsonProperty("killmail_time")
    private String killmailTime;
    @JsonProperty("solar_system_id")
    private Integer solarSystemId;
    private Participant[] attackers;
    private Participant victim;

    public Long getKillmailId() {
        return killmailId;
    }

    public void setKillmailId(final Long killmailId) {
        this.killmailId = killmailId;
    }

    public String getKillmailTime() {
        return killmailTime;
    }

    public void setKillmailTime(final String killmailTime) {
        this.killmailTime = killmailTime;
    }

    public Integer getSolarSystemId() {
        return solarSystemId;
    }

    public void setSolarSystemId(final Integer solarSystemId) {
        this.solarSystemId = solarSystemId;
    }

    public Participant[] getAttackers() {
        return attackers;
    }

    public void setAttackers(final Participant[] attackers) {
        this.attackers = attackers;
    }

    public Participant getVictim() {
        return victim;
    }

    public void setVictim(final Participant victim) {
        this.victim = victim;
    }

    @Override
    public String toString() {
        return "Killmail{" +
               "killmailId=" + killmailId +
               ", killmailTime='" + killmailTime + '\'' +
               ", solarSystemId=" + solarSystemId +
               ", attackers=" + Arrays.toString(attackers) +
               ", victim=" + victim +
               '}';
    }
}
