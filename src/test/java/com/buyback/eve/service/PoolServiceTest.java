package com.buyback.eve.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.buyback.eve.domain.Killmail;
import com.buyback.eve.domain.Pool;
import com.buyback.eve.domain.PoolPlayer;
import com.buyback.eve.domain.User;
import com.buyback.eve.repository.PoolRepository;
import com.buyback.eve.repository.UserRepository;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        ArgumentCaptor<Pool> tArgumentCaptor = ArgumentCaptor.forClass(Pool.class);
        when(repo.save(tArgumentCaptor.capture())).thenReturn(null);
        Pool value = new Pool();
        value.setYearMonth("2017-07");
        when(repo.findByYearMonth("2017-07")).thenReturn(Optional.of(value));
        when(userRepo.findOneByCharacterId(2L)).thenReturn(Optional.of(new User()));

        final Killmail killmail = new Killmail();
        killmail.setKillTime("2017-07-01 10:00:00");
        killmail.setKillId(1);
        killmail.setPoints(1);
        killmail.setAttackerIds(Collections.singletonList(2L));

        sut.addKillmailIfNotExists(killmail);

        Pool pool = tArgumentCaptor.getValue();
        assertEquals(1, pool.getClaimedCoins().intValue());
        assertEquals(1, pool.getPoolPlayers().size());
        assertEquals("2017-07", pool.getYearMonth());
    }

    @Test
    public void getPool_alreadyExists() throws Exception {
        Pool pool = new Pool();
        when(repo.findByYearMonth(anyString())).thenReturn(Optional.of(pool));

        Pool result = sut.getPool("test");

        assertEquals(pool, result);
    }

    @Test
    public void getPool_newPool() throws Exception {
        when(repo.findByYearMonth(anyString())).thenReturn(Optional.empty());
        when(repo.save(any(Pool.class))).thenReturn(null);

        Pool result = sut.getPool("test");

        assertEquals(0L, result.getBalance().longValue());
        assertEquals("test", result.getYearMonth());

        verify(repo).save(any(Pool.class));
    }

    @Test
    public void addPoolDataForAttacker_withExistingPlayer() throws Exception {
        final PoolPlayer existingPlayer = new PoolPlayer();
        existingPlayer.setCoins(10L);
        existingPlayer.setCharacterId(1L);
        final Pool pool = new Pool();
        pool.setPoolPlayers(Collections.singletonList(existingPlayer));
        final Killmail killmail = new Killmail();
        killmail.setPoints(100);

        sut.addPoolDataForAttacker(killmail, pool, 1L);

        assertEquals(20, existingPlayer.getCoins().intValue());
    }

    @Test
    public void addPoolDataForAttacker_withoutExistingPlayer() throws Exception {
        final Pool pool = new Pool();
        final Killmail killmail = new Killmail();
        killmail.setPoints(100);

        sut.addPoolDataForAttacker(killmail, pool, 1L);

        assertEquals(10, pool.getPoolPlayers().get(0).getCoins().intValue());
    }

    @Test
    public void attackerSignedUp_true() throws Exception {
        when(userRepo.findOneByCharacterId(anyLong())).thenReturn(Optional.of(new User()));

        assertTrue(sut.attackerSignedUp(1L));
    }

    @Test
    public void attackerSignedUp_false() throws Exception {
        when(userRepo.findOneByCharacterId(anyLong())).thenReturn(Optional.empty());

        assertFalse(sut.attackerSignedUp(1L));
    }

    @Test
    public void addNewPlayerToPool() throws Exception {
        final Killmail killmail = new Killmail();
        killmail.setKillId(1L);
        final Pool pool = new Pool();
        final Long attackerId = 2L;
        final long coins = 3L;

        sut.addNewPlayerToPool(killmail, pool, attackerId, coins);

        assertEquals(1, pool.getPoolPlayers().size());
        PoolPlayer poolPlayer = pool.getPoolPlayers().get(0);
        assertEquals(attackerId, poolPlayer.getCharacterId());
        assertEquals(coins, poolPlayer.getCoins().longValue());
        assertTrue(poolPlayer.getKillmailIds().contains(1L));
    }

    @Test
    public void addKillmailToExistingPlayers_withMultiplePlayers() throws Exception {
        Pool pool = new Pool();
        final List<PoolPlayer> poolPlayers = new ArrayList<>();
        poolPlayers.add(createPoolPlayer(1L));
        poolPlayers.add(createPoolPlayer(2L));
        pool.setPoolPlayers(poolPlayers);

        final Killmail killmail = new Killmail();
        killmail.setKillId(5);

        sut.addKillmailToExistingPlayers(killmail, pool, 1L, 10);

        assertEquals(2, pool.getPoolPlayers().size());
        PoolPlayer player = pool.getPoolPlayers().get(0);
        assertEquals(10, player.getCoins().intValue());
        assertTrue(player.getKillmailIds().contains(5L));
    }

    private PoolPlayer createPoolPlayer(final long characterId) {
        PoolPlayer player = new PoolPlayer();
        player.setCharacterId(characterId);
        player.setCoins(0L);
        return player;
    }

}
