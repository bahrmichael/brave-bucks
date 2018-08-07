package com.bravebucks.eve;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;

public class MyResponseErrorHandler implements ResponseErrorHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DelayService esiDelayService;

    public MyResponseErrorHandler(final DelayService esiDelayService) {
        this.esiDelayService = esiDelayService;
    }

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getRawStatusCode() >= 400;
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = null;
        try {
            s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } finally {
            if (null != s) {
                s.close();
            }
        }
    }

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        final String body = convertStreamToString(response.getBody());
        logger.warn("{}: Body: {}, Headers: {}", response.getRawStatusCode(), body, getHeaders(response));
        String statusText = response.getStatusText();
        if (response.getRawStatusCode() == 520) {
            esiDelayService.enhanceYourCalm(response.getHeaders());
            throw new HttpClientErrorException(HttpStatus.valueOf(420), statusText,
                                               body.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        } else if (response.getRawStatusCode() >= 500) {
            throw new HttpServerErrorException(HttpStatus.valueOf(response.getRawStatusCode()),
                                               statusText,
                                               body.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        } else if (response.getRawStatusCode() >= 400) {
            if (body.contains("invalid_token")) {
                statusText = "invalid_token";
            }
            if (response.getRawStatusCode() == 420) {
                esiDelayService.enhanceYourCalm(response.getHeaders());
            }
            throw new HttpClientErrorException(HttpStatus.valueOf(response.getRawStatusCode()), statusText,
                                               body.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        }
    }

    private String getHeaders(final ClientHttpResponse response) {
        return response.getHeaders().entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining(", "));
    }
}
