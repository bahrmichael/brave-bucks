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
    public void mapJsonToKillmail() throws Exception {
        Optional<Killmail> optional = KillmailParser.parseKillmail(object1, 1L);

        assertNotNull(optional);
        assertTrue(optional.isPresent());
        Killmail killmail = optional.get();
        assertEquals(1L, killmail.getCharacterId());
        assertEquals(63894774L, killmail.getKillId());
        assertEquals(30001178L, killmail.getSolarSystemId());
        assertEquals("2017-08-05 21:23:25", killmail.getKillTime());
        assertEquals(5, killmail.getAttackerCount());
        assertEquals(2721466267L, killmail.getTotalValue());
        assertEquals(40, killmail.getPoints());
        assertEquals(false, killmail.isNpc());
    }

    @Test
    public void mapJsonToKillmail_b() throws Exception {
        Optional<Killmail> optional = KillmailParser.parseKillmail(object2, 2L);

        assertNotNull(optional);
        assertTrue(optional.isPresent());
        Killmail killmail = optional.get();
        assertEquals(2L, killmail.getCharacterId());
        assertEquals(63894773L, killmail.getKillId());
        assertEquals(30001173L, killmail.getSolarSystemId());
        assertEquals("2017-03-05 21:23:23", killmail.getKillTime());
        assertEquals(3, killmail.getAttackerCount());
        assertEquals(3L, killmail.getTotalValue());
        assertEquals(3, killmail.getPoints());
        assertEquals(true, killmail.isNpc());
    }

    @Test
    public void mapJsonArrayToKillmails() throws Exception {
        final Collection collection = Collections.newArrayList(object1, object2);
        JSONArray data = new JSONArray(collection);

        List<Killmail> killmails = KillmailParser.parseKillmails(data, 1L);

        assertNotNull(killmails);
        assertEquals(2, killmails.size());
    }

    private final JSONObject object1 = new JSONObject("{\n"
                                                      + "        \"killID\"       : 63894774,\n"
                                                      + "        \"solarSystemID\": 30001178,\n"
                                                      + "        \"killTime\"     : \"2017-08-05 21:23:25\",\n"
                                                      + "        \"attackers\"    : [\n"
                                                      + "            {\n"
                                                      + "            },\n"
                                                      + "            {\n"
                                                      + "            },\n"
                                                      + "            {\n"
                                                      + "            },\n"
                                                      + "            {\n"
                                                      + "            },\n"
                                                      + "            {\n"
                                                      + "            }\n"
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
                                                      + "        \"attackers\"    : [\n"
                                                      + "            {\n"
                                                      + "            },\n"
                                                      + "            {\n"
                                                      + "            },\n"
                                                      + "            {\n"
                                                      + "            }\n"
                                                      + "        ],\n"
                                                      + "        \"zkb\"          : {\n"
                                                      + "            \"totalValue\" : 3.32,\n"
                                                      + "            \"points\"     : 3,\n"
                                                      + "            \"npc\"        : true\n"
                                                      + "        }\n"
                                                      + "    }");
}
