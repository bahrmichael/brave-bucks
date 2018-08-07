package com.bravebucks.eve.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.body.MultipartBody;
import com.mashape.unirest.request.body.RequestBodyEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Deprecated
public class JsonRequestService {

    private static final String WRONG_STATUS_CODE = "{} returned status code {}.";
    private static final String UNIREST_EXCEPTION = "Failed to get data from url={}";
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private Map<String, String> defaultHeaders;

    @Deprecated
    public JsonRequestService() {
        defaultHeaders = new HashMap<>();
        defaultHeaders.put("User-Agent", "EvE: Rihan Shazih");
        defaultHeaders.put("Accept-Encoding", "gzip");

        // Only one time
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    @Deprecated
    public Optional<JsonNode> searchSolarSystem(final String systemName) {
        String url = "https://esi.tech.ccp.is/v2/search/?categories=solar_system&datasource=tranquility"
                     + "&language=en-us&search=" + systemName + "&strict=true";
        GetRequest getRequest = get(url, null);
        return executeRequest(getRequest);
    }

    Optional<JsonNode> getKillmails(final Long characterId, final long duration) {
        String url = "https://zkillboard.com/api/kills/characterID/" + characterId + "/pastSeconds/" + duration
                     + "/no-items/";
        GetRequest getRequest = get(url, null);
        return executeRequest(getRequest);
    }
    @Deprecated
    public Optional<JsonNode> getKillmail(final Long killId) {
        String url = "https://zkillboard.com/api/killID/" + killId + "/no-items/";
        GetRequest getRequest = get(url, null);
        return executeRequest(getRequest);
    }
    @Deprecated
    public Optional<JsonNode> getPlayerGroupNames(final long allianceId) {
        String url = "https://esi.tech.ccp.is/v2/universe/names/";
        final RequestBodyEntity request = Unirest.post(url).headers(defaultHeaders).body(singletonList(allianceId));
        return executeRequest(request);
    }
    @Deprecated
    public Optional<JsonNode> getAccessToken(final String clientId, final String clientSecret, final String code) {
        String url = "https://login.eveonline.com/oauth/token";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        Map<String, Object> fields = new HashMap<>();
        fields.put("grant_type", "authorization_code");
        fields.put("code", code);

        MultipartBody postRequest = post(url, clientId, clientSecret, headers, fields);

        return executeRequest(postRequest);
    }
    @Deprecated
    public Optional<JsonNode> getUserDetails(final String accessToken) {
        String url = "https://login.eveonline.com/oauth/verify";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);

        GetRequest getRequest = get(url, headers);

        return executeRequest(getRequest);
    }

    Optional<JsonNode> getAdms() {
        return justGet("https://esi.tech.ccp.is/v1/sovereignty/structures/?datasource=tranquility");
    }

    private Optional<JsonNode> justGet(final String url) {
        return executeRequest(get(url, null));
    }

    Optional<JsonNode> executeRequest(final BaseRequest request) {
        try {
            HttpResponse<JsonNode> response = request.asJson();
            if (response.getStatus() != 200) {
                log.warn(WRONG_STATUS_CODE, request.getHttpRequest().getUrl(), response.getStatus());
                return Optional.empty();
            }
            return Optional.of(response.getBody());
        } catch (UnirestException e) {
            log.error(UNIREST_EXCEPTION, request.getHttpRequest().getUrl(), e);
            return Optional.empty();
        }
    }

    GetRequest get(String url, Map<String, String> headers) {
        return Unirest.get(url).headers(defaultHeaders).headers(headers);
    }

    MultipartBody post(final String url, final String username, final String password, final Map<String, String> headers, final Map<String, Object> fields) {
        return Unirest.post(url).basicAuth(username, password).headers(defaultHeaders).headers(headers).fields(fields);
    }
}
