package com.bravebucks.eve.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.io.Serializable;
import java.util.Objects;

import com.bravebucks.eve.domain.enumeration.Region;

/**
 * A SolarSystem.
 */
@Document(collection = "solar_system")
public class SolarSystem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String id;

    @Field("system_id")
    private Long systemId;

    @Field("system_name")
    private String systemName;

    @Field("region")
    private Region region;

    @Field("track_pvp")
    private Boolean trackPvp;

    @Field("track_ratting")
    private Boolean trackRatting;

    // jhipster-needle-entity-add-field - Jhipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getTrackPvp() {
        return trackPvp;
    }

    public void setTrackPvp(final Boolean trackPvp) {
        this.trackPvp = trackPvp;
    }

    public Boolean getTrackRatting() {
        return trackRatting;
    }

    public void setTrackRatting(final Boolean trackRatting) {
        this.trackRatting = trackRatting;
    }

    public Long getSystemId() {
        return systemId;
    }

    public SolarSystem systemId(Long systemId) {
        this.systemId = systemId;
        return this;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }

    public String getSystemName() {
        return systemName;
    }

    public SolarSystem systemName(String systemName) {
        this.systemName = systemName;
        return this;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public Region getRegion() {
        return region;
    }

    public SolarSystem region(Region region) {
        this.region = region;
        return this;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
    // jhipster-needle-entity-add-getters-setters - Jhipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SolarSystem solarSystem = (SolarSystem) o;
        if (solarSystem.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), solarSystem.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SolarSystem{" +
            "id=" + getId() +
            ", systemId='" + getSystemId() + "'" +
            ", systemName='" + getSystemName() + "'" +
            ", region='" + getRegion() + "'" +
            "}";
    }
}
