package com.bravebucks.eve.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.bravebucks.eve.domain.Killmail;
import com.mashape.unirest.http.JsonNode;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.flapdoodle.embed.process.collections.Collections;

public class KillmailParserTest {

    private JsonRequestService requestService = mock(JsonRequestService.class);
    private AdmService admService = mock(AdmService.class);
    private KillmailParser sut = new KillmailParser(requestService, admService);

    @Before
    public void setUp() throws Exception {
        when(requestService.getPlayerGroupNames(1)).thenReturn(Optional.of(new JsonNode("[{\"id\": 1, \"name\": \"Goons\"}]")));
        when(requestService.getPlayerGroupNames(2)).thenReturn(Optional.of(new JsonNode("[{\"id\": 2, \"name\": \"Red Alliance\"}]")));
        when(admService.getAdm(anyLong())).thenReturn(6);
    }

    @Test
    public void withEmptyArray() throws Exception {
        List<Killmail> killmails = sut.parseKillmails(new JSONArray("[]"));
        assertTrue(killmails.isEmpty());
    }

    @Test
    public void noVictimCharacterId_returnsNull() throws Exception {
        assertNull(sut.parseKillmail(new JSONObject("{'victim': {}}")));
    }

    @Test
    public void mapJsonToKillmail() throws Exception {
        Killmail killmail = sut.parseKillmail(object1);

        assertNotNull(killmail);
        assertEquals(63894774L, killmail.getKillId());
        assertEquals(30001178L, killmail.getSolarSystemId());
        assertEquals("2017-08-05 21:23:25", killmail.getKillTime());
        assertEquals(5, killmail.getAttackerIds().size());
        assertEquals(2721466267L, killmail.getTotalValue());
        assertEquals(6, killmail.getPoints());
        assertEquals(false, killmail.isNpc());
        assertEquals(123L, killmail.getVictimId());
        assertEquals("Goons", killmail.getVictimGroupName());
        assertEquals(1L, killmail.getFinalBlowAttackerId());
    }

    @Test
    public void mapJsonToKillmail_b() throws Exception {
        Killmail killmail = sut.parseKillmail(object2);

        assertNotNull(killmail);
        assertEquals(63894773L, killmail.getKillId());
        assertEquals(30001173L, killmail.getSolarSystemId());
        assertEquals("2017-03-05 21:23:23", killmail.getKillTime());
        assertEquals(3, killmail.getAttackerIds().size());
        assertEquals(3L, killmail.getTotalValue());
        assertEquals(1, killmail.getPoints());
        assertEquals(true, killmail.isNpc());
        assertEquals(456L, killmail.getVictimId());
        assertEquals("Goons", killmail.getVictimGroupName());
        assertEquals(1L, killmail.getFinalBlowAttackerId());
    }

    @Test
    public void mapJsonArrayToKillmails() throws Exception {
        final Collection collection = Collections.newArrayList(object1, object2);
        JSONArray data = new JSONArray(collection);

        List<Killmail> killmails = sut.parseKillmails(data);

        assertNotNull(killmails);
        assertEquals(2, killmails.size());
    }

    public static final JSONObject object1 = new JSONObject("{\n"
                                                      + "        \"killmail_id\"       : 63894774,\n"
                                                      + "        \"solar_system_id\": 30001178,\n"
                                                      + "        \"killmail_time\"     : \"2017-08-05 21:23:25\",\n"
                                                      + "        \"victim\"     : {\"character_id\":123, "
                                                            + "            \"ship_type_id\": 15563\n"
                                                            + ",\"alliance_id\":\"1\"},\n"
                                                      + "        \"attackers\"    : [\n"
                                                      + "            {\"character_id\":1, \"final_blow\": true},"
                                                      + "            {\"character_id\":2, \"final_blow\": false},"
                                                      + "            {\"character_id\":3, \"final_blow\": false},"
                                                      + "            {\"character_id\":4, \"final_blow\": false},"
                                                      + "            {\"character_id\":5, \"final_blow\": false}"
                                                      + "        ],\n"
                                                      + "        \"zkb\"          : {\n"
                                                      + "            \"totalValue\" : 2721466267.32,\n"
                                                      + "            \"points\"     : 40,\n"
                                                      + "            \"npc\"        : false\n"
                                                      + "        }\n"
                                                      + "    }");


    private final JSONObject object2 = new JSONObject("{\n"
                                                      + "        \"killmail_id\"       : 63894773,\n"
                                                      + "        \"solar_system_id\": 30001173,\n"
                                                      + "        \"killmail_time\"     : \"2017-03-05 21:23:23\",\n"
                                                      + "        \"victim\"     : {\"character_id\":456, "
                                                      + "            \"ship_type_id\": 15563, "
                                                      + "\"alliance_id\":\"1\"},\n"
                                                      + "        \"attackers\"    : [\n"
                                                      + "            {\"character_id\":1, \"final_blow\": true},"
                                                      + "            {\"character_id\":2, \"final_blow\": false},"
                                                      + "            {\"character_id\":3, \"final_blow\": false}"
                                                      + "        ],\n"
                                                      + "        \"zkb\"          : {\n"
                                                      + "            \"totalValue\" : 3.32,\n"
                                                      + "            \"points\"     : 3,\n"
                                                      + "            \"npc\"        : true\n"
                                                      + "        }\n"
                                                      + "    }");

    @Test
    public void mapJsonToKillmail_issue22() throws Exception {
        Killmail killmail = sut.parseKillmail(issue22);

        assertNotNull(killmail);
        assertEquals(64870712L, killmail.getKillId());
        assertEquals(30001204L, killmail.getSolarSystemId());
        assertEquals("2017-09-24 08:18:33", killmail.getKillTime());
        assertEquals(1, killmail.getAttackerIds().size());
        assertEquals(259230154L, killmail.getTotalValue());
        assertEquals(7, killmail.getPoints());
        assertEquals(false, killmail.isNpc());
        assertEquals(90013607L, killmail.getVictimId());
        assertEquals("Red Alliance", killmail.getVictimGroupName());
        assertEquals(96919940L, killmail.getFinalBlowAttackerId());
    }

    private final JSONObject issue22 = new JSONObject("{\n"
                                                      + "        \"killmail_id\": 64870712,\n"
                                                      + "        \"solar_system_id\": 30001204,\n"
                                                      + "        \"killmail_time\": \"2017-09-24 08:18:33\",\n"
                                                      + "        \"moonID\": 0,\n"
                                                      + "        \"victim\": {\n"
                                                      + "            \"shipTypeID\": 33818,\n"
                                                      + "            \"character_id\": 90013607,\n"
                                                      + "            \"characterName\": \"Mumrik1\",\n"
                                                      + "            \"corporationID\": 1722847451,\n"
                                                      + "            \"corporationName\": \"Bad Robot Inc.\",\n"
                                                      + "            \"alliance_id\": 2,\n"
                                                      + "            \"factionID\": 0,\n"
                                                      + "            \"factionName\": \"\",\n"
                                                      + "            \"damageTaken\": 15563,\n"
                                                      + "            \"ship_type_id\": 15563\n"
                                                      + "        },\n"
                                                      + "        \"attackers\": [\n"
                                                      + "            {\n"
                                                      + "                \"character_id\": 96919940,\n"
                                                      + "                \"characterName\": \"Futility Prevails\",\n"
                                                      + "                \"corporationID\": 98169165,\n"
                                                      + "                \"corporationName\": \"Brave Newbies Inc.\",\n"
                                                      + "                \"alliance_id\": 3,\n"
                                                      + "                \"allianceName\": \"Brave Collective\",\n"
                                                      + "                \"factionID\": 0,\n"
                                                      + "                \"factionName\": \"\",\n"
                                                      + "                \"securityStatus\": 5,\n"
                                                      + "                \"damageDone\": 15563,\n"
                                                      + "                \"final_blow\": true,\n"
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

    @Test
    public void parseKillmail_nullReturnsNull() throws Exception {
        assertNull(sut.parseKillmail(null));
    }
}
