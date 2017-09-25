package com.buyback.eve.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.buyback.eve.domain.Pool;
import com.buyback.eve.domain.PoolPlayer;
import com.buyback.eve.repository.PoolRepository;
import com.buyback.eve.repository.UserRepository;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class PoolServiceTest {

    private PoolRepository repo = mock(PoolRepository.class);
    private UserRepository userRepo = mock(UserRepository.class);
    private PoolService sut = new PoolService(repo, userRepo);

    @Test
    public void hasPlayer_false() throws Exception {
        final List<PoolPlayer> poolPlayers = new ArrayList<>();
        Pool pool = new Pool();
        pool.setPoolPlayers(poolPlayers);

        assertFalse(sut.hasPlayer(1L, pool));
    }

    @Test
    public void hasPlayer_true() throws Exception {
        final PoolPlayer poolPlayer = new PoolPlayer();
        poolPlayer.setCharacterId(1L);
        final List<PoolPlayer> poolPlayers = new ArrayList<>();
        poolPlayers.add(poolPlayer);
        Pool pool = new Pool();
        pool.setPoolPlayers(poolPlayers);

        assertTrue(sut.hasPlayer(1L, pool));
    }

    @Test
    public void hasKillmail_true() throws Exception {
        final PoolPlayer poolPlayer = new PoolPlayer();
        poolPlayer.setKillmailIds(Collections.singletonList(1L));
        final List<PoolPlayer> poolPlayers = new ArrayList<>();
        poolPlayers.add(poolPlayer);
        Pool pool = new Pool();
        pool.setPoolPlayers(poolPlayers);

        assertTrue(sut.hasKillmail(1L, pool));
    }

    @Test
    public void hasKillmail_false() throws Exception {
        final PoolPlayer poolPlayer = new PoolPlayer();
        poolPlayer.setKillmailIds(Collections.emptyList());
        final List<PoolPlayer> poolPlayers = new ArrayList<>();
        poolPlayers.add(poolPlayer);
        Pool pool = new Pool();
        pool.setPoolPlayers(poolPlayers);

        assertFalse(sut.hasKillmail(1L, pool));
    }

    @Test
    public void addKillmailIfNotExists() throws Exception {
    }

    @Test
    public void getPool() throws Exception {
    }

    @Test
    public void addPoolDataForAttacker() throws Exception {
    }

    @Test
    public void attackerSignedUp() throws Exception {
    }
}
