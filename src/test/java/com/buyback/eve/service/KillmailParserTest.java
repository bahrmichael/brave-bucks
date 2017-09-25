package com.buyback.eve.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.buyback.eve.domain.Killmail;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;

import de.flapdoodle.embed.process.collections.Collections;

public class KillmailParserTest {

    // todo: error handling for broken json

    @Test
    public void calculatePoints() throws Exception {
        final Killmail killmail = new Killmail();
        killmail.setPoints(1L);
        killmail.setFinalBlowAttackerId(2L);

        long points = KillmailParser.calculateCoins(killmail, 9L);

        assertEquals(1L, points);
    }

    @Test
    public void calculatePoints_finalBlow() throws Exception {
        final Killmail killmail = new Killmail();
        killmail.setPoints(2L);
        killmail.setFinalBlowAttackerId(3L);

        long points = KillmailParser.calculateCoins(killmail, 3L);

        assertEquals(3L, points);
    }

    @Test
    public void withEmptyArray() throws Exception {
        List<Killmail> killmails = KillmailParser.parseKillmails(new JSONArray("[]"));
        assertTrue(killmails.isEmpty());
    }

    @Test
    public void mapJsonToKillmail() throws Exception {
        Killmail killmail = KillmailParser.parseKillmail(object1);

        assertNotNull(killmail);
        assertEquals(63894774L, killmail.getKillId());
        assertEquals(30001178L, killmail.getSolarSystemId());
        assertEquals("2017-08-05 21:23:25", killmail.getKillTime());
        assertEquals(5, killmail.getAttackerIds().size());
        assertEquals(2721466267L, killmail.getTotalValue());
        assertEquals(40, killmail.getPoints());
        assertEquals(false, killmail.isNpc());
        assertEquals(123L, killmail.getVictimId());
        assertEquals("Goons", killmail.getVictimAlliance());
        assertEquals(1L, killmail.getFinalBlowAttackerId());
    }

    @Test
    public void mapJsonToKillmail_b() throws Exception {
        Killmail killmail = KillmailParser.parseKillmail(object2);

        assertNotNull(killmail);
        assertEquals(63894773L, killmail.getKillId());
        assertEquals(30001173L, killmail.getSolarSystemId());
        assertEquals("2017-03-05 21:23:23", killmail.getKillTime());
        assertEquals(3, killmail.getAttackerIds().size());
        assertEquals(3L, killmail.getTotalValue());
        assertEquals(3, killmail.getPoints());
        assertEquals(true, killmail.isNpc());
        assertEquals(456L, killmail.getVictimId());
        assertEquals("Goons", killmail.getVictimAlliance());
        assertEquals(1L, killmail.getFinalBlowAttackerId());
    }

    @Test
    public void mapJsonArrayToKillmails() throws Exception {
        final Collection collection = Collections.newArrayList(object1, object2);
        JSONArray data = new JSONArray(collection);

        List<Killmail> killmails = KillmailParser.parseKillmails(data);

        assertNotNull(killmails);
        assertEquals(2, killmails.size());
    }

    public static final JSONObject object1 = new JSONObject("{\n"
                                                      + "        \"killID\"       : 63894774,\n"
                                                      + "        \"solarSystemID\": 30001178,\n"
                                                      + "        \"killTime\"     : \"2017-08-05 21:23:25\",\n"
                                                      + "        \"victim\"     : {\"characterID\":123, \"allianceName\":\"Goons\"},\n"
                                                      + "        \"attackers\"    : [\n"
                                                      + "            {\"characterID\":1, \"finalBlow\": 1},"
                                                      + "            {\"characterID\":2, \"finalBlow\": 0},"
                                                      + "            {\"characterID\":3, \"finalBlow\": 0},"
                                                      + "            {\"characterID\":4, \"finalBlow\": 0},"
                                                      + "            {\"characterID\":5, \"finalBlow\": 0}"
                                                      + "        ],\n"
                                                      + "        \"zkb\"          : {\n"
                                                      + "            \"totalValue\" : 2721466267.32,\n"
                                                      + "            \"points\"     : 40,\n"
                                                      + "            \"npc\"        : false\n"
                                                      + "        }\n"
                                                      + "    }");


    private final JSONObject object2 = new JSONObject("{\n"
                                                      + "        \"killID\"       : 63894773,\n"
                                                      + "        \"solarSystemID\": 30001173,\n"
                                                      + "        \"killTime\"     : \"2017-03-05 21:23:23\",\n"
                                                      + "        \"victim\"     : {\"characterID\":456, \"allianceName\":\"Goons\"},\n"
                                                      + "        \"attackers\"    : [\n"
                                                      + "            {\"characterID\":1, \"finalBlow\": 1},"
                                                      + "            {\"characterID\":2, \"finalBlow\": 0},"
                                                      + "            {\"characterID\":3, \"finalBlow\": 0}"
                                                      + "        ],\n"
                                                      + "        \"zkb\"          : {\n"
                                                      + "            \"totalValue\" : 3.32,\n"
                                                      + "            \"points\"     : 3,\n"
                                                      + "            \"npc\"        : true\n"
                                                      + "        }\n"
                                                      + "    }");

    @Test
    public void mapJsonToKillmail_issue22() throws Exception {
        Killmail killmail = KillmailParser.parseKillmail(issue22);

        assertNotNull(killmail);
        assertEquals(64870712L, killmail.getKillId());
        assertEquals(30001204L, killmail.getSolarSystemId());
        assertEquals("2017-09-24 08:18:33", killmail.getKillTime());
        assertEquals(1, killmail.getAttackerIds().size());
        assertEquals(259230154L, killmail.getTotalValue());
        assertEquals(50, killmail.getPoints());
        assertEquals(false, killmail.isNpc());
        assertEquals(90013607L, killmail.getVictimId());
        assertEquals("Red Alliance", killmail.getVictimAlliance());
        assertEquals(96919940L, killmail.getFinalBlowAttackerId());
    }

    private final JSONObject issue22 = new JSONObject("{\n"
                                                      + "        \"killID\": 64870712,\n"
                                                      + "        \"solarSystemID\": 30001204,\n"
                                                      + "        \"killTime\": \"2017-09-24 08:18:33\",\n"
                                                      + "        \"moonID\": 0,\n"
                                                      + "        \"victim\": {\n"
                                                      + "            \"shipTypeID\": 33818,\n"
                                                      + "            \"characterID\": 90013607,\n"
                                                      + "            \"characterName\": \"Mumrik1\",\n"
                                                      + "            \"corporationID\": 1722847451,\n"
                                                      + "            \"corporationName\": \"Bad Robot Inc.\",\n"
                                                      + "            \"allianceID\": 1220922756,\n"
                                                      + "            \"allianceName\": \"Red Alliance\",\n"
                                                      + "            \"factionID\": 0,\n"
                                                      + "            \"factionName\": \"\",\n"
                                                      + "            \"damageTaken\": 15563\n"
                                                      + "        },\n"
                                                      + "        \"attackers\": [\n"
                                                      + "            {\n"
                                                      + "                \"characterID\": 96919940,\n"
                                                      + "                \"characterName\": \"Futility Prevails\",\n"
                                                      + "                \"corporationID\": 98169165,\n"
                                                      + "                \"corporationName\": \"Brave Newbies Inc.\",\n"
                                                      + "                \"allianceID\": 99003214,\n"
                                                      + "                \"allianceName\": \"Brave Collective\",\n"
                                                      + "                \"factionID\": 0,\n"
                                                      + "                \"factionName\": \"\",\n"
                                                      + "                \"securityStatus\": 5,\n"
                                                      + "                \"damageDone\": 15563,\n"
                                                      + "                \"finalBlow\": 1,\n"
                                                      + "                \"weaponTypeID\": 2175,\n"
                                                      + "                \"shipTypeID\": 16233\n"
                                                      + "            }\n"
                                                      + "        ],\n"
                                                      + "        \"position\": {\n"
                                                      + "            \"y\": -231065645588.13,\n"
                                                      + "            \"x\": 2009892244049.2,\n"
                                                      + "            \"z\": -3717810205848.4\n"
                                                      + "        },\n"
                                                      + "        \"zkb\": {\n"
                                                      + "            \"locationID\": 50003029,\n"
                                                      + "            \"hash\": "
                                                      + "\"771ad0db14e8ab1b71888aba7fbbf503571bbce9\",\n"
                                                      + "            \"fittedValue\": 254083935.79,\n"
                                                      + "            \"totalValue\": 259230154.52,\n"
                                                      + "            \"points\": 50,\n"
                                                      + "            \"npc\": false\n"
                                                      + "        }\n"
                                                      + "    }");
}
