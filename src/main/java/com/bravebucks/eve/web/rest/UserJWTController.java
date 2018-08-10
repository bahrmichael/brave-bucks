package com.bravebucks.eve.web.rest;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import com.bravebucks.eve.domain.EveCharacter;
import com.bravebucks.eve.domain.User;
import com.bravebucks.eve.domain.esi.AuthVerificationResponse;
import com.bravebucks.eve.domain.esi.CharacterDetailsResponse;
import com.bravebucks.eve.repository.CharacterRepository;
import com.bravebucks.eve.repository.UserRepository;
import com.bravebucks.eve.security.jwt.TokenProvider;
import com.bravebucks.eve.service.UserService;
import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.bravebucks.eve.security.jwt.JWTConfigurer.AUTHORIZATION_HEADER;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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

    @Value("${WALLET_CLIENT_ID}")
    private String walletClientId;

    @Value("${WALLET_CLIENT_SECRET}")
    private String walletClientSecret;

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RestTemplate restTemplate;
    private final CharacterRepository characterRepository;

    public UserJWTController(TokenProvider tokenProvider, UserRepository userRepository, UserService userService,
                             final RestTemplate restTemplate,
                             final CharacterRepository characterRepository) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.userService = userService;
        this.restTemplate = restTemplate;
        this.characterRepository = characterRepository;
    }

    @GetMapping("/authenticate/sso")
    @Timed
    public ResponseEntity authorize(@RequestParam("code") String code, @RequestParam("state") String state, HttpServletResponse response) {

        User user;
        if (state.startsWith("wallet")) {
            final String[] split = state.split("-");
            if (split.length != 2) {
                // todo: return error
                return ResponseEntity.badRequest().build();
            }

            String targetUserId = split[1];

            final AuthVerificationResponse authResponse = verifyAuthentication(code, state, walletClientId, walletClientSecret);
            final CharacterDetailsResponse charDetails = getCharacterDetails(authResponse.getAccessToken());

            final EveCharacter character = new EveCharacter(charDetails.getCharacterId(), charDetails.getCharacterName(),
                                                    authResponse.getRefreshToken(), targetUserId);
            characterRepository.save(character);
            user = userRepository.findOne(targetUserId);
        } else {
            final AuthVerificationResponse authResponse = verifyAuthentication(code, state, clientId, clientSecret);
            final CharacterDetailsResponse charDetails = getCharacterDetails(authResponse.getAccessToken());
            if (null == charDetails) {
                return new ResponseEntity<>("AuthenticationException", HttpStatus.UNAUTHORIZED);
            }
            user = createIfNotExists(charDetails);
        }

        try {
            List<SimpleGrantedAuthority> authorities = user.getAuthorities().stream().map(
                a -> new SimpleGrantedAuthority(a.getName())).collect(Collectors.toList());
            Authentication authentication = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getLogin(), authorities);
            String jwt = tokenProvider.createToken(authentication, true);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            response.addHeader(AUTHORIZATION_HEADER, "Bearer " + jwt);
            return ResponseEntity.ok(new JWTToken(jwt));
        } catch (AuthenticationException ae) {
            log.trace("Authentication exception trace: {}", ae);
            return new ResponseEntity<>(Collections.singletonMap("AuthenticationException",
                ae.getLocalizedMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    private CharacterDetailsResponse getCharacterDetails(final String accessToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION_HEADER, "Bearer " + accessToken);

        final HttpEntity<Void> request = new HttpEntity<>(null, headers);
        return restTemplate.exchange("https://login.eveonline.com/oauth/verify", HttpMethod.GET, request, CharacterDetailsResponse.class).getBody();
    }

    private AuthVerificationResponse verifyAuthentication(final String code, final String state, final String clientId, final String clientSecret) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        headers.add("Authorization", getBasicAuth(clientId, clientSecret));

        final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", code);

        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        return restTemplate.postForObject("https://login.eveonline.com/oauth/token", request, AuthVerificationResponse.class);
    }

    public static String getBasicAuth(final String clientId, final String clientSecret) {
        final String auth = clientId + ":" + clientSecret;
        final byte[] encodedAuth = Base64.encodeBase64(
            auth.getBytes(Charset.forName("UTF-8")));
        return "Basic " + new String(encodedAuth);
    }

    User createIfNotExists(final CharacterDetailsResponse characterDetails) {
        return userRepository.findOneByLogin(characterDetails.getCharacterName())
                             .orElseGet(() -> userService.createUser(characterDetails.getCharacterName(), characterDetails.getCharacterId()));
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
