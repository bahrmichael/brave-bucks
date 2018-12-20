package com.bravebucks.eve.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.bravebucks.eve.domain.esi.AdmResponse;
import com.codahale.metrics.annotation.Timed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AdmService {

    private static final Logger LOG = LoggerFactory.getLogger(AdmService.class);
    private static final String URL = "https://esi.evetech.net/v1/sovereignty/structures/";

    private final RestTemplate restTemplate;
    private final Map<Integer, Double> systemAdms = new HashMap<>();

    public AdmService(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getAdm(final int systemId) {
        if (!systemAdms.containsKey(systemId)) {
            update();
        }
        // if esi did not return data for that systemId
        if (!systemAdms.containsKey(systemId)) {
            return 5.0;
        }
        return systemAdms.get(systemId);
    }

    @PostConstruct
    public void init() {
        update();
    }

    @Scheduled(cron = "0 30 */3 * * *")
    @Timed
    public void update() {
        final ResponseEntity<AdmResponse[]> admResponse = restTemplate.getForEntity(URL, AdmResponse[].class, new HashMap<>());

        if (admResponse.getStatusCode() != HttpStatus.OK) {
            LOG.info("ADM status code was {}. Aborting.", admResponse.getStatusCode());
        }

        for (AdmResponse adm : admResponse.getBody()) {
            systemAdms.put(adm.getSolarSystemId(), adm.getAdm());
        }
        LOG.info("ADM update complete.");
    }
}
