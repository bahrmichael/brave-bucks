package com.bravebucks.eve.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bravebucks.eve.domain.Killmail;
import com.mashape.unirest.http.JsonNode;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KillmailParser {

    private static final Logger log = LoggerFactory.getLogger(KillmailParser.class);

    private final JsonRequestService requestService;
    private final AdmService admService;

    public KillmailParser(final JsonRequestService requestService, final AdmService admService) {
        this.requestService = requestService;
        this.admService = admService;
    }

    public Killmail parseKillmail(final JSONObject object) {
        if (null == object) {
            log.error("A JSONObject was null. Skipping.");
            return null;
        }
        Killmail result = new Killmail();
        final JSONObject victim = object.getJSONObject("victim");
        if (!victim.has("character_id")) {
            // that wasn't an actual player being killed, but sth like a bubble
            return null;
        }
        result.setKillId(object.getLong("killmail_id"));
        result.setSolarSystemId(object.getLong("solar_system_id"));
        result.setKillTime(object.getString("killmail_time"));
        setAttackers(object, result);
        JSONObject zkb = object.getJSONObject("zkb");
        result.setNpc(zkb.getBoolean("npc"));
        result.setTotalValue(zkb.getLong("totalValue"));
        result.setPoints(getPoints(zkb.getLong("points"), object.getLong("solar_system_id")));
        result.setVictimId(victim.getLong("character_id"));
        result.setShipTypeId(victim.getLong("ship_type_id"));

        setGroup(result, victim);

        return result;
    }

    private long getPoints(final long points, final long solarSystemId) {
        long preSquare = points;
        final Integer adm = admService.getAdm(solarSystemId);
        if (null != adm) {
            final int factor = 6 - adm;
            if (factor > 0) {
                preSquare *= factor;
            }
        }
        return (long) Math.sqrt(preSquare);
    }

    public void setAttackers(final JSONObject object, final Killmail result) {
        JSONArray attackers = object.getJSONArray("attackers");
        for (int i = 0; i < attackers.length(); i++) {
            JSONObject attacker = attackers.getJSONObject(i);
            if (!attacker.has("character_id")) {
                // probably a rat
                continue;
            }
            long attackerId = attacker.getLong("character_id");
            result.addAttackerId(attackerId);
            boolean finalBlow = attacker.getBoolean("final_blow");
            if (finalBlow) {
                result.setFinalBlowAttackerId(attackerId);
            }
        }
    }

    public void setGroup(final Killmail result, final JSONObject victim) {
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
                    if (jsonObject.getLong("id") == groupId) {
                        result.setVictimGroupName(jsonObject.getString("name"));
                        break;
                    }
                }
            }
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
