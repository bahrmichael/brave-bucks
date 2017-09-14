package com.buyback.eve.web.rest;

import com.buyback.eve.domain.PlayerStats;
import com.buyback.eve.service.PlayerStatsService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PlayerStatsResource {

    private final PlayerStatsService playerStatsService;

    public PlayerStatsResource(final PlayerStatsService playerStatsService) {
        this.playerStatsService = playerStatsService;
    }

    @GetMapping(path = "/stats/my")
    public ResponseEntity getPlayerStats() {
        PlayerStats statsForCurrentUser = playerStatsService.getStatsForCurrentUser();
        return ResponseEntity.ok(statsForCurrentUser);
    }
}
