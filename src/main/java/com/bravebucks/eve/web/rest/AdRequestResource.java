package com.bravebucks.eve.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bravebucks.eve.domain.AdRequest;
import com.bravebucks.eve.domain.enumeration.AdStatus;
import com.bravebucks.eve.repository.AdRequestRepository;
import com.bravebucks.eve.security.AuthoritiesConstants;
import com.bravebucks.eve.security.SecurityUtils;
import com.bravebucks.eve.web.rest.util.HeaderUtil;
import com.bravebucks.eve.web.rest.util.PaginationUtil;
import com.codahale.metrics.annotation.Timed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;

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

    @GetMapping("/ad-requests/pending")
    @Timed
    @Secured(AuthoritiesConstants.MANAGER)
    public ResponseEntity<Integer> countPendingPayouts() {
        return ResponseEntity.ok(adRequestRepository.countByAdStatus(AdStatus.REQUESTED));
    }

    /**
     * POST  /ad-requests : Create a new adRequest.
     *
     * @param adRequest the adRequest to create
     * @return the ResponseEntity with status 201 (Created) and with body the new adRequest, or with status 400 (Bad Request) if the adRequest has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/ad-requests")
    public ResponseEntity createAdRequest(@RequestBody AdRequest adRequest) throws URISyntaxException {
        log.debug("REST request to save AdRequest : {}", adRequest);
        if (adRequest.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new adRequest cannot already have an ID")).body(null);
        }

        List<AdRequest> existingAds = adRequestRepository.findByMonth(adRequest.getMonth());
        if (existingAds.size() >= 5) {
            return ResponseEntity.status(409).body("This month is already booked out. Please choose another one.");
        }

        Optional<AdRequest> optional = adRequestRepository.findByServiceAndMonth(adRequest.getService(), adRequest.getMonth());
        if (optional.isPresent()) {
            return ResponseEntity.status(409).body("There is already an ad requested for this service and month. Please choose another month.");
        }

        adRequest.setRequester(SecurityUtils.getCurrentUserLogin());
        adRequest.setAdStatus(AdStatus.REQUESTED);

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
    @PreAuthorize("hasRole('ROLE_MANAGER')")
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

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/ad-requests/available-months")
    public ResponseEntity<List<String>> getAvailableMonths() {
        List<String> availableMonths = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 1; i <= 12; i++) {
            String month = now.plusMonths(i).getYear() + "-" + now.plusMonths(i).getMonthValue();
            if (adRequestRepository.findByMonth(month).size() < 5) {
                availableMonths.add(month);
            }
        }
        return ResponseEntity.ok(availableMonths);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/ad-requests/active")
    public ResponseEntity<AdRequest> getCurrentAd() {
        Optional<AdRequest> activeAd = adRequestRepository.findByAdStatus(AdStatus.ACTIVE).stream().findAny();
        if (activeAd.isPresent()) {
            return ResponseEntity.ok(activeAd.get());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * GET  /ad-requests : get all the adRequests.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of adRequests in body
     */
    @PreAuthorize("hasRole('ROLE_MANAGER')")
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
    @PreAuthorize("hasRole('ROLE_MANAGER')")
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
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/ad-requests/{id}")
    public ResponseEntity<Void> deleteAdRequest(@PathVariable String id) {
        log.debug("REST request to delete AdRequest : {}", id);
        adRequestRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
