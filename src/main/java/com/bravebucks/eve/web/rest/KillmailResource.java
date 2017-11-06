package com.bravebucks.eve.web.rest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import com.bravebucks.eve.domain.Killmail;
import com.bravebucks.eve.domain.User;
import com.bravebucks.eve.repository.UserRepository;
import com.bravebucks.eve.security.AuthoritiesConstants;
import com.bravebucks.eve.security.SecurityUtils;
import com.bravebucks.eve.service.JsonRequestService;
import com.bravebucks.eve.service.KillmailParser;
import com.bravebucks.eve.repository.KillmailRepository;
import com.bravebucks.eve.service.KillmailPuller;
import com.codahale.metrics.annotation.Timed;
import com.mashape.unirest.http.JsonNode;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for executing appraisal requests.
 */
@RestController
@RequestMapping("/api/")
public class KillmailResource {

    private final Logger log = LoggerFactory.getLogger(KillmailResource.class);

    private LocalDateTime lastLongPullInvocation = fiveMinutesAgo();

    private final KillmailRepository killmailRepository;
    private final KillmailPuller killmailPuller;
    private final JsonRequestService jsonRequestService;
    private final KillmailParser killmailParser;
    private final UserRepository userRepository;

    public KillmailResource(final KillmailRepository killmailRepository,
                            final KillmailPuller killmailPuller,
                            final JsonRequestService jsonRequestService,
                            final KillmailParser killmailParser,
                            final UserRepository userRepository) {
        this.killmailRepository = killmailRepository;
        this.killmailPuller = killmailPuller;
        this.jsonRequestService = jsonRequestService;
        this.killmailParser = killmailParser;
        this.userRepository = userRepository;
    }

    /**
     * Adds a killmail if it doesn't exist yet.
     *
     * @param killmailId
     * @return
     */
    @PostMapping("/killmail/{killId}")
    @Timed
    public ResponseEntity addKillmail(@PathVariable("killId") Long killId) {
        if (killmailRepository.findByKillId(killId).isPresent()) {
            // killmail already exists
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Optional<JsonNode> optional = jsonRequestService.getKillmail(killId);
        if (optional.isPresent()) {
            JSONArray obj = optional.get().getArray();
            if (obj.length() == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The killmail could not be found.");
            }
            Killmail killmail = killmailParser.parseKillmail(obj.getJSONObject(0));
            if (null == killmail) {
                log.info("{} was a structure or failed to be parsed.", killId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This looks like a structure, which we don't count.");
            } else {
                boolean notInFleet = killmailPuller.isNotInFleet(killmail);
                if (!notInFleet) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There were more than 20 attackers on the killmail.");
                }
                boolean inBraveSystem = killmailPuller.isInBraveSystem(killmail);
                if (!inBraveSystem) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The killmail is not from one of the systems of the Hunting Area.");
                }
                boolean victimNotBrave = killmailPuller.isVictimNotBrave(killmail);
                if (!victimNotBrave) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The victim is from Brave Collective. Bad Awox!");
                }
                boolean notAnEmptyPod = killmailPuller.isNotAnEmptyPod(killmail);
                if (!notAnEmptyPod) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The killmail is an empty pod. We don't count that.");
                }
                boolean isTooOld = LocalDateTime.now().minusDays(1).isAfter(LocalDateTime.ofInstant(Instant.parse(killmail.getKillTime()), ZoneOffset.UTC));
                if (isTooOld) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        "The killmail is too old to be counted. The maximum age is 2 days.");
                }

                killmailRepository.save(killmail);

                Optional<User> optionalUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
                if (optionalUser.isPresent()) {
                    Long characterId = optionalUser.get().getCharacterId();
                    if (!killmail.getAttackerIds().contains(characterId)) {
                        return ResponseEntity.ok("You are however not one of the attackers.");
                    }
                } else {
                    log.warn("An authenticated request was performed, but the user {} could not be found.", SecurityUtils.getCurrentUserLogin());
                    return ResponseEntity.ok().build();
                }

                return ResponseEntity.status(201).body(PlayerStatsResource.createMailDto(killmail));
            }
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("The server has a hiccup, please try again later. DM Rihan Shazih if the issue persists.");
        }
    }

    private LocalDateTime fiveMinutesAgo() {
        return LocalDateTime.now().minusMinutes(5L);
    }
}
