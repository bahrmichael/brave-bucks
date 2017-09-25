package com.buyback.eve.service;

import java.util.Optional;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JsonRequestService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    Optional<JSONArray> getKillmails(final Long characterId) {
        final long duration = 3600;
        String url = "https://zkillboard.com/api/kills/characterID/" + characterId + "/pastSeconds/" + duration + "/no-items/";
        try {
            HttpResponse<JsonNode> response = Unirest.get(url)
                                                     .header("Accept-Encoding", "gzip")
                                                     .header("User-Agent", "EvE: Rihan Shazih")
                                                     .asJson();
            if (response.getStatus() != 200) {
                log.warn("{} returned status code {}. Data will not be parsed.", url, response.getStatus());
                return Optional.empty();
            }
            return Optional.of(response.getBody().getArray());
        } catch (UnirestException e) {
            log.error("Failed to get data from zKill={}", url, e);
            return Optional.empty();
        }
    }

    public Optional<JSONObject> getKillmail(final Long killId) {
        String url = "https://zkillboard.com/api/killID/"+ killId +"/no-items/";
        try {
            HttpResponse<JsonNode> response = Unirest.get(url)
                                                     .header("Accept-Encoding", "gzip")
                                                     .header("User-Agent", "EvE: Rihan Shazih")
                                                     .asJson();
            if (response.getStatus() != 200) {
                log.warn("{} returned status code {}. Data will not be parsed.", url, response.getStatus());
                return Optional.empty();
            }
            return Optional.of(response.getBody().getArray().getJSONObject(0));
        } catch (UnirestException e) {
            log.error("Failed to get data from zKill={}", url, e);
            return Optional.empty();
        }
    }
}
