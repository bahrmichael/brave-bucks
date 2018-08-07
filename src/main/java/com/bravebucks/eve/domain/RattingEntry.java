package com.bravebucks.eve.domain;

import java.time.Instant;

public class RattingEntry {

    private String id;
    private Long journalId;
    private String userId;
    private Integer characterId;
    private Integer killCount;
    private Integer systemId;
    private Instant instant;
    private Integer adm;
    private boolean processed;

    public RattingEntry() {
    }

    public RattingEntry(final Long journalId, final String userId, final Integer characterId, final Integer killCount,
                        final Integer systemId,
                        final Instant instant, final Integer adm) {
        this.journalId = journalId;
        this.userId = userId;
        this.characterId = characterId;
        this.killCount = killCount;
        this.systemId = systemId;
        this.instant = instant;
        this.adm = adm;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(final boolean processed) {
        this.processed = processed;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Long getJournalId() {
        return journalId;
    }

    public void setJournalId(final Long journalId) {
        this.journalId = journalId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public Integer getCharacterId() {
        return characterId;
    }

    public void setCharacterId(final Integer characterId) {
        this.characterId = characterId;
    }

    public Integer getKillCount() {
        return killCount;
    }

    public void setKillCount(final Integer killCount) {
        this.killCount = killCount;
    }

    public Integer getSystemId() {
        return systemId;
    }

    public void setSystemId(final Integer systemId) {
        this.systemId = systemId;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(final Instant instant) {
        this.instant = instant;
    }

    public Integer getAdm() {
        return adm;
    }

    public void setAdm(final Integer adm) {
        this.adm = adm;
    }
}
