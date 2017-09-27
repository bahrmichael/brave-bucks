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

        CharacterDetails characterDetails = getCharacterDetails(clientId, clientSecret, code);
        if (null == characterDetails) {
            return new ResponseEntity<>("AuthenticationException", HttpStatus.UNAUTHORIZED);
        }

        createIfNotExists(characterDetails);

        try {
            final GrantedAuthority authority = new SimpleGrantedAuthority(AuthoritiesConstants.USER);
            Authentication authentication = new UsernamePasswordAuthenticationToken(characterDetails.getName(), characterDetails.getName(), Collections.singletonList(authority));
            String jwt = tokenProvider.createToken(authentication, true);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            response.addHeader(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);
            return ResponseEntity.ok(new JWTToken(jwt));
        } catch (AuthenticationException ae) {
            log.trace("Authentication exception trace: {}", ae);
            return new ResponseEntity<>(Collections.singletonMap("AuthenticationException",
                ae.getLocalizedMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    void createIfNotExists(final CharacterDetails characterDetails) {
        if (!userRepository.findOneByLogin(characterDetails.getName()).isPresent()) {
            userService.createUser(characterDetails.getName(), characterDetails.getId());
        }
    }

    CharacterDetails getCharacterDetails(final String clientId, final String clientSecret, final String code) {
        final CharacterDetails[] characterDetails = {null};
        requestService.getAccessToken(clientId, clientSecret, code).ifPresent(response -> {
            JSONObject object = response.getObject();
            String accessToken = object.getString("access_token");
            requestService.getUserDetails(accessToken).ifPresent(detailsResponse -> {
                JSONObject details = detailsResponse.getObject();
                String characterName = details.getString("CharacterName");
                Long characterId = details.getLong("CharacterID");
                characterDetails[0] = new CharacterDetails(characterName, characterId);
            });
        });
        return characterDetails[0];
    }

    static class CharacterDetails {
        private final String name;
        private final Long id;

        CharacterDetails(final String name, final Long id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public Long getId() {
            return id;
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
