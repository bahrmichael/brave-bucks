package com.buyback.eve.service;

import java.util.Optional;

import com.buyback.eve.repository.KillmailRepository;

import static com.buyback.eve.service.KillmailParser.*;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KillmailPuller {

    private final KillmailRepository killmailRepository;

    public KillmailPuller(final KillmailRepository killmailRepository) {
        this.killmailRepository = killmailRepository;
    }

    @Async
    @Scheduled(cron = "0 37 * * * *")
    public void pullKillmails() {
        getRawData().ifPresent(s -> killmailRepository.save(parseKillmails(s)));
    }

    private Optional<String> getRawData() {
        return Optional.empty();
    }
}
