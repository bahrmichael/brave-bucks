package com.buyback.eve.web.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import com.buyback.eve.domain.Killmail;
import com.buyback.eve.repository.KillmailRepository;
import com.buyback.eve.service.JsonRequestService;
import com.buyback.eve.service.KillmailParser;
import com.buyback.eve.service.KillmailPuller;
import com.mashape.unirest.http.JsonNode;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public KillmailResource(final KillmailRepository killmailRepository,
                            final KillmailPuller killmailPuller,
                            final JsonRequestService jsonRequestService,
                            final KillmailParser killmailParser) {
        this.killmailRepository = killmailRepository;
        this.killmailPuller = killmailPuller;
        this.jsonRequestService = jsonRequestService;
        this.killmailParser = killmailParser;
    }

    /**
     * Adds a killmail if it doesn't exist yet.
     *
     * @param killmailId
     * @return
     */
    @PostMapping("/public/killmail/{link}")
    public ResponseEntity addKillmail(@PathVariable("link") String link) {
        Long killId = Long.valueOf(link.split("/kill/")[0].replace("/", ""));
        Optional<JsonNode> optional = jsonRequestService.getKillmail(killId);
        if (optional.isPresent()) {
            JSONObject obj = optional.get().getObject();
            Killmail killmail = killmailParser.parseKillmail(obj);
            if (null == killmail) {
                log.info("{} was a structure or failed to be parsed.", link);
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
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The killmail is an empty pos. We don't count that.");
                }
                boolean isTooOld = LocalDateTime.parse(killmail.getKillTime()).isBefore(LocalDateTime.now().minusDays(2));
                if (isTooOld) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The killmail is too old to be counted. The maximum age is 2 days.");
                }
                killmailRepository.save(killmail);
                return ResponseEntity.ok().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("The server has a hiccup, please try again later. DM Rihan Shazih if the issue persists.");
        }
    }

    @PostMapping("/killmail/long-pull")
    public ResponseEntity longPull() {
        if (lastLongPullInvocation.isBefore(fiveMinutesAgo())) {
            lastLongPullInvocation = LocalDateTime.now();
            killmailPuller.longPull();
            return ResponseEntity.status(200).build();
        } else {
            return ResponseEntity.status(420).build();
        }
    }

    private LocalDateTime fiveMinutesAgo() {
        return LocalDateTime.now().minusMinutes(5L);
    }
}
