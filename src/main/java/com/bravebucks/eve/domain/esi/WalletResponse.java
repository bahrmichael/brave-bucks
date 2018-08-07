package com.bravebucks.eve.domain.esi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WalletResponse {

    @JsonProperty("date")
    private String date;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("ref_type")
    private String refType;

    @JsonProperty("id")
    private Long id;

    // must stay long for non-systemId contextIds
    @JsonProperty("context_id")
    private Long contextId;

    @JsonProperty("context_id_type")
    private String contextType;

    public String getRefType() {
        return refType;
    }

    public void setRefType(final String refType) {
        this.refType = refType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(final String reason) {
        this.reason = reason;
    }

    public Long getContextId() {
        return contextId;
    }

    public void setContextId(final Long contextId) {
        this.contextId = contextId;
    }

    public String getContextType() {
        return contextType;
    }

    public void setContextType(final String contextType) {
        this.contextType = contextType;
    }

    @Override
    public String toString() {
        return "WalletResponse{" +
               "date='" + date + '\'' +
               ", reason='" + reason + '\'' +
               ", id=" + id +
               ", contextId=" + contextId +
               ", contextType='" + contextType + '\'' +
               '}';
    }
}
