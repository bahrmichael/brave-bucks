package com.buyback.eve.web.rest;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/callback")
public class CallbackResource {

    private final UserJWTController userJWTController;

    @Autowired
    public CallbackResource(final UserJWTController userJWTController) {
        this.userJWTController = userJWTController;
    }

    @GetMapping
    public ResponseEntity handleCallback(@RequestParam("code") String code, @RequestParam("state") String state, HttpServletResponse response) {
        return userJWTController.authorize(code, state, response);
    }
}
