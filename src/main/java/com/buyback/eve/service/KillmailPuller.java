package com.buyback.eve.service;

import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;

import javax.annotation.PostConstruct;

import com.buyback.eve.domain.Killmail;
import com.buyback.eve.domain.SolarSystem;
import com.buyback.eve.repository.KillmailRepository;
import com.buyback.eve.repository.SolarSystemRepository;
import com.buyback.eve.repository.UserRepository;
import com.codahale.metrics.annotation.Timed;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KillmailPuller {

    static final long HOUR = 3600L;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private List<Long> systems;

    private final KillmailRepository killmailRepository;
    private final UserRepository userRepository;
    private final JsonRequestService jsonRequestService;
    private final SolarSystemRepository solarSystemRepository;
    private final KillmailParser killmailParser;

    public KillmailPuller(final KillmailRepository killmailRepository,
                          final UserRepository userRepository,
                          final JsonRequestService jsonRequestService,
                          final SolarSystemRepository solarSystemRepository,
                          final KillmailParser killmailParser) {
        this.killmailRepository = killmailRepository;
        this.userRepository = userRepository;
        this.jsonRequestService = jsonRequestService;
        this.solarSystemRepository = solarSystemRepository;
        this.killmailParser = killmailParser;
    }

    @PostConstruct
    public void init() {
        pullKillmails();
    }

    @Async
    @Scheduled(cron = "0 */10 * * * *")
    @Timed
    public void pullKillmails() {
        pullKillmails(HOUR);
    }

    public void longPull() {
        final long maxDuration = HOUR * 24 * 7L;
        pullKillmails(maxDuration);
    }

    void pullKillmails(final Long duration) {
        systems = new ArrayList<>();
        solarSystemRepository.findAll().stream().mapToLong(SolarSystem::getSystemId).forEach(id -> systems.add(id));

        userRepository.findAll().stream().filter(user -> user.getCharacterId() != null)
                      .forEach(user -> jsonRequestService.getKillmails(user.getCharacterId(), duration)
                      .ifPresent(jsonBody -> {
                          final JSONArray array = jsonBody.getArray();
                          if (array.length() > 0) {
                              log.info("Adding {} killmails for characterId {}", array.length(), user.getCharacterId());
                              final List<Killmail> killmails = killmailParser.parseKillmails(array);
                              filterAndSaveKillmails(killmails);
                          }
        }));
    }

    public void filterAndSaveKillmails(final List<Killmail> killmails) {
        List<Killmail> filtered = killmails.stream()
                                           .filter(this::isVictimNotBrave)
                                           .filter(this::isInBraveSystem)
                                           .filter(this::isNotAnEmptyPod)
                                           .filter(this::isNotInFleet)
                                           .collect(toList());
        killmailRepository.save(filtered);
    }

    public boolean isNotInFleet(final Killmail killmail) {
        return killmail.getAttackerIds().size() < 20;
    }

    public boolean isNotAnEmptyPod(final Killmail killmail) {
        // Capsules are valued 10k
        return killmail.getTotalValue() != 10_000L;
    }

    public boolean isInBraveSystem(final Killmail killmail) {
        return systems.contains(killmail.getSolarSystemId());
    }

    public void setSystems(final List<Long> systems) {
        this.systems = systems;
    }

    public boolean isVictimNotBrave(final Killmail killmail) {
        return !killmail.getVictimGroupName().equals("Brave Collective");
    }
}
