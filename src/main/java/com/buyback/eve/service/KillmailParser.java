package com.buyback.eve.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.buyback.eve.domain.Killmail;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class KillmailParser {

    private static final Logger log = LoggerFactory.getLogger(KillmailParser.class);

    private KillmailParser() {
    }

    static Optional<Killmail> parseKillmail(final JSONObject object, final Long characterId) {
        if (null == object) {
            log.warn("JSONObject for characterID {} was null. Skipping.");
            return Optional.empty();
        }
        Killmail result = new Killmail();
        result.setCharacterId(characterId);
        result.setKillId(object.getLong("killID"));
        result.setSolarSystemId(object.getLong("solarSystemID"));
        result.setKillTime(object.getString("killTime"));
        result.setAttackerCount(object.getJSONArray("attackers").length());
        result.setNpc(object.getJSONObject("zkb").getBoolean("npc"));
        result.setTotalValue(object.getJSONObject("zkb").getLong("totalValue"));
        result.setPoints(object.getJSONObject("zkb").getLong("points"));
        result.setVictimId(object.getJSONObject("victim").getLong("characterID"));
        result.setVictimAlliance(object.getJSONObject("victim").getString("allianceName"));
        return Optional.of(result);
    }

    static List<Killmail> parseKillmails(final JSONArray killmailArray, final Long characterId) {
        List<Killmail> killmails = new ArrayList<>();
        for (int i = 0; i < killmailArray.length(); i++) {
            parseKillmail(killmailArray.getJSONObject(i), characterId).ifPresent(killmails::add);
        }
        return killmails;
    }
}
