package com.bravebucks.eve.service;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.bravebucks.eve.domain.Killmail;
import com.bravebucks.eve.domain.zkb.KillmailPackage;
import com.bravebucks.eve.domain.zkb.Participant;
import com.bravebucks.eve.domain.zkb.ZkbInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KillmailParser {

    private static final Logger log = LoggerFactory.getLogger(KillmailParser.class);

    private final UniverseNamesClient namesClient;

    public KillmailParser(final UniverseNamesClient namesClient) {
        this.namesClient = namesClient;
    }

    Killmail parseKillmail(final KillmailPackage killmailPackage) {
        if (null == killmailPackage) {
            log.error("KillmailPackage was null. Skipping.");
            return null;
        }
        final com.bravebucks.eve.domain.zkb.Killmail killmail = killmailPackage.getKillmail();
        final Participant victim = killmail.getVictim();
        if (null == victim.getCharacterId()) {
            // that wasn't an actual player being killed, but sth like a bubble
            return null;
        }

        Killmail result = new Killmail();
        result.setKillId(killmail.getKillmailId());
        result.setSolarSystemId(killmail.getSolarSystemId());
        result.setKillTime(killmail.getKillmailTime());

        // filter out rats
        result.setAttackerIds(
            Arrays.stream(killmail.getAttackers())
                  .filter(a -> null != a.getCharacterId())
                  .map(Participant::getCharacterId)
                  .collect(Collectors.toList()));

        result.setFinalBlowAttackerId(
            Arrays.stream(killmail.getAttackers())
                .filter(a -> null != a.getCharacterId())
                .filter(Participant::isFinalBlow)
                .findFirst()
                .map(Participant::getCharacterId)
                .orElse(null));

        final ZkbInfo zkb = killmailPackage.getZkb();
        result.setNpc(zkb.isNpc());
        result.setTotalValue((long) zkb.getTotalValue());
        result.setPoints(zkb.getPoints());
        result.setVictimId(victim.getCharacterId());
        result.setShipTypeId(victim.getShipTypeId());

        final Integer groupId = victim.getGroupId();
        final Integer victimId = victim.getCharacterId();
        final Map<Integer, String> names = namesClient.get(Arrays.asList(groupId, victimId));
        result.setVictimGroupName(names.get(groupId));
        result.setVictimName(names.get(victimId));

        return result;
    }

}
