package com.bravebucks.eve.domain;

public class EveCharacter {
    private int id;
    private String name;
    private String walletReadRefreshToken;
    private String owningUser;
    private String walletJournalEtag;

    public EveCharacter() {
    }

    public EveCharacter(final int id, final String name, final String walletReadRefreshToken, final String owningUser) {
        this.id = id;
        this.name = name;
        this.walletReadRefreshToken = walletReadRefreshToken;
        this.owningUser = owningUser;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getWalletReadRefreshToken() {
        return walletReadRefreshToken;
    }

    public void setWalletReadRefreshToken(final String walletReadRefreshToken) {
        this.walletReadRefreshToken = walletReadRefreshToken;
    }

    public String getOwningUser() {
        return owningUser;
    }

    public void setOwningUser(final String owningUser) {
        this.owningUser = owningUser;
    }

    public String getWalletJournalEtag() {
        return walletJournalEtag;
    }

    public void setWalletJournalEtag(final String walletJournalEtag) {
        this.walletJournalEtag = walletJournalEtag;
    }
}
