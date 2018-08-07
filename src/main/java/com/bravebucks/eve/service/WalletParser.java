package com.bravebucks.eve.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.bravebucks.eve.DelayService;
import com.bravebucks.eve.domain.RattingEntry;
import com.bravebucks.eve.domain.User;
import com.bravebucks.eve.domain.esi.AccessTokenResponse;
import com.bravebucks.eve.domain.esi.WalletResponse;
import com.bravebucks.eve.repository.RattingEntryRepository;
import com.bravebucks.eve.repository.UserRepository;

import static com.bravebucks.eve.web.rest.UserJWTController.getBasicAuth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class WalletParser {

    private static final Logger log = LoggerFactory.getLogger(WalletParser.class);

    @Value("${WALLET_CLIENT_ID}")
    private String walletClientId;

    @Value("${WALLET_CLIENT_SECRET}")
    private String walletClientSecret;

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final AdmService admService;
    private final RattingEntryRepository rattingEntryRepository;
    private final DelayService delayService;

    public WalletParser(final RestTemplate restTemplate,
                        final UserRepository userRepository, final AdmService admService,
                        final RattingEntryRepository rattingEntryRepository,
                        final DelayService delayService) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.admService = admService;
        this.rattingEntryRepository = rattingEntryRepository;
        this.delayService = delayService;
    }

    @Async
    @Scheduled(cron = "0 */10 * * * *")
    public void collectNewJournalEntries() {
        final List<User> users = userRepository.findAllByWalletReadRefreshTokensNotNull();
        for (final User user : users) {
            for (Map.Entry<Integer, String> entry : user.getWalletReadRefreshTokens().entrySet()) {

                if (delayService.shouldIChill()) {
                    continue;
                }

                try {

                    final String accessToken = getAccessTokenWithRefreshToken(entry.getValue(), walletClientId, walletClientSecret);
                    final String walletUri = "https://esi.evetech.net/v4/characters/" + entry.getKey() + "/wallet/journal/";


                    final ResponseEntity<WalletResponse[]> walletResponse = restTemplate.exchange(walletUri, HttpMethod.GET,
                                                                                                  authorizedRequest(accessToken),
                                                                                                  WalletResponse[].class);

                    if (walletResponse.getStatusCode() != HttpStatus.OK || walletResponse.getBody() == null) {
                        log.info("No new transactions for {} (wallet response is {}).", entry.getKey(), walletResponse.getStatusCode());
                        return;
                    }

                    for (WalletResponse walletEntry : walletResponse.getBody()) {
                        if ("bounty_prizes".equals(walletEntry.getRefType())
                            && rattingEntryRepository.countByJournalId(walletEntry.getId()) == 0) {

                            final String[] killSplit = walletEntry.getReason().split(",");
                            int killCount = 0;
                            for (String killCounter : killSplit) {
                                killCount += Integer.parseInt(killCounter.split(": ")[1]);
                            }
                            final Integer systemId = walletEntry.getContextId().intValue();
                            final Integer adm = admService.getAdm(systemId);
                            final Instant instant = Instant.parse(walletEntry.getDate());

                            final RattingEntry rattingEntry = new RattingEntry(walletEntry.getId(), user.getId(),
                                                                               entry.getKey(), killCount, systemId,
                                                                               instant, adm);

                            rattingEntryRepository.save(rattingEntry);
                        }
                    }
                } catch (final HttpServerErrorException | HttpClientErrorException exception) {
                    log.info("No new transactions for {} (TQ status is {}): {}", entry.getKey(), exception.getStatusCode(), exception.getMessage());
                }
            }
        }
    }

    private String getAccessTokenWithRefreshToken(final String refreshToken, final String clientId,
                                                  final String clientSecret) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "refresh_token");
        map.add("refresh_token", refreshToken);
        final String authHeader = getBasicAuth(clientId, clientSecret);
        headers.add("Authorization", authHeader);

        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        return restTemplate.postForEntity("https://login.eveonline.com/oauth/token", request,
                                              AccessTokenResponse.class).getBody().getAccessToken();
    }

    private static HttpEntity<Object> authorizedRequest(final String accessToken) {
        final HttpHeaders headers = buildAuthHeader(accessToken);
        return new HttpEntity<>(null, headers);
    }

    private static HttpHeaders buildAuthHeader(final String accessToken) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        return headers;
    }
}
