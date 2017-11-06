package com.bravebucks.eve.web.rest;

import com.bravebucks.eve.domain.Donation;

import com.bravebucks.eve.repository.DonationRepository;
import com.bravebucks.eve.security.AuthoritiesConstants;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Donation.
 */
@RestController
@RequestMapping("/api")
@Secured(AuthoritiesConstants.ADMIN)
public class DonationResource {

    private final Logger log = LoggerFactory.getLogger(DonationResource.class);

    private static final String ENTITY_NAME = "donation";

    private final DonationRepository donationRepository;
    public DonationResource(final DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    /**
     * POST  /donations : Create a new donation.
     *
     * @param donation the donation to create
     * @return the ResponseEntity with status 201 (Created) and with body the new donation, or with status 400 (Bad Request) if the donation has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/donations")
    public ResponseEntity<Donation> createDonation(@RequestBody Donation donation) throws URISyntaxException {
        log.debug("REST request to save Donation : {}", donation);
        if (donation.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new donation cannot already have an ID")).body(null);
        }
        if (!validDate(donation.getMonth())) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "invaliddate", "The date is invalid. Please use the format 20XX-XX.")).body(null);
        }
        donation.setCreated(Instant.now());
        Donation result = donationRepository.save(donation);
        return ResponseEntity.created(new URI("/api/donations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    private boolean validDate(final String month) {
        final int i = Integer.parseInt(month.split("-")[1]);
        return i >= 1 && i <= 12;
    }

    /**
     * PUT  /donations : Updates an existing donation.
     *
     * @param donation the donation to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated donation,
     * or with status 400 (Bad Request) if the donation is not valid,
     * or with status 500 (Internal Server Error) if the donation couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/donations")
    public ResponseEntity<Donation> updateDonation(@RequestBody Donation donation) throws URISyntaxException {
        log.debug("REST request to update Donation : {}", donation);
        if (donation.getId() == null) {
            return createDonation(donation);
        }
        if (!validDate(donation.getMonth())) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "invaliddate", "The date is invalid. Please use the format 20XX-XX.")).body(null);
        }
        Donation result = donationRepository.save(donation);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, donation.getId().toString()))
            .body(result);
    }

    /**
     * GET  /donations : get all the donations.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of donations in body
     */
    @GetMapping("/donations")
    public ResponseEntity<List<Donation>> getAllDonations(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Donations");
        Page<Donation> page = donationRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/donations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /donations/:id : get the "id" donation.
     *
     * @param id the id of the donation to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the donation, or with status 404 (Not Found)
     */
    @GetMapping("/donations/{id}")
    public ResponseEntity<Donation> getDonation(@PathVariable String id) {
        log.debug("REST request to get Donation : {}", id);
        Donation donation = donationRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(donation));
    }

    /**
     * DELETE  /donations/:id : delete the "id" donation.
     *
     * @param id the id of the donation to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/donations/{id}")
    public ResponseEntity<Void> deleteDonation(@PathVariable String id) {
        log.debug("REST request to delete Donation : {}", id);
        donationRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
