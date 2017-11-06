package com.bravebucks.eve.web.rest;

import com.bravebucks.eve.domain.enumeration.AdStatus;
import com.codahale.metrics.annotation.Timed;
import com.bravebucks.eve.domain.AdRequest;

import com.bravebucks.eve.repository.AdRequestRepository;
import com.bravebucks.eve.web.rest.util.HeaderUtil;
import com.bravebucks.eve.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing AdRequest.
 */
@RestController
@RequestMapping("/api")
public class AdRequestResource {

    private final Logger log = LoggerFactory.getLogger(AdRequestResource.class);

    private static final String ENTITY_NAME = "adRequest";

    private final AdRequestRepository adRequestRepository;
    public AdRequestResource(AdRequestRepository adRequestRepository) {
        this.adRequestRepository = adRequestRepository;
    }

    /**
     * POST  /ad-requests : Create a new adRequest.
     *
     * @param adRequest the adRequest to create
     * @return the ResponseEntity with status 201 (Created) and with body the new adRequest, or with status 400 (Bad Request) if the adRequest has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/ad-requests")
    @Timed
    public ResponseEntity<AdRequest> createAdRequest(@RequestBody AdRequest adRequest) throws URISyntaxException {
        log.debug("REST request to save AdRequest : {}", adRequest);
        if (adRequest.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new adRequest cannot already have an ID")).body(null);
        }
        AdRequest result = adRequestRepository.save(adRequest);
        return ResponseEntity.created(new URI("/api/ad-requests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /ad-requests : Updates an existing adRequest.
     *
     * @param adRequest the adRequest to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated adRequest,
     * or with status 400 (Bad Request) if the adRequest is not valid,
     * or with status 500 (Internal Server Error) if the adRequest couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/ad-requests")
    public ResponseEntity<AdRequest> updateAdRequest(@RequestBody AdRequest adRequest) throws URISyntaxException {
        log.debug("REST request to update AdRequest : {}", adRequest);
        if (adRequest.getId() == null) {
            return createAdRequest(adRequest);
        }
        AdRequest result = adRequestRepository.save(adRequest);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, adRequest.getId().toString()))
            .body(result);
    }

    @GetMapping("/public/sponsored/active")
    public ResponseEntity<AdRequest> getCurrentAd() {
        AdRequest activeAd = adRequestRepository.findByAdStatus(AdStatus.ACTIVE);
        return ResponseEntity.ok(activeAd);
    }

    /**
     * GET  /ad-requests : get all the adRequests.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of adRequests in body
     */
    @GetMapping("/ad-requests")
    public ResponseEntity<List<AdRequest>> getAllAdRequests(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of AdRequests");
        Page<AdRequest> page = adRequestRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/ad-requests");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /ad-requests/:id : get the "id" adRequest.
     *
     * @param id the id of the adRequest to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the adRequest, or with status 404 (Not Found)
     */
    @GetMapping("/ad-requests/{id}")
    public ResponseEntity<AdRequest> getAdRequest(@PathVariable String id) {
        log.debug("REST request to get AdRequest : {}", id);
        AdRequest adRequest = adRequestRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(adRequest));
    }

    /**
     * DELETE  /ad-requests/:id : delete the "id" adRequest.
     *
     * @param id the id of the adRequest to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/ad-requests/{id}")
    public ResponseEntity<Void> deleteAdRequest(@PathVariable String id) {
        log.debug("REST request to delete AdRequest : {}", id);
        adRequestRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
