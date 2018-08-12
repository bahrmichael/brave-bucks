package com.bravebucks.eve.service;

import java.util.HashMap;
import java.util.Objects;

import com.bravebucks.eve.domain.esi.CharacterInfoResponse;
import com.bravebucks.eve.repository.UserRepository;
import com.codahale.metrics.annotation.Timed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class AllianceParser {

    private static final Logger log = LoggerFactory.getLogger(AllianceParser.class);

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public AllianceParser(final UserRepository userRepository,
                          final RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    @Async
    @Scheduled(cron = "0 0 9 * * *")
    @Timed
    public void updateAlliances() {
        userRepository.findAll().forEach(user -> {
            final String uri = "https://esi.evetech.net/v4/characters/" + user.getCharacterId() + "/";
            try {
                final CharacterInfoResponse characterInfo = restTemplate.getForObject(uri, CharacterInfoResponse.class,
                                                                                      new HashMap<>());
                if (!Objects.equals(user.getAllianceId(), characterInfo.getAllianceId())) {
                    user.setAllianceId(characterInfo.getAllianceId());
                    userRepository.save(user);
                }
            } catch (HttpClientErrorException | HttpServerErrorException ex) {
                log.error("Failed to retrieve character info for {}.", user.getCharacterId(), ex);
            }
        });
    }
}
