package com.bravebucks.eve.web.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
public class ConfigResource {

    @Value("${SSO_URL}")
    private String SSO_URL;

    @Value("${WALLET_URL}")
    private String WALLET_URL;

    @GetMapping(path = "/ssourl")
    public String getSsoUrl() {
        return SSO_URL;
    }

    @GetMapping(path = "/walleturl")
    public String getWalletUrl() {
        return WALLET_URL;
    }

}
