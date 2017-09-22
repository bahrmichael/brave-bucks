package com.buyback.eve.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

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
    }

    @PostConstruct
    public void init() {
        pullKillmails();
    }

    @Async
    @Scheduled(cron = "0 18 * * * *")
    public void pullKillmails() {
        userRepository.findAll().stream().filter(user -> user.getCharacterId() != null)
                      .forEach(user -> getRawData(user.getCharacterId()).ifPresent(jsonArray -> {
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
                                    30001162L, // V-3
                                    30001204L, // YHN
                                    30001200L, // 3GD
                                    30001831L, // DSS (stain)
                                    30001201L, // 4M
                                    30001199L, // 3-OK
                                    30001203L, // AX-DOT
                                    30001213L, // MUXX
                                    30001214L, // E1
                                    30001202L, // MY
                                    30001219L, // 8B
                                    30001220L, // SNVF
                                    30001221L, // HP
                                    30001222L, // V2-V
                                    30001224L // CS65
                                  ).collect(Collectors.toList());

    private boolean isInBraveSystem(final Killmail killmail) {
        return systems.contains(killmail.getSolarSystemId());
    }

    private boolean isVictimNotBrave(final Killmail killmail) {
        return !killmail.getVictimAlliance().equals("Brave Collective");
    }

    private Optional<JSONArray> getRawData(final Long characterId) {
        final long seconds = 3600L * 24 * 7;
        String url = "https://zkillboard.com/api/kills/characterID/" + characterId + "/pastSeconds/" + seconds + "/no-items/";
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
}
