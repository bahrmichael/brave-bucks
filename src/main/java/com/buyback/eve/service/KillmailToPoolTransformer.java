package com.buyback.eve.service;

import javax.annotation.PostConstruct;

import com.buyback.eve.repository.KillmailRepository;
import com.codahale.metrics.annotation.Timed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class KillmailToPoolTransformer {

    private final KillmailRepository killmailRepository;
    private final PoolService poolService;

    @Autowired
    public KillmailToPoolTransformer(final KillmailRepository killmailRepository,
                                     final PoolService poolService) {
        this.killmailRepository = killmailRepository;
        this.poolService = poolService;
    }

    @PostConstruct
    public void init() {
        addKillmailsToPool();
    }

    @Async
    @Timed
    @Scheduled(cron = "0 */10 * * * *")
    public void addKillmailsToPool() {
        killmailRepository.findAll().stream().filter(DateUtil::isCurrentMonth).forEach(poolService::addKillmailIfNotExists);
    }
}
