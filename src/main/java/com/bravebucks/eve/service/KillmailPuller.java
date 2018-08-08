package com.bravebucks.eve.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.toList;

import javax.annotation.PostConstruct;

import com.bravebucks.eve.domain.Killmail;
import com.bravebucks.eve.repository.SolarSystemRepository;
import com.bravebucks.eve.repository.UserRepository;
import com.bravebucks.eve.domain.SolarSystem;
import com.bravebucks.eve.repository.KillmailRepository;
import com.codahale.metrics.annotation.Timed;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.github.jhipster.config.JHipsterConstants;

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
    private final Environment env;

    public KillmailPuller(final KillmailRepository killmailRepository,
                          final UserRepository userRepository,
                          final JsonRequestService jsonRequestService,
                          final SolarSystemRepository solarSystemRepository,
                          final KillmailParser killmailParser, final Environment env) {
        this.killmailRepository = killmailRepository;
        this.userRepository = userRepository;
        this.jsonRequestService = jsonRequestService;
        this.solarSystemRepository = solarSystemRepository;
        this.killmailParser = killmailParser;
        this.env = env;
    }

    @PostConstruct
    public void init() {
        // dev only
        if (Arrays.asList(env.getActiveProfiles()).contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)) {
//            pullKillmails();
        }
    }

    private boolean isFirstPull = true;

    @Async
    @Scheduled(cron = "0 */30 * * * *")
    @Timed
    public void pullKillmails() {
        if (isFirstPull) {
            longPull();
            isFirstPull = false;
        } else {
            pullKillmails(HOUR * 4);
        }
    }

    public void longPull() {
        final long maxDuration = HOUR * 24 * 7L;
        pullKillmails(maxDuration);
    }

    public void pullKillmails(final Long duration) {
        systems = new ArrayList<>();
        solarSystemRepository.findAllByTrackPvp(true).stream()
                             .mapToLong(SolarSystem::getSystemId)
                             .forEach(id -> systems.add(id));

        userRepository.findAll().stream().filter(user -> user.getCharacterId() != null)
                      .forEach(user -> jsonRequestService.getKillmails(user.getCharacterId(), duration)
                      .ifPresent(jsonBody -> {
                          final JSONArray array = jsonBody.getArray();
                          if (array.length() > 0) {
                              final List<Killmail> killmails = killmailParser.parseKillmails(array);
                              List<Killmail> filtered = filterKillmails(killmails);
                              log.info("Processing {} killmails for characterId {}", filtered.size(), user.getCharacterId());
                              killmailRepository.save(filtered);
                          }
        }));
    }

    public List<Killmail> filterKillmails(final List<Killmail> killmails) {
        return killmails.stream()
                       .filter(this::isVictimNotBrave)
                       .filter(this::isInBraveSystem)
                       .filter(this::isNotInFleet)
                       .filter(this::isNotAnEmptyPod)
                       .filter(this::doesNotExist)
                       .collect(toList());
    }

    private boolean doesNotExist(final Killmail killmail) {
        return !killmailRepository.findByKillId(killmail.getKillId()).isPresent();
    }

    public boolean isNotInFleet(final Killmail killmail) {
        return killmail.getAttackerIds().size() <= 20;
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
