package com.buyback.eve.domain;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

public class PoolTest {
    @Test
    public void hasPlayer_withNoPlayer() throws Exception {
        Pool pool = new Pool();
        assertFalse(pool.hasPlayer(1L));
    }

    @Test
    public void hasPlayer_withPlayer() throws Exception {
        Pool pool = new Pool();
        final List<PoolPlayer> players = new ArrayList<>();
        PoolPlayer player = new PoolPlayer();
        player.setCharacterId(1L);
        players.add(player);
        pool.setPoolPlayers(players);

        assertTrue(pool.hasPlayer(1L));
    }

    @Test
    public void addPlayerWithKillmail() throws Exception {
        Pool pool = new Pool();

        final Killmail killmail = new Killmail();
        killmail.setCharacterId(1L);
        killmail.setKillId(2L);
        killmail.setPoints(10L);

        pool.addKillmailIfNotExists(killmail);

        assertTrue(pool.hasPlayer(1L));
        assertEquals(1L, pool.getPoolPlayers().get(0).getCharacterId().longValue());
        assertEquals(2L, pool.getPoolPlayers().get(0).getKillmailIds().get(0).longValue());
        assertEquals(3L, pool.getPoolPlayers().get(0).getCoins().longValue());
    }

    @Test
    public void addToPool() {
        final Pool pool = new Pool();
        Killmail killmail1 = new Killmail();
        killmail1.setKillId(1L);

        int before = pool.getPoolPlayers().size();
        pool.addKillmailIfNotExists(killmail1);
        int after = pool.getPoolPlayers().size();

        assertEquals(before + 1, after);

        Killmail killmail2 = new Killmail();
        killmail2.setKillId(2L);

        pool.addKillmailIfNotExists(killmail2);

        assertEquals("No new player should have been added.", after, pool.getPoolPlayers().size());
        assertEquals(2, pool.getPoolPlayers().get(0).getKillmailIds().size());
    }
}
