package com.buyback.eve.web.rest;

import com.buyback.eve.service.PlayerStatsService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PlayerStatsResource {

    private PlayerStatsService playerStatsService;

    @GetMapping(path = "/stats")
    public ResponseEntity getPlayerStats() {
        return ResponseEntity.ok(playerStatsService.getStatsForCurrentUser());
    }
}
