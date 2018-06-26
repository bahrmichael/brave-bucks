package com.bravebucks.eve.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import com.bravebucks.eve.domain.User;
import com.bravebucks.eve.repository.UserRepository;
import com.bravebucks.eve.domain.Killmail;
import com.bravebucks.eve.domain.SolarSystem;
import com.bravebucks.eve.repository.KillmailRepository;
import com.bravebucks.eve.repository.SolarSystemRepository;
import com.mashape.unirest.http.JsonNode;
import static com.bravebucks.eve.service.KillmailPuller.HOUR;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class KillmailPullerTest {

    private KillmailRepository killmailRepo = mock(KillmailRepository.class);
    private UserRepository userRepo = mock(UserRepository.class);
    private JsonRequestService requestService = mock(JsonRequestService.class);
    private SolarSystemRepository solarSystemRepository = mock(SolarSystemRepository.class);
    private KillmailParser killmailParser = mock(KillmailParser.class);
    private Environment env = mock(Environment.class);
    private KillmailPuller sut = Mockito.spy(new KillmailPuller(killmailRepo, userRepo, requestService, solarSystemRepository,
                                                                killmailParser, env));

    @Test
    public void longPull() throws Exception {
        doNothing().when(sut).pullKillmails(anyLong());
        sut.longPull();
        verify(sut).pullKillmails(24 * 7 * HOUR);
    }

    @Test
    public void isVictimNotBrave_true() throws Exception {
        final Killmail killmail = new Killmail();
        killmail.setVictimGroupName("notBrave");
        assertTrue(sut.isVictimNotBrave(killmail));
    }

    @Test
    public void isVictimNotBrave_false() throws Exception {
        final Killmail killmail = new Killmail();
        killmail.setVictimGroupName("Brave Collective");
        assertFalse(sut.isVictimNotBrave(killmail));
    }

    @Test
    public void isInBraveSystem_true() throws Exception {
        SolarSystem system = new SolarSystem();
        system.setSystemId(1L);
        when(solarSystemRepository.findAll()).thenReturn(singletonList(system));
        final Killmail killmail = new Killmail();
        killmail.setSolarSystemId(1L);

        sut.setSystems(singletonList(1L));
        assertTrue(sut.isInBraveSystem(killmail));
    }

    @Test
    public void isInBraveSystem_false() throws Exception {
        final Killmail killmail = new Killmail();
        killmail.setSolarSystemId(1);

        sut.setSystems(singletonList(2L));
        assertFalse(sut.isInBraveSystem(killmail));
    }

    @Test
    public void isNotAnEmptyPod_true() throws Exception {
        final Killmail killmail = new Killmail();
        assertTrue(sut.isNotAnEmptyPod(killmail));
    }

    @Test
    public void isNotAnEmptyPod_false() throws Exception {
        final Killmail killmail = new Killmail();
        killmail.setTotalValue(10_000L);
        assertFalse(sut.isNotAnEmptyPod(killmail));
    }

    @Test
    public void isNotInFleet() {
        final Killmail killmail = new Killmail();
        killmail.setAttackerIds(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L));
        assertTrue(sut.isNotInFleet(killmail));
    }

    @Test
    public void filterAndSaveKillmails() {
        when(killmailRepo.save(anyList())).thenReturn(null);
        when(killmailRepo.findByKillId(2L)).thenReturn(Optional.empty());

        final Killmail killmail = new Killmail();
        killmail.setVictimGroupName("test");
        killmail.setSolarSystemId(1L);
        killmail.setKillId(2L);
        final List<Killmail> killmails = singletonList(killmail);

        sut.setSystems(singletonList(1L));
        sut.filterKillmails(killmails);

        verify(killmailRepo, never()).save(anyList());
    }

    @Test
    public void pullKillmails_userWithoutCharacterId_doesNothing() throws Exception {
        when(userRepo.findAll()).thenReturn(singletonList(new User()));

        sut.pullKillmails(1L);

        verify(requestService, never()).getKillmails(null, 1L);
    }

    @Test
    public void pullKillmails_requestReturnsNothing_doesNothing() throws Exception {
        User user = new User();
        user.setCharacterId(2L);
        when(userRepo.findAll()).thenReturn(singletonList(user));
        when(requestService.getKillmails(2L, 1L)).thenReturn(Optional.empty());

        sut.pullKillmails(1L);

        verify(killmailRepo, never()).save(anyList());
    }

    @Test
    public void pullKillmails_requestReturnsEmptyArray_doesntSaveAny() throws Exception {
        User user = new User();
        user.setCharacterId(2L);
        when(userRepo.findAll()).thenReturn(singletonList(user));
        when(requestService.getKillmails(2L, 1L)).thenReturn(Optional.of(new JsonNode("[]")));

        sut.pullKillmails(1L);

        verify(killmailRepo, never()).save(anyList());
    }

    @Test
    public void pullKillmails_requestReturnsSomething_doesSave() {
        User user = new User();
        user.setCharacterId(2L);
        when(userRepo.findAll()).thenReturn(singletonList(user));
        when(requestService.getKillmails(2L, 1L)).thenReturn(Optional.of(new JsonNode("[{}]")));
        when(sut.filterKillmails(emptyList())).thenReturn(emptyList());
        when(killmailRepo.save(anyList())).thenReturn(null);

        sut.pullKillmails(1L);

        verify(sut).filterKillmails(emptyList());
    }
}
