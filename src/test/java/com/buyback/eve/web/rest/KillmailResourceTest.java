package com.buyback.eve.web.rest;

import java.util.Optional;

import com.buyback.eve.domain.Killmail;
import com.buyback.eve.repository.KillmailRepository;
import com.buyback.eve.service.JsonRequestService;
import com.buyback.eve.service.KillmailPuller;
import static com.buyback.eve.service.KillmailParserTest.object1;

import org.junit.Test;
import org.springframework.http.ResponseEntity;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class KillmailResourceTest {

    private KillmailRepository repo = mock(KillmailRepository.class);
    private KillmailPuller puller = mock(KillmailPuller.class);
    private JsonRequestService jsonService = mock(JsonRequestService.class);
    private KillmailResource sut = new KillmailResource(repo, puller, jsonService);

    @Test
    public void addKillmail_withAlreadyExisting_skipsTheRest() throws Exception {
        when(repo.findByKillId(1L)).thenReturn(Optional.of(new Killmail()));

        ResponseEntity responseEntity = sut.addKillmail(1L);

        assertEquals(200, responseEntity.getStatusCodeValue());

        verify(jsonService, never()).getKillmail(1L);
    }

    @Test
    public void addKillmail_whenJsonRequestFails_skipsTheRest() throws Exception {
        when(repo.findByKillId(1L)).thenReturn(Optional.empty());
        when(jsonService.getKillmail(1L)).thenReturn(Optional.empty());

        ResponseEntity responseEntity = sut.addKillmail(1L);

        assertEquals(200, responseEntity.getStatusCodeValue());

        verify(puller, never()).filterAndSaveKillmails(any());
    }

    @Test
    public void addKillmail_whenJsonRequestSucceeds_savesAKillmail() throws Exception {
        when(repo.findByKillId(1L)).thenReturn(Optional.empty());
        when(jsonService.getKillmail(1L)).thenReturn(Optional.of(object1));

        ResponseEntity responseEntity = sut.addKillmail(1L);

        assertEquals(200, responseEntity.getStatusCodeValue());

        verify(puller).filterAndSaveKillmails(any());
    }
}
