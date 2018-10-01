package com.bravebucks.eve.domain.zkb;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Participant {
    @JsonProperty("character_id")
    private Integer characterId;

    @JsonProperty("ship_type_id")
    private Integer shipTypeId;

    @JsonProperty("final_blow")
    private boolean finalBlow;

    @JsonProperty("alliance_id")
    private Integer allianceId;

    @JsonProperty("corporation_id")
    private Integer corporationId;

    public Integer getGroupId() {
        return allianceId != null ? allianceId : corporationId;
    }

    public Integer getAllianceId() {
        return allianceId;
    }

    public void setAllianceId(final Integer allianceId) {
        this.allianceId = allianceId;
    }

    public Integer getCorporationId() {
        return corporationId;
    }

    public void setCorporationId(final Integer corporationId) {
        this.corporationId = corporationId;
    }

    public boolean isFinalBlow() {
        return finalBlow;
    }

    public void setFinalBlow(final boolean finalBlow) {
        this.finalBlow = finalBlow;
    }

    public Integer getCharacterId() {
        return characterId;
    }

    public void setCharacterId(final Integer characterId) {
        this.characterId = characterId;
    }

    public Integer getShipTypeId() {
        return shipTypeId;
    }

    public void setShipTypeId(final Integer shipTypeId) {
        this.shipTypeId = shipTypeId;
    }

    @Override
    public String toString() {
        return "Participant{" +
               "characterId=" + characterId +
               ", shipTypeId=" + shipTypeId +
               ", finalBlow=" + finalBlow +
               ", allianceId=" + allianceId +
               ", corporationId=" + corporationId +
               '}';
    }
}
