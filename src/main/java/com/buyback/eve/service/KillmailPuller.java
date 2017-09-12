package com.buyback.eve.service;

import java.util.Optional;

import com.buyback.eve.repository.KillmailRepository;
import com.buyback.eve.repository.UserRepository;

import static com.buyback.eve.service.KillmailParser.*;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KillmailPuller {

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

    private Optional<String> getRawData(final Long characterId) {
        return Optional.empty();
    }
}
