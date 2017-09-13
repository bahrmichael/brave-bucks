package com.buyback.eve.domain;

import java.util.ArrayList;
import java.util.List;

public class PoolPlayer {
    private Long characterId;
    private Long coins;
    private List<Long> killmailIds = new ArrayList<>();

    public Long getCharacterId() {
        return characterId;
    }

    public void setCharacterId(final Long characterId) {
        this.characterId = characterId;
    }

    public Long getCoins() {
        return coins;
    }

    public void setCoins(final Long coins) {
        this.coins = coins;
    }

    public List<Long> getKillmailIds() {
        return killmailIds;
    }

    public void setKillmailIds(final List<Long> killmailIds) {
        this.killmailIds = killmailIds;
    }

    void addKillmailId(final long killId) {
        killmailIds.add(killId);
    }
}
