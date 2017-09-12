package com.buyback.eve.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Killmail {
    private Long characterId;
    private long killId;
    private long solarSystemId;
    private String killTime;
    private long attackerCount;
    private long totalValue;
    private long points;
    private boolean npc;
}
