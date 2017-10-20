package com.buyback.eve.web.rest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.buyback.eve.domain.Killmail;
import com.buyback.eve.domain.PlayerStats;
import com.buyback.eve.domain.User;
import com.buyback.eve.repository.KillmailRepository;
import com.buyback.eve.repository.UserRepository;
import com.buyback.eve.service.PlayerStatsService;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlayerStatsResourceTest {

    private PlayerStatsService service = mock(PlayerStatsService.class);
    private KillmailRepository killmailRepo = mock(KillmailRepository.class);
    private UserRepository userRepo = mock(UserRepository.class);
    private PlayerStatsResource sut = new PlayerStatsResource(service, killmailRepo, userRepo);

    private final PlayerStats stats = new PlayerStats(1L, 1L, 1L, 1L);

    @Before
    public void before() {
        when(service.getStatsForCurrentUser()).thenReturn(stats);
    }

    @Test
    public void getPlayerStats() throws Exception {
        ResponseEntity playerStats = sut.getPlayerStats();

        assertEquals(HttpStatus.OK, playerStats.getStatusCode());
        assertEquals(stats, playerStats.getBody());
    }

    @Test
    public void getPotentialPayout() throws Exception {
        ResponseEntity playerStats = sut.getPotentialPayout();

        assertEquals(HttpStatus.OK, playerStats.getStatusCode());
        assertEquals(0.0, playerStats.getBody());
    }

    @Test
    public void getKillmails_withNonResolvableUser() throws Exception {
        when(userRepo.findOneByLogin(anyString())).thenReturn(Optional.empty());

        ResponseEntity responseEntity = sut.getKillmails();
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void getKillmails_withUser_withoutKillmails() throws Exception {
        User user = new User();
        user.setCharacterId(1L);
        when(userRepo.findOneByLogin(anyString())).thenReturn(Optional.of(user));
        when(killmailRepo.findByAttackerId(1L)).thenReturn(Collections.emptyList());

        ResponseEntity responseEntity = sut.getKillmails();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof List);
        assertTrue(((List) responseEntity.getBody()).isEmpty());
    }

    @Test
    public void getKillmails_withUser_withKillmails() throws Exception {
        User user = new User();
        user.setCharacterId(1L);
        when(userRepo.findOneByLogin(anyString())).thenReturn(Optional.of(user));
        when(killmailRepo.findByAttackerId(1L)).thenReturn(Collections.singletonList(new Killmail()));

        ResponseEntity responseEntity = sut.getKillmails();
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody() instanceof List);
        assertFalse(((List) responseEntity.getBody()).isEmpty());
    }
}
