package com.buyback.eve.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static java.util.Collections.emptyList;

import com.buyback.eve.domain.Killmail;
import com.buyback.eve.domain.User;
import com.buyback.eve.repository.KillmailRepository;
import com.buyback.eve.repository.UserRepository;
import static com.buyback.eve.service.KillmailPuller.HOUR;

import org.json.JSONArray;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
    private KillmailPuller sut = spy(new KillmailPuller(killmailRepo, userRepo, requestService));

    @Test
    public void pullKillmails() throws Exception {
        doNothing().when(sut).pullKillmails(anyLong());
        sut.pullKillmails();
        verify(sut).pullKillmails(HOUR);
    }

    @Test
    public void init() throws Exception {
        doNothing().when(sut).pullKillmails(anyLong());
        sut.init();
        verify(sut).pullKillmails(HOUR);
    }

    @Test
    public void longPull() throws Exception {
        doNothing().when(sut).pullKillmails(anyLong());
        sut.longPull();
        verify(sut).pullKillmails(24 * 7 * HOUR);
    }

    @Test
    public void isVictimNotBrave_true() throws Exception {
        final Killmail killmail = new Killmail();
        killmail.setVictimAlliance("notBrave");
        assertTrue(sut.isVictimNotBrave(killmail));
    }

    @Test
    public void isVictimNotBrave_false() throws Exception {
        final Killmail killmail = new Killmail();
        killmail.setVictimAlliance("Brave Collective");
        assertFalse(sut.isVictimNotBrave(killmail));
    }

    @Test
    public void isInBraveSystem_true() throws Exception {
        final Killmail killmail = new Killmail();
        killmail.setSolarSystemId(KillmailPuller.systems.get(0));
        assertTrue(sut.isInBraveSystem(killmail));
    }

    @Test
    public void isInBraveSystem_false() throws Exception {
        final Killmail killmail = new Killmail();
        killmail.setSolarSystemId(1);
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
    public void isNotInFleet() throws Exception {
        final Killmail killmail = new Killmail();
        killmail.setAttackerIds(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L));
        assertTrue(sut.isNotInFleet(killmail));
    }

    @Test
    public void filterAndSaveKillmails() throws Exception {
        when(killmailRepo.save(anyList())).thenReturn(null);

        final Killmail killmail = new Killmail();
        killmail.setVictimAlliance("test");
        killmail.setSolarSystemId(KillmailPuller.systems.get(0));
        final List<Killmail> killmails = Collections.singletonList(killmail);

        sut.filterAndSaveKillmails(killmails);

        verify(killmailRepo).save(anyList());
    }

    @Test
    public void pullKillmails_userWithoutCharacterId_doesNothing() throws Exception {
        when(userRepo.findAll()).thenReturn(Collections.singletonList(new User()));

        sut.pullKillmails(1L);

        verify(requestService, never()).getKillmails(null, 1L);
    }

    @Test
    public void pullKillmails_requestReturnsNothing_doesNothing() throws Exception {
        User user = new User();
        user.setCharacterId(2L);
        when(userRepo.findAll()).thenReturn(Collections.singletonList(user));
        when(requestService.getKillmails(2L, 1L)).thenReturn(Optional.empty());

        sut.pullKillmails(1L);

        verify(killmailRepo, never()).save(anyList());
    }

    @Test
    public void pullKillmails_requestReturnsEmptyArray_doesntSaveAny() throws Exception {
        User user = new User();
        user.setCharacterId(2L);
        when(userRepo.findAll()).thenReturn(Collections.singletonList(user));
        when(requestService.getKillmails(2L, 1L)).thenReturn(Optional.of(new JSONArray("[]")));

        sut.pullKillmails(1L);

        verify(killmailRepo, never()).save(anyList());
    }

    @Test
    public void pullKillmails_requestReturnsSomething_doesSave() throws Exception {
        User user = new User();
        user.setCharacterId(2L);
        when(userRepo.findAll()).thenReturn(Collections.singletonList(user));
        when(requestService.getKillmails(2L, 1L)).thenReturn(Optional.of(new JSONArray("[{}]")));
        doNothing().when(sut).filterAndSaveKillmails(emptyList());

        sut.pullKillmails(1L);

        verify(sut).filterAndSaveKillmails(emptyList());
    }
}
