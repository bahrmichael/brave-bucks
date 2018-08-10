package com.bravebucks.eve.web.rest;

import com.bravebucks.eve.domain.enumeration.Region;
import com.bravebucks.eve.security.AuthoritiesConstants;
import com.bravebucks.eve.service.JsonRequestService;
import com.bravebucks.eve.domain.SolarSystem;

import com.bravebucks.eve.repository.SolarSystemRepository;
import com.bravebucks.eve.web.rest.util.HeaderUtil;
import com.bravebucks.eve.web.rest.util.PaginationUtil;
import com.codahale.metrics.annotation.Timed;
import com.mashape.unirest.http.JsonNode;

import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing SolarSystem.
 */
@RestController
@RequestMapping("/api")
public class SolarSystemResource {

    private final Logger log = LoggerFactory.getLogger(SolarSystemResource.class);

    private static final String ENTITY_NAME = "solarSystem";

    private final SolarSystemRepository solarSystemRepository;
    private final JsonRequestService jsonRequestService;
    public SolarSystemResource(SolarSystemRepository solarSystemRepository,
                               final JsonRequestService jsonRequestService) {
        this.solarSystemRepository = solarSystemRepository;
        this.jsonRequestService = jsonRequestService;
    }

    /**
     * POST  /solar-systems : Create a new solarSystem.
     *
     * @param solarSystem the solarSystem to create
     * @return the ResponseEntity with status 201 (Created) and with body the new solarSystem, or with status 400 (Bad Request) if the solarSystem has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @Secured(AuthoritiesConstants.MANAGER)
    @PostMapping("/solar-systems")
    public ResponseEntity<SolarSystem> createSolarSystem(@RequestBody SolarSystem solarSystem) throws URISyntaxException {
        log.debug("REST request to save SolarSystem : {}", solarSystem);
        if (solarSystem.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new solarSystem cannot already have an ID")).body(null);
        }

        solarSystem.setSystemName(solarSystem.getSystemName().toUpperCase());

        Optional<JsonNode> optional = jsonRequestService.searchSolarSystem(solarSystem.getSystemName());
        if (optional.isPresent()) {
            JSONObject object = optional.get().getObject();
            if (object.has("solar_system")) {
                long systemId = object.getJSONArray("solar_system").getLong(0);
                solarSystem.setSystemId(systemId);
                SolarSystem result = solarSystemRepository.save(solarSystem);
                return ResponseEntity.created(new URI("/api/solar-systems/" + result.getId()))
                                     .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                                     .body(result);
            }
        }
        return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "notresolved",
                                                                                 "The system could not be found. Is there a typo?")).body(null);
    }

    /**
     * PUT  /solar-systems : Updates an existing solarSystem.
     *
     * @param solarSystem the solarSystem to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated solarSystem,
     * or with status 400 (Bad Request) if the solarSystem is not valid,
     * or with status 500 (Internal Server Error) if the solarSystem couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @Secured(AuthoritiesConstants.MANAGER)
    @PutMapping("/solar-systems")
    public ResponseEntity<SolarSystem> updateSolarSystem(@RequestBody SolarSystem solarSystem) throws URISyntaxException {
        log.debug("REST request to update SolarSystem : {}", solarSystem);
        if (solarSystem.getId() == null) {
            return createSolarSystem(solarSystem);
        }
        SolarSystem result = solarSystemRepository.save(solarSystem);
        return ResponseEntity.ok()
                             .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, solarSystem.getId()))
                             .body(result);
    }

    @Secured(AuthoritiesConstants.USER)
    @GetMapping("/solar-systems/region/{region}")
    public ResponseEntity<List<SolarSystem>> getSystems(@PathVariable("region") final Region region) {
        return ResponseEntity.ok(solarSystemRepository.findByRegion(region));
    }

    /**
     * GET  /solar-systems : get all the solarSystems.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of solarSystems in body
     */
    @Secured(AuthoritiesConstants.MANAGER)
    @GetMapping("/solar-systems")
    public ResponseEntity<List<SolarSystem>> getAllSolarSystems(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of SolarSystems");
        Page<SolarSystem> page = solarSystemRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/solar-systems");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /solar-systems/:id : get the "id" solarSystem.
     *
     * @param id the id of the solarSystem to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the solarSystem, or with status 404 (Not Found)
     */
    @Secured(AuthoritiesConstants.MANAGER)
    @GetMapping("/solar-systems/{id}")
    public ResponseEntity<SolarSystem> getSolarSystem(@PathVariable String id) {
        log.debug("REST request to get SolarSystem : {}", id);
        SolarSystem solarSystem = solarSystemRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(solarSystem));
    }
    /**
     * DELETE  /solar-systems/:id : delete the "id" solarSystem.
     *
     * @param id the id of the solarSystem to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @Secured(AuthoritiesConstants.MANAGER)
    @DeleteMapping("/solar-systems/{id}")
    public ResponseEntity<Void> deleteSolarSystem(@PathVariable String id) {
        log.debug("REST request to delete SolarSystem : {}", id);
        solarSystemRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
