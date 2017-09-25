package com.buyback.eve.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import com.buyback.eve.domain.Killmail;
import com.buyback.eve.repository.KillmailRepository;
import com.buyback.eve.repository.UserRepository;
import static com.buyback.eve.service.KillmailParser.parseKillmails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KillmailPuller {

    static final long HOUR = 3600L;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final KillmailRepository killmailRepository;
    private final UserRepository userRepository;
    private final JsonRequestService jsonRequestService;

    public KillmailPuller(final KillmailRepository killmailRepository,
                          final UserRepository userRepository,
                          final JsonRequestService jsonRequestService) {
        this.killmailRepository = killmailRepository;
        this.userRepository = userRepository;
        this.jsonRequestService = jsonRequestService;
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
        long maxDuration = HOUR * 24 * 7L;
        pullKillmails(maxDuration);
    }

    void pullKillmails(Long duration) {
        userRepository.findAll().stream().filter(user -> user.getCharacterId() != null)
                      .forEach(user -> jsonRequestService.getKillmails(user.getCharacterId(), duration).ifPresent(jsonArray -> {
                          log.info("Adding killmails for characterId={}", user.getCharacterId());
                          if (jsonArray.length() > 0) {
                              List<Killmail> killmails = parseKillmails(jsonArray);
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
                                           .collect(Collectors.toList());
        killmailRepository.save(filtered);
    }

    boolean isNotInFleet(final Killmail killmail) {
        return killmail.getAttackerIds().size() <= 20;
    }

    boolean isNotAnEmptyPod(final Killmail killmail) {
        // Capsules are valued 10k
        return killmail.getTotalValue() != 10_000L;
    }

    static final List<Long> systems = Stream.of(
                                    30001198L, // GE
                                    30001162L, // V-3
                                    30001156L, // B-3
                                    30001159L, // HY-
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
                                    30001221L, // HP
                                    30001220L, // SNVF
                                    30001222L, // V2-V
                                    30001224L // CX65
                                  ).collect(Collectors.toList());

    boolean isInBraveSystem(final Killmail killmail) {
        return systems.contains(killmail.getSolarSystemId());
    }

    boolean isVictimNotBrave(final Killmail killmail) {
        return !killmail.getVictimAlliance().equals("Brave Collective");
    }
}
