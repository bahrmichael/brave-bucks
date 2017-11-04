package com.bravebucks.eve.web.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
public class ConfigResource {

    @Value("${SSO_URL}")
    private String SSO_URL;

    public ConfigResource() { }

    ConfigResource(String ssoUrl) {
        SSO_URL = ssoUrl;
    }

    @GetMapping(path = "/ssourl")
    public ResponseEntity<String> getSsoUrl() {
        return new ResponseEntity<>(SSO_URL, HttpStatus.OK);
    }

}
