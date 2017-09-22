package com.buyback.eve.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.buyback.eve.domain.Killmail;
import com.buyback.eve.domain.Pool;
import com.buyback.eve.repository.KillmailRepository;
import com.buyback.eve.repository.PoolRepository;
import com.codahale.metrics.annotation.Timed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KillmailToPoolTransformer {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final KillmailRepository killmailRepository;
    private final PoolRepository poolRepository;

    @Autowired
    public KillmailToPoolTransformer(final KillmailRepository killmailRepository, final PoolRepository poolRepository) {
        this.killmailRepository = killmailRepository;
        this.poolRepository = poolRepository;
    }

    @Async
    @Timed
    @Scheduled(cron = "0 */10 * * * *")
    public void addKillmailsToPool() {
        String yearMonth = getYearMonth(LocalDate.now());
        Optional<Pool> optional = poolRepository.findByYearMonth(yearMonth);
        Pool pool;
        if (optional.isPresent()) {
            pool = optional.get();
        } else {
            pool = new Pool();
            pool.setYearMonth(getYearMonth(LocalDate.now()));
            pool.setBalance(0L);
        }
        killmailRepository.findAll().stream().filter(KillmailToPoolTransformer::isCurrentMonth).forEach(pool::addKillmailIfNotExists);
        poolRepository.save(pool);
    }

    public static boolean isCurrentMonth(final Killmail killmail) {
        LocalDateTime killTime = LocalDateTime.parse(killmail.getKillTime(), FORMATTER);
        LocalDate now = LocalDate.now();
        return killTime.getYear() == now.getYear() && killTime.getMonthValue() == now.getMonthValue();
    }

    public static String getYearMonth(final LocalDate localDate) {
        return String.format("%d-%02d", localDate.getYear(), localDate.getMonthValue());
    }
}
