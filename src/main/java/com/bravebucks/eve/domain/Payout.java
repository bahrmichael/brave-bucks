package com.bravebucks.eve.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import com.bravebucks.eve.domain.enumeration.PayoutStatus;

/**
 * A Payout.
 */
@Document(collection = "payout")
public class Payout implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String id;

    @Field("user")
    private String user;

    @Field("amount")
    private Double amount;

    @Field("last_updated")
    private Instant lastUpdated;

    @Field("last_modified_by")
    private String lastModifiedBy;

    @Field("status")
    private PayoutStatus status;

    @Field("details")
    private String details;

    public Payout() {
    }

    public Payout(final String user, final Double amount, final String lastModifiedBy, final PayoutStatus status,
                  final String details) {
        this.user = user;
        this.amount = amount;
        this.lastModifiedBy = lastModifiedBy;
        this.status = status;
        this.details = details;
        lastUpdated = Instant.now();
    }

    // jhipster-needle-entity-add-field - Jhipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public Payout user(String user) {
        this.user = user;
        return this;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Double getAmount() {
        return amount;
    }

    public Payout amount(Double amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public Payout lastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public Payout lastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public PayoutStatus getStatus() {
        return status;
    }

    public Payout status(PayoutStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(PayoutStatus status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public Payout details(String details) {
        this.details = details;
        return this;
    }

    public void setDetails(String details) {
        this.details = details;
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
        Payout payout = (Payout) o;
        if (payout.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), payout.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Payout{" +
            "id=" + getId() +
            ", user='" + getUser() + "'" +
            ", amount='" + getAmount() + "'" +
            ", lastUpdated='" + getLastUpdated() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", status='" + getStatus() + "'" +
            ", details='" + getDetails() + "'" +
            "}";
    }
}
