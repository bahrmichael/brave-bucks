package com.buyback.eve.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.buyback.eve.domain.Killmail;
import com.buyback.eve.repository.KillmailRepository;
import com.buyback.eve.repository.UserRepository;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import static com.buyback.eve.service.KillmailParser.parseKillmails;

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
        pullKillmails();
    }

    @Async
    @Scheduled(cron = "0 18 */2 * * *")
    public void pullKillmails() {
        userRepository.findAll().forEach(user -> getRawData(user.getCharacterId()).ifPresent(jsonArray -> {
            List<Killmail> killmails = parseKillmails(jsonArray, user.getCharacterId());
            List<Killmail> filtered = killmails.stream()
                                               .filter(this::isVictimNotBrave)
                                               .filter(this::isInBraveSystem)
                                               .filter(this::isNotAnEmptyPod)
                                               .filter(this::isNotInFleet)
                                               .collect(Collectors.toList());
            killmailRepository.save(filtered);
        }));
    }

    private boolean isNotInFleet(final Killmail killmail) {
        return killmail.getAttackerCount() < 40;
    }

    private boolean isNotAnEmptyPod(final Killmail killmail) {
        // Capsules are valued 10k
        return killmail.getTotalValue() != 10_000L;
    }

    private static final List<Long> systems = Stream.of(
                                    30001198L, // GE
                                    30001162L // V-3
                                  ).collect(Collectors.toList());

    private boolean isInBraveSystem(final Killmail killmail) {
        return systems.contains(killmail.getSolarSystemId());
    }

    private boolean isVictimNotBrave(final Killmail killmail) {
        return !killmail.getVictimAlliance().equals("Brave Collective");
    }

    private Optional<JSONArray> getRawData(final Long characterId) {
        String url = "https://zkillboard.com/api/kills/characterID/" + characterId + "/pastSeconds/14400/no-items/";
        try {
            HttpResponse<JsonNode> jsonNodeHttpResponse = Unirest.get(url)
                                                                 .header("Accept-Encoding", "gzip")
                                                                 .header("User-Agent", "EvE: Rihan Shazih")
                                                                 .asJson();
            return Optional.of(jsonNodeHttpResponse.getBody().getArray());
        } catch (UnirestException e) {
            log.error("Failed to get data from zKill={}", url, e);
            return Optional.empty();
        }
    }
}
