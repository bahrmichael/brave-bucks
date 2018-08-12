package com.bravebucks.eve.web.rest;

import com.bravebucks.eve.security.AuthoritiesConstants;
import com.bravebucks.eve.service.AllianceParser;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Secured(AuthoritiesConstants.ADMIN)
public class AdminResource {

    private final AllianceParser allianceParser;

    public AdminResource(final AllianceParser allianceParser) {
        this.allianceParser = allianceParser;
    }

    @PostMapping("/update-alliances")
    public void updateAlliances() {
        allianceParser.updateAlliances();
    }
}
