package com.bravebucks.eve.service;

import java.util.Optional;

import com.mashape.unirest.http.HttpMethod;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.body.MultipartBody;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JsonRequestServiceTest {

    private JsonRequestService sut = spy(new JsonRequestService());

    @Test
    public void getKillmails() throws Exception {
        doReturn(null).when(sut).executeRequest(any(GetRequest.class));

        Optional<JsonNode> killmails = sut.getKillmails(1L, 2L);
        assertNull(killmails);

        verify(sut).get("https://zkillboard.com/api/kills/characterID/1/pastSeconds/2/no-items/", null);
        verify(sut).executeRequest(any(GetRequest.class));
    }

    @Test
    public void getKillmail() throws Exception {
        doReturn(null).when(sut).executeRequest(any(GetRequest.class));

        Optional<JsonNode> killmail = sut.getKillmail(1L);
        assertNull(killmail);

        verify(sut).get("https://zkillboard.com/api/killID/1/no-items/", null);
        verify(sut).executeRequest(any(GetRequest.class));
    }

    @Test
    public void getAccessToken() throws Exception {
        doReturn(null).when(sut).executeRequest(any(MultipartBody.class));

        Optional<JsonNode> response = sut.getAccessToken("clientId", "clientSecret", "code");
        assertNull(response);

        verify(sut).post(eq("https://login.eveonline.com/oauth/token"), eq("clientId"), eq("clientSecret"), anyMap(), anyMap());
        verify(sut).executeRequest(any(MultipartBody.class));
    }

    @Test
    public void getUserDetails() throws Exception {
        doReturn(null).when(sut).executeRequest(any(GetRequest.class));

        Optional<JsonNode> response = sut.getUserDetails("token");
        assertNull(response);

        verify(sut).get(eq("https://login.eveonline.com/oauth/verify"), anyMap());
        verify(sut).executeRequest(any(GetRequest.class));
    }

    @Test
    public void executeRequest_withNon200() throws Exception {
        final BaseRequest requestMock = mock(BaseRequest.class);
        final HttpResponse<JsonNode> responseMock = mock(HttpResponse.class);
        when(requestMock.asJson()).thenReturn(responseMock);
        when(requestMock.getHttpRequest()).thenReturn(new HttpRequest(HttpMethod.GET, "test"));
        when(responseMock.getStatus()).thenReturn(0);

        Optional<JsonNode> result = sut.executeRequest(requestMock);
        assertFalse(result.isPresent());
    }

    @Test
    public void executeRequest_withException() throws Exception {
        final BaseRequest requestMock = mock(BaseRequest.class);
        when(requestMock.asJson()).thenThrow(new UnirestException("test"));
        when(requestMock.getHttpRequest()).thenReturn(new HttpRequest(HttpMethod.GET, "test"));

        Optional<JsonNode> result = sut.executeRequest(requestMock);
        assertFalse(result.isPresent());
    }

    @Test
    public void executeRequest() throws Exception {
        JsonNode expected = new JsonNode("{}");

        final BaseRequest requestMock = mock(BaseRequest.class);
        final HttpResponse<JsonNode> responseMock = mock(HttpResponse.class);
        when(requestMock.asJson()).thenReturn(responseMock);
        when(requestMock.getHttpRequest()).thenReturn(new HttpRequest(HttpMethod.GET, "test"));
        when(responseMock.getStatus()).thenReturn(200);
        when(responseMock.getBody()).thenReturn(expected);

        Optional<JsonNode> result = sut.executeRequest(requestMock);
        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
    }
}
