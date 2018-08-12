package com.bravebucks.eve.domain.esi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterInfoResponse {

    @JsonProperty("alliance_id")
    private Integer allianceId;

    public Integer getAllianceId() {
        return allianceId;
    }

    public void setAllianceId(final Integer allianceId) {
        this.allianceId = allianceId;
    }
}
