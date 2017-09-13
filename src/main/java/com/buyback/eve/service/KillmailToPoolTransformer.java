package com.buyback.eve.service;

import com.buyback.eve.domain.Killmail;
import com.buyback.eve.domain.Pool;

public class KillmailToPoolTransformer {

    void addKill(final Pool pool, final Killmail killmail) {
        if (pool.hasPlayer(killmail.getCharacterId())) {
            pool.addKillmailWithExistingPlayer(killmail);
        } else {
            pool.addPlayerWithKillmail(killmail);
        }
    }
}
