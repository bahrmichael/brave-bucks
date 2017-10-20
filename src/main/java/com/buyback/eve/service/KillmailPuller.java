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
import static com.buyback.eve.service.KillmailParser.parseKillmails;

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

    public KillmailPuller(final KillmailRepository killmailRepository,
                          final UserRepository userRepository,
                          final JsonRequestService jsonRequestService,
                          final SolarSystemRepository solarSystemRepository) {
        this.killmailRepository = killmailRepository;
        this.userRepository = userRepository;
        this.jsonRequestService = jsonRequestService;
        this.solarSystemRepository = solarSystemRepository;
    }

    @PostConstruct
    public void init() {
        pullKillmails();
    }

    @Async
    @Scheduled(cron = "0 */10 * * * *")
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
                          log.info("Adding killmails for characterId={}", user.getCharacterId());
                          if (array.length() > 0) {
                              final List<Killmail> killmails = parseKillmails(array);
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

    boolean isNotInFleet(final Killmail killmail) {
        return killmail.getAttackerIds().size() <= 20;
    }

    boolean isNotAnEmptyPod(final Killmail killmail) {
        // Capsules are valued 10k
        return killmail.getTotalValue() != 10_000L;
    }

    boolean isInBraveSystem(final Killmail killmail) {
        return systems.contains(killmail.getSolarSystemId());
    }

    public void setSystems(final List<Long> systems) {
        this.systems = systems;
    }

    boolean isVictimNotBrave(final Killmail killmail) {
        return !killmail.getVictimAlliance().equals("Brave Collective");
    }
}
