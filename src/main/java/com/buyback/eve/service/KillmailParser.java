package com.buyback.eve.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.buyback.eve.domain.Killmail;

import org.json.JSONArray;
import org.json.JSONObject;

class KillmailParser {

    private KillmailParser() {
    }

    static Optional<Killmail> parseKillmail(final JSONObject object, final Long characterId) {
        Killmail result = new Killmail();
        result.setCharacterId(characterId);
        result.setKillId(object.getLong("killID"));
        result.setSolarSystemId(object.getLong("solarSystemID"));
        result.setKillTime(object.getString("killTime"));
        JSONArray attackers = object.getJSONArray("attackers");
        result.setAttackerCount(attackers.length());
        for (int i = 0; i < attackers.length(); i++) {
            int finalBlow = attackers.getJSONObject(i).getInt("finalBlow");
            long attackerId = attackers.getJSONObject(i).getLong("characterID");
            if (attackerId == characterId && finalBlow == 1) {
                result.setFinalBlow(true);
                break;
            }
        }
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
