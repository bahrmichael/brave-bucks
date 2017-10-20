package com.buyback.eve.web.rest;

import com.buyback.eve.domain.Transaction;
import com.buyback.eve.domain.enumeration.PayoutStatus;
import com.buyback.eve.domain.enumeration.TransactionType;
import com.buyback.eve.repository.TransactionRepository;
import com.buyback.eve.security.AuthoritiesConstants;
import com.buyback.eve.security.SecurityUtils;
import com.codahale.metrics.annotation.Timed;
import com.buyback.eve.domain.Payout;

import com.buyback.eve.repository.PayoutRepository;
import com.buyback.eve.web.rest.util.HeaderUtil;
import com.buyback.eve.web.rest.util.PaginationUtil;
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
 * REST controller for managing Payout.
 */
@RestController
@RequestMapping("/api")
public class PayoutResource {

    private final Logger log = LoggerFactory.getLogger(PayoutResource.class);

    private static final String ENTITY_NAME = "payout";

    private final PayoutRepository payoutRepository;
    private final TransactionRepository transactionRepository;

    public PayoutResource(PayoutRepository payoutRepository,
                          final TransactionRepository transactionRepository) {
        this.payoutRepository = payoutRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * POST  /payouts : Create a new payout.
     *
     * @param payout the payout to create
     * @return the ResponseEntity with status 201 (Created) and with body the new payout, or with status 400 (Bad Request) if the payout has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/payouts")
    @Timed
    @Secured(AuthoritiesConstants.MANAGER)
    public ResponseEntity<Payout> createPayout(@RequestBody final Payout payout) throws URISyntaxException {
        log.debug("REST request to save Payout : {}", payout);
        if (payout.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new payout cannot already have an ID")).body(null);
        }
        payout.setLastModifiedBy(SecurityUtils.getCurrentUserLogin());
        payout.setLastUpdated(Instant.now());

        final Payout result = payoutRepository.save(payout);

        addTransactionIfPaid(result);

        return ResponseEntity.created(new URI("/api/payouts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * PUT  /payouts : Updates an existing payout.
     *
     * @param payout the payout to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated payout,
     * or with status 400 (Bad Request) if the payout is not valid,
     * or with status 500 (Internal Server Error) if the payout couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/payouts")
    @Timed
    @Secured(AuthoritiesConstants.MANAGER)
    public ResponseEntity<Payout> updatePayout(@RequestBody final Payout payout) throws URISyntaxException {
        log.debug("REST request to update Payout : {}", payout);
        if (payout.getId() == null) {
            return createPayout(payout);
        }

        final Payout existing = payoutRepository.findOne(payout.getId());
        if (null == existing) {
            return ResponseEntity.notFound().build();
        }
        if (PayoutStatus.PAID == existing.getStatus()) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "badstatus", "A payout with status PAID cannot be modified.")).body(null);
        }

        payout.setLastModifiedBy(SecurityUtils.getCurrentUserLogin());
        payout.setLastUpdated(Instant.now());

        final Payout result = payoutRepository.save(payout);

        addTransactionIfPaid(result);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, payout.getId()))
            .body(result);
    }

    private void addTransactionIfPaid(final Payout result) {
        if (PayoutStatus.PAID == result.getStatus()) {
            transactionRepository.save(new Transaction(result.getUser(), -1 * result.getAmount(), TransactionType.PAYOUT));
        }
    }

    /**
     * GET  /payouts : get all the payouts.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of payouts in body
     */
    @GetMapping("/payouts")
    @Timed
    @Secured(AuthoritiesConstants.MANAGER)
    public ResponseEntity<List<Payout>> getAllPayouts(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Payouts");
        Page<Payout> page = payoutRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/payouts");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /payouts/:id : get the "id" payout.
     *
     * @param id the id of the payout to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the payout, or with status 404 (Not Found)
     */
    @GetMapping("/payouts/{id}")
    @Timed
    @Secured(AuthoritiesConstants.MANAGER)
    public ResponseEntity<Payout> getPayout(@PathVariable String id) {
        log.debug("REST request to get Payout : {}", id);
        Payout payout = payoutRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(payout));
    }

    /**
     * DELETE  /payouts/:id : delete the "id" payout.
     *
     * @param id the id of the payout to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/payouts/{id}")
    @Timed
    @Secured(AuthoritiesConstants.MANAGER)
    public ResponseEntity<Void> deletePayout(@PathVariable String id) {
        log.debug("REST request to delete Payout : {}", id);

        final Payout existing = payoutRepository.findOne(id);
        if (null == existing) {
            return ResponseEntity.notFound().build();
        }
        if (PayoutStatus.PAID == existing.getStatus()) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "badstatus", "A payout with status PAID cannot be modified.")).body(null);
        }

        payoutRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
