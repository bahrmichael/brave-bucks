package com.buyback.eve.service;

import java.util.Optional;

import com.buyback.eve.repository.KillmailRepository;
import com.buyback.eve.repository.UserRepository;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import static com.buyback.eve.service.KillmailParser.*;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KillmailPuller {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final KillmailRepository killmailRepository;
    private final UserRepository userRepository;

    public KillmailPuller(final KillmailRepository killmailRepository,
                          final UserRepository userRepository) {
        this.killmailRepository = killmailRepository;
        this.userRepository = userRepository;
    }

    @Async
    @Scheduled(cron = "0 37 * * * *")
    public void pullKillmails() {
        userRepository.findAll().forEach(user
                               -> getRawData(user.getCharacterId()).ifPresent(jsonBody
                                     -> killmailRepository.save(parseKillmails(jsonBody, user.getCharacterId()))));
    }

    private Optional<JSONArray> getRawData(final Long characterId) {
        String url = "https://zkillboard.com/api/kills/" + characterId + "/pastSeconds/7200/no-items/";
        try {
            HttpResponse<JsonNode> jsonNodeHttpResponse = Unirest.get(url).asJson();
            return Optional.of(jsonNodeHttpResponse.getBody().getArray());
        } catch (UnirestException e) {
            log.error("Failed to get data from zKill={}", url, e);
            return Optional.empty();
        }
    }
}
