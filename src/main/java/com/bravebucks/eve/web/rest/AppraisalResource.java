package com.bravebucks.eve.web.rest;

import com.bravebucks.eve.domain.Appraisal;
import com.codahale.metrics.annotation.Timed;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST controller for executing appraisal requests.
 */
@RestController
@RequestMapping("/api/appraisal")
public class AppraisalResource {

    private final Logger log = LoggerFactory.getLogger(AppraisalResource.class);

    /**
     * POST  / : Execute an appraisal request.
     *
     * @param appraisal the appraisal to request/update
     * @return the executed/updated Appraisal
     */
    @PostMapping
    @Timed
    public ResponseEntity<Appraisal> requestAppraisal(@RequestBody Appraisal appraisal) {
        log.debug("REST request to perform an appraisal : {}", appraisal);

        if (appraisal.getAdditionalRaw().isEmpty()) {
            return ResponseEntity.status(400).build();
        }

        try {
            executeRequestAndUpdateAppraisal(appraisal);
        } catch (UnirestException e) {
            log.error("Unirest failed.", e);
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.ok().body(appraisal);
    }

    private void executeRequestAndUpdateAppraisal(final Appraisal appraisal) throws UnirestException {
        appraisal.updateRaw();
        appraisal.setLink(AppraisalUtil.getLinkFromRaw(appraisal.getRaw()));
        appraisal.setTotalBuy(AppraisalUtil.getBuy(appraisal.getLink()));
        appraisal.setItems(AppraisalUtil.getItems(appraisal.getLink()));
    }
}
