package com.buyback.eve.service;

import javax.annotation.PostConstruct;

import com.buyback.eve.repository.KillmailRepository;
import com.codahale.metrics.annotation.Timed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PayoutCalculator {

    private final KillmailRepository killmailRepository;

    @Autowired
    public PayoutCalculator(final KillmailRepository killmailRepository) {
        this.killmailRepository = killmailRepository;
    }

    @PostConstruct
    public void init() {
        calculatePayouts();
    }

    @Async
    @Timed
    @Scheduled(cron = "0 0 * * * *")
    public void calculatePayouts() {
//        killmailRepository.findAll().stream().filter(DateUtil::isCurrentMonth).forEach(poolService::addKillmailIfNotExists);
    }
}
