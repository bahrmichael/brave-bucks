package com.bravebucks.eve.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A Donation.
 */
@Document(collection = "donation")
public class Donation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String id;

    @Field("donater")
    private String donater;

    @Field("month")
    private String month;

    @Field("amount")
    private Double amount;

    @Field("created")
    private Instant created;

    // jhipster-needle-entity-add-field - Jhipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDonater() {
        return donater;
    }

    public Donation donater(String donater) {
        this.donater = donater;
        return this;
    }

    public void setDonater(String donater) {
        this.donater = donater;
    }

    public String getMonth() {
        return month;
    }

    public Donation month(String month) {
        this.month = month;
        return this;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Double getAmount() {
        return amount;
    }

    public Donation amount(Double amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(final Instant created) {
        this.created = created;
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
        Donation donation = (Donation) o;
        if (donation.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), donation.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Donation{" +
            "id=" + getId() +
            ", donater='" + getDonater() + "'" +
            ", month='" + getMonth() + "'" +
            ", amount='" + getAmount() + "'" +
            "}";
    }
}
