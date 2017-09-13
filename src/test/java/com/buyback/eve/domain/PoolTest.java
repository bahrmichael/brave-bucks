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

        pool.addPlayerWithKillmail(killmail);

        assertTrue(pool.hasPlayer(1L));
        assertTrue(pool.getPoolPlayers().get(0).getCharacterId().equals(1L));
        assertTrue(pool.getPoolPlayers().get(0).getKillmailIds().get(0).equals(2L));
        assertTrue(pool.getPoolPlayers().get(0).getCoins().equals(10L));
    }
}
