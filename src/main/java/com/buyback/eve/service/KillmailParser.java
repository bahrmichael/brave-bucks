package com.buyback.eve.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.buyback.eve.domain.Killmail;

import org.json.JSONArray;
import org.json.JSONObject;

import springfox.documentation.spring.web.json.Json;

class KillmailParser {

    private KillmailParser() {
    }

    static Optional<Killmail> parseKillmail(final String singleKillmail) {
        return parseKillmail(new JSONObject(singleKillmail));
    }

    private static Optional<Killmail> parseKillmail(final JSONObject object) {
        Killmail result = new Killmail();
        result.setKillId(object.getLong("killID"));
        result.setSolarSystemId(object.getLong("solarSystemID"));
        result.setKillTime(object.getString("killTime"));
        result.setAttackerCount(object.getJSONArray("attackers").length());
        result.setNpc(object.getJSONObject("zkb").getBoolean("npc"));
        result.setTotalValue(object.getJSONObject("zkb").getLong("totalValue"));
        result.setPoints(object.getJSONObject("zkb").getLong("points"));
        return Optional.of(result);
    }

    static List<Killmail> parseKillmails(final String killmailArray) {
        List<Killmail> killmails = new ArrayList<>();
        JSONArray array = new JSONArray(killmailArray);
        for (int i = 0; i < array.length(); i++) {
            parseKillmail(array.getJSONObject(i)).ifPresent(killmails::add);
        }
        return killmails;
    }
}
