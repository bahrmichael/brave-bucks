package com.buyback.eve.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.buyback.eve.domain.Killmail;
import com.mashape.unirest.http.JsonNode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KillmailParser {

    private static final Logger log = LoggerFactory.getLogger(KillmailParser.class);

    private final JsonRequestService requestService;

    public KillmailParser(final JsonRequestService requestService) {
        this.requestService = requestService;
    }

    public Killmail parseKillmail(final JSONObject object) {
        if (null == object) {
            log.error("A JSONObject was null. Skipping.");
            return null;
        }
        try {
            Killmail result = new Killmail();
            result.setKillId(object.getLong("killmail_id"));
            result.setSolarSystemId(object.getLong("solar_system_id"));
            result.setKillTime(object.getString("killmail_time"));
            JSONArray attackers = object.getJSONArray("attackers");
            for (int i = 0; i < attackers.length(); i++) {
                long attackerId = attackers.getJSONObject(i).getLong("character_id");
                result.addAttackerId(attackerId);
                boolean finalBlow = attackers.getJSONObject(i).getBoolean("final_blow");
                if (finalBlow) {
                    result.setFinalBlowAttackerId(attackerId);
                }
            }
            result.setNpc(object.getJSONObject("zkb").getBoolean("npc"));
            result.setTotalValue(object.getJSONObject("zkb").getLong("totalValue"));
            result.setPoints(object.getJSONObject("zkb").getLong("points"));
            final JSONObject victim = object.getJSONObject("victim");
            if (!victim.has("character_id")) {
                // that wasn't an actual player being killed, but sth like a bubble
                return null;
            }
            result.setVictimId(victim.getLong("character_id"));

            Long groupId = null;
            if (victim.has("alliance_id")) {
                groupId = victim.getLong("alliance_id");
            } else if (victim.has("corporation_id")) {
                groupId = victim.getLong("corporation_id");
            }

            if (null != groupId) {
                final Optional<JsonNode> playerGroupNames = requestService.getPlayerGroupNames(groupId);
                if (playerGroupNames.isPresent()) {
                    final JSONArray array = playerGroupNames.get().getArray();
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject jsonObject = array.getJSONObject(i);
                        if (jsonObject.getLong("alliance_id") == groupId) {
                            result.setVictimGroupName(jsonObject.getString("alliance_name"));
                            break;
                        }
                    }
                }
            }

            return result;
        } catch (final JSONException jsonException) {
            log.warn("A Killmail could not be parsed.", jsonException);
            return null;
        }
    }

    public List<Killmail> parseKillmails(final JSONArray killmailArray) {
        List<Killmail> killmails = new ArrayList<>();
        for (int i = 0; i < killmailArray.length(); i++) {
            Killmail killmail = parseKillmail(killmailArray.getJSONObject(i));
            if (null != killmail) {
                killmails.add(killmail);
            }
        }
        return killmails;
    }
}
