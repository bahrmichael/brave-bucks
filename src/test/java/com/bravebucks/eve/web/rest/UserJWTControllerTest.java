package com.bravebucks.eve.web.rest;

import java.util.Optional;

import com.bravebucks.eve.domain.User;
import com.bravebucks.eve.repository.UserRepository;
import com.bravebucks.eve.service.JsonRequestService;
import com.bravebucks.eve.service.UserService;
import com.mashape.unirest.http.JsonNode;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserJWTControllerTest {

    private UserRepository userRepo = mock(UserRepository.class);
    private JsonRequestService requestService = mock(JsonRequestService.class);
    private UserService userService = mock(UserService.class);
    private UserJWTController sut = new UserJWTController(null, userRepo, userService, requestService);

    @Test
    public void authorize() throws Exception {
    }

    @Test
    public void createIfNotExists_withExists() throws Exception {
        UserJWTController.CharacterDetails characterDetails = new UserJWTController.CharacterDetails("test", 1L);
        when(userRepo.findOneByLogin("test")).thenReturn(Optional.of(new User()));

        sut.createIfNotExists(characterDetails);

        verify(userService, never()).createUser("test", 1L);
    }

    @Test
    public void createIfNotExists_withNotExists() throws Exception {
        UserJWTController.CharacterDetails characterDetails = new UserJWTController.CharacterDetails("test", 1L);
        when(userRepo.findOneByLogin("test")).thenReturn(Optional.empty());
        when(userService.createUser("test", 1L)).thenReturn(null);

        sut.createIfNotExists(characterDetails);

        verify(userService).createUser("test", 1L);
    }

    @Test
    public void getCharacterDetails_whenReceivesNoAuthToken_returnsNull() throws Exception {
        when(requestService.getAccessToken(anyString(), anyString(), anyString())).thenReturn(Optional.empty());

        UserJWTController.CharacterDetails result = sut.getCharacterDetails("", "", "");
        assertNull(result);
    }

    @Test
    public void getCharacterDetails_whenReceivesNoDetails_returnsNull() throws Exception {
        final JsonNode jsonNode = new JsonNode("{\"access_token\": \"text\"}");
        when(requestService.getAccessToken(anyString(), anyString(), anyString())).thenReturn(Optional.of(jsonNode));
        when(requestService.getUserDetails("text")).thenReturn(Optional.empty());

        UserJWTController.CharacterDetails result = sut.getCharacterDetails("", "", "");
        assertNull(result);

        verify(requestService).getUserDetails("text");
    }

    @Test
    public void getCharacterDetails() throws Exception {
        final JsonNode jsonNode = new JsonNode("{\"access_token\": \"text\"}");
        when(requestService.getAccessToken(anyString(), anyString(), anyString())).thenReturn(Optional.of(jsonNode));
        final JsonNode detailsNode = new JsonNode("{CharacterName:\"name\", CharacterID: 1}");
        when(requestService.getUserDetails("text")).thenReturn(Optional.of(detailsNode));

        UserJWTController.CharacterDetails result = sut.getCharacterDetails("", "", "");
        assertNotNull(result);
        assertEquals("name", result.getName());
        assertEquals(1L, result.getId().longValue());
    }

}
