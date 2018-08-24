package com.bravebucks.eve.domain.esi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AdmResponse {

    @JsonProperty("solar_system_id")
    private int solarSystemId;

    @JsonProperty("vulnerability_occupancy_level")
    private double adm;

    public int getSolarSystemId() {
        return solarSystemId;
    }

    public void setSolarSystemId(final int solarSystemId) {
        this.solarSystemId = solarSystemId;
    }

    public double getAdm() {
        return adm;
    }

    public void setAdm(final double adm) {
        this.adm = adm;
    }
}
