package com.bravebucks.eve.domain.esi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CharacterInfoResponse {

    private Integer allianceId;

    public Integer getAllianceId() {
        return allianceId;
    }

    public void setAllianceId(final Integer allianceId) {
        this.allianceId = allianceId;
    }
}
