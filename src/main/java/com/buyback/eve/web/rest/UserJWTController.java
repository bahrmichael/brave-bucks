package com.buyback.eve.web.rest;

import java.util.Collections;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import com.buyback.eve.repository.UserRepository;
import com.buyback.eve.security.AuthoritiesConstants;
import com.buyback.eve.security.jwt.JWTConfigurer;
import com.buyback.eve.security.jwt.TokenProvider;
import com.buyback.eve.service.JsonRequestService;
import com.buyback.eve.service.UserService;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mashape.unirest.http.JsonNode;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Value("${CLIENT_ID}")
    private String clientId;

    @Value("${CLIENT_SECRET}")
    private String clientSecret;

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JsonRequestService requestService;

    public UserJWTController(TokenProvider tokenProvider, UserRepository userRepository, UserService userService,
                             final JsonRequestService requestService) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.userService = userService;
        this.requestService = requestService;
    }

    @GetMapping("/authenticate/sso")
    @Timed
    public ResponseEntity authorize(@RequestParam("code") String code, @RequestParam("state") String state, HttpServletResponse response) {

        String characterName = null;
        Long characterId = null;
        Optional<JsonNode> accessToken = requestService.getAccessToken(clientId, clientSecret, code);
        if (accessToken.isPresent()) {
            Optional<JsonNode> detailsOptional = requestService.getUserDetails(accessToken.get().getObject().getString("access_token"));
            if (detailsOptional.isPresent()) {
                JSONObject details = detailsOptional.get().getObject();
                characterName = details.getString("CharacterName");
                characterId = details.getLong("CharacterID");
            }
        }

        if (null == characterId || null == characterName) {
            return new ResponseEntity<>("AuthenticationException", HttpStatus.UNAUTHORIZED);
        }

        if (!userRepository.findOneByLogin(characterName).isPresent()) {
            userService.createUser(characterName, characterId);
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
