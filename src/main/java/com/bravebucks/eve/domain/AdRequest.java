package com.bravebucks.eve.domain;

import java.io.Serializable;
import java.util.Objects;

import com.bravebucks.eve.domain.enumeration.AdStatus;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A AdRequest.
 */
@Document(collection = "ad_request")
public class AdRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String id;

    @Field("requester")
    private String requester;

    @Field("service")
    private String service;

    @Field("month")
    private String month;

    @Field("description")
    private String description;

    @Field("link")
    private String link;

    @Field("ad_status")
    private AdStatus adStatus;

    // jhipster-needle-entity-add-field - Jhipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequester() {
        return requester;
    }

    public AdRequest requester(String requester) {
        this.requester = requester;
        return this;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getService() {
        return service;
    }

    public AdRequest service(String service) {
        this.service = service;
        return this;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMonth() {
        return month;
    }

    public AdRequest month(String month) {
        this.month = month;
        return this;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDescription() {
        return description;
    }

    public AdRequest description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public AdRequest link(String link) {
        this.link = link;
        return this;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public AdStatus getAdStatus() {
        return adStatus;
    }

    public AdRequest adStatus(AdStatus adStatus) {
        this.adStatus = adStatus;
        return this;
    }

    public void setAdStatus(AdStatus adStatus) {
        this.adStatus = adStatus;
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
        AdRequest adRequest = (AdRequest) o;
        if (adRequest.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), adRequest.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "AdRequest{" +
            "id=" + getId() +
            ", requester='" + getRequester() + "'" +
            ", service='" + getService() + "'" +
            ", month='" + getMonth() + "'" +
            ", description='" + getDescription() + "'" +
            ", link='" + getLink() + "'" +
            ", adStatus='" + getAdStatus() + "'" +
            "}";
    }
}
