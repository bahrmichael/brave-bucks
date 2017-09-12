package com.buyback.eve.web.rest;

import java.util.Collections;

import javax.servlet.http.HttpServletResponse;

import com.buyback.eve.repository.UserRepository;
import com.buyback.eve.security.AuthoritiesConstants;
import com.buyback.eve.security.jwt.JWTConfigurer;
import com.buyback.eve.security.jwt.TokenProvider;
import com.buyback.eve.service.UserService;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class UserJWTController {

    private final Logger log = LoggerFactory.getLogger(UserJWTController.class);

    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserJWTController(TokenProvider tokenProvider, AuthenticationManager authenticationManager,
                             UserRepository userRepository, UserService userService,
                             final BCryptPasswordEncoder passwordEncoder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/authenticate/sso")
    @Timed
    public ResponseEntity authorize(@RequestParam("code") String code, @RequestParam("state") String state, HttpServletResponse response) {

        String characterName;
        try {
            String url = "https://login.eveonline.com/oauth/token";
            HttpResponse<JsonNode> tokenResponse = Unirest.post(url)
                                  .basicAuth("cd26e57f99cf4cc5b7311164cc64edd2",
                                                                    "DgR7MhUAa3l0VNhgaABHE4LsF9WGSnXRZEepOtjh")
                                  .header("Content-Type", "application/x-www-form-urlencoded")
                                  .field("grant_type", "authorization_code")
                                  .field("code", code).asJson();
            int status = tokenResponse.getStatus();
            String access_token = tokenResponse.getBody().getObject().getString("access_token");

            HttpResponse<JsonNode> details = Unirest.get("https://login.eveonline.com/oauth/verify")
                                                    .header("Authorization", "Bearer " + access_token)
                                                    .asJson();
            characterName = details.getBody().getObject().getString("CharacterName");
        } catch (UnirestException e) {
            log.trace("SSO exception trace: {}", e);
            return new ResponseEntity<>(Collections.singletonMap("AuthenticationException",
                                                                 e.getLocalizedMessage()), HttpStatus.UNAUTHORIZED);
        }

        if (!userRepository.findOneByLogin(characterName).isPresent()) {
            userService.createUser(characterName);
        }

        try {
            final GrantedAuthority authority = new SimpleGrantedAuthority(AuthoritiesConstants.USER);
            Authentication authentication = new UsernamePasswordAuthenticationToken(characterName, characterName, Collections.singletonList(authority));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.createToken(authentication, true);
            response.addHeader(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);
            return ResponseEntity.ok(new JWTToken(jwt));
        } catch (AuthenticationException ae) {
            log.trace("Authentication exception trace: {}", ae);
            return new ResponseEntity<>(Collections.singletonMap("AuthenticationException",
                ae.getLocalizedMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
