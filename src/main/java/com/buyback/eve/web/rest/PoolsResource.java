package com.buyback.eve.web.rest;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.buyback.eve.domain.Pool;
import com.buyback.eve.repository.PoolRepository;

import static com.buyback.eve.service.DateUtil.getYearMonth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PoolsResource {

    private final PoolRepository poolRepository;

    public PoolsResource(final PoolRepository poolRepository) {
        this.poolRepository = poolRepository;
    }

    @GetMapping(path = "/pools/isk")
    public ResponseEntity getPlayerStats() {
        return ResponseEntity.ok(Stream.of(next(), current(), previous()).collect(Collectors.toList()));
    }

    @GetMapping(path = "/pools/current/exchange")
    public ResponseEntity getCurrentExchange() {
        Optional<Pool> optional = poolRepository.findByYearMonth(getYearMonth(LocalDate.now()));
        if (optional.isPresent()) {
            Pool pool = optional.get();
            Long balance = pool.getBalance() == null ? 0L : pool.getBalance();
            Long claimedCoins = pool.getClaimedCoins() == null ? 1L : pool.getClaimedCoins();
            if (claimedCoins == 0) {
                return ResponseEntity.ok(0L);
            }
            long rate = balance / claimedCoins;
            return ResponseEntity.ok(rate);
        } else {
            return ResponseEntity.ok(0L);
        }
    }

    private Long previous() {
        Optional<Pool> optional = poolRepository.findByYearMonth(getYearMonth(LocalDate.now().minusDays(30)));
        return optional.map(Pool::getBalance).orElse(0L);
    }

    private Long current() {
        Optional<Pool> optional = poolRepository.findByYearMonth(getYearMonth(LocalDate.now()));
        return optional.map(Pool::getBalance).orElse(0L);
    }

    private Long next() {
        Optional<Pool> optional = poolRepository.findByYearMonth(getYearMonth(LocalDate.now().plusDays(30)));
        return optional.map(Pool::getBalance).orElse(0L);
    }
}
