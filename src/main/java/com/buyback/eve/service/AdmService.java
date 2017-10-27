package com.buyback.eve.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import com.mashape.unirest.http.JsonNode;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * AdmService
 *
 * Created on 06.10.2017
 *
 * Copyright (C) 2017 Volkswagen AG, All rights reserved.
 */
@Service
public class AdmService {

    private static final Logger LOG = LoggerFactory.getLogger(AdmService.class);

    private final JsonRequestService service;
    private final Map<Long, Integer> systemAdms = new HashMap<>();

    public AdmService(final JsonRequestService service) {
        this.service = service;
    }

    public Integer getAdm(final long systemId) {
        if (!systemAdms.containsKey(systemId)) {
            update();
        }
        return systemAdms.get(systemId);
    }

    @PostConstruct
    public void init() {
        update();
    }

    @Scheduled(cron = "0 15 * * * *")
    public void update() {
        final Optional<JsonNode> optional = service.getAdms();
        optional.ifPresent(jsonNode -> {
            final JSONArray array = jsonNode.getArray();
            for (int i = 0; i < array.length(); i++) {
                final JSONObject jsonObject = array.getJSONObject(i);
                final long solarSystemId = jsonObject.getLong("solar_system_id");
                final int adm;
                if (jsonObject.has("vulnerability_occupancy_level")) {
                    adm = jsonObject.getInt("vulnerability_occupancy_level");
                } else {
                    LOG.warn("Could not load ADM for {}", solarSystemId);
                    adm = 5;
                }
                systemAdms.put(solarSystemId, adm);
            }
        });
    }
}
