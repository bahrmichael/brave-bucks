package com.buyback.eve.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.io.Serializable;
import java.util.Objects;

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

    @Field("sytem_name")
    private String sytemName;

    // jhipster-needle-entity-add-field - Jhipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getSytemName() {
        return sytemName;
    }

    public SolarSystem sytemName(String sytemName) {
        this.sytemName = sytemName;
        return this;
    }

    public void setSytemName(String sytemName) {
        this.sytemName = sytemName;
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
            ", sytemName='" + getSytemName() + "'" +
            "}";
    }
}
