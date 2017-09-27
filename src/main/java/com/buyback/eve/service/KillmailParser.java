package com.buyback.eve.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.buyback.eve.domain.Killmail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KillmailParser {

    private static final Logger log = LoggerFactory.getLogger(KillmailParser.class);

    private KillmailParser() {
    }

    public static Killmail parseKillmail(final JSONObject object) {
        if (null == object) {
            log.error("A JSONObject was null. Skipping.");
            return null;
        }
        try {
            Killmail result = new Killmail();
            result.setKillId(object.getLong("killID"));
            result.setSolarSystemId(object.getLong("solarSystemID"));
            result.setKillTime(object.getString("killTime"));
            JSONArray attackers = object.getJSONArray("attackers");
            for (int i = 0; i < attackers.length(); i++) {
                long attackerId = attackers.getJSONObject(i).getLong("characterID");
                result.addAttackerId(attackerId);
                int finalBlow = attackers.getJSONObject(i).getInt("finalBlow");
                if (finalBlow == 1) {
                    result.setFinalBlowAttackerId(attackerId);
                }
            }
            result.setNpc(object.getJSONObject("zkb").getBoolean("npc"));
            result.setTotalValue(object.getJSONObject("zkb").getLong("totalValue"));
            result.setPoints(object.getJSONObject("zkb").getLong("points"));
            result.setVictimId(object.getJSONObject("victim").getLong("characterID"));
            result.setVictimAlliance(object.getJSONObject("victim").getString("allianceName"));
            return result;
        } catch (JSONException jsonException) {
            log.warn("A Killmail could not be parsed.", jsonException);
            return null;
        }
    }

    public static List<Killmail> parseKillmails(final JSONArray killmailArray) {
        List<Killmail> killmails = new ArrayList<>();
        for (int i = 0; i < killmailArray.length(); i++) {
            Killmail killmail = parseKillmail(killmailArray.getJSONObject(i));
            if (null != killmail) {
                killmails.add(killmail);
            }
        }
        return killmails;
    }

    public static long calculateCoins(final Killmail killmail, final long characterId) {
        long points = (long) Math.sqrt((double) killmail.getPoints());
        if (killmail.getFinalBlowAttackerId() == characterId) {
            points += 2;
        }
        // todo: adm
        return points;
    }
}
