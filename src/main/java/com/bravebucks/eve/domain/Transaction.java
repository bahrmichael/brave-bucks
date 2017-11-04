package com.bravebucks.eve.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import com.bravebucks.eve.domain.enumeration.TransactionType;

/**
 * A Transaction.
 */
@Document(collection = "transaction")
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String id;

    @Field("user")
    private String user;

    @Field("instant")
    private Instant instant;

    @Field("amount")
    private Double amount;

    @Field("type")
    private TransactionType type;

    public Transaction() {
    }

    public Transaction(final String user, final Double amount, final TransactionType type) {
        this.user = user;
        instant = Instant.now();
        this.amount = amount;
        this.type = type;
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

    public Transaction user(String user) {
        this.user = user;
        return this;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Instant getInstant() {
        return instant;
    }

    public Transaction instant(Instant instant) {
        this.instant = instant;
        return this;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public Double getAmount() {
        return amount;
    }

    public Transaction amount(Double amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public Transaction type(TransactionType type) {
        this.type = type;
        return this;
    }

    public void setType(TransactionType type) {
        this.type = type;
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
        Transaction transaction = (Transaction) o;
        if (transaction.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), transaction.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + getId() +
            ", user='" + getUser() + "'" +
            ", instant='" + getInstant() + "'" +
            ", amount='" + getAmount() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
