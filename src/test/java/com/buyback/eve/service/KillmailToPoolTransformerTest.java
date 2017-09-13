package com.buyback.eve.service;

import com.buyback.eve.domain.Killmail;
import com.buyback.eve.domain.Pool;

import org.junit.Test;
import static org.junit.Assert.*;

public class KillmailToPoolTransformerTest {

    private KillmailToPoolTransformer sut = new KillmailToPoolTransformer();

    @Test
    public void addToPool() {
        final Pool pool = new Pool();
        Killmail killmail1 = new Killmail();
        killmail1.setKillId(1L);

        int before = pool.getPoolPlayers().size();
        sut.addKill(pool, killmail1);
        int after = pool.getPoolPlayers().size();

        assertEquals(before + 1, after);

        Killmail killmail2 = new Killmail();
        killmail2.setKillId(2);

        int before2 = pool.getPoolPlayers().size();
        sut.addKill(pool, killmail2);
        int after2 = pool.getPoolPlayers().size();

        assertEquals(before2, after2);
        assertEquals(2, pool.getPoolPlayers().get(0).getKillmailIds().size());
    }

}
