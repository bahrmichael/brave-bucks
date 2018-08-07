package com.bravebucks.eve;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class XEsiInterceptor implements ClientHttpRequestInterceptor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
                                        final ClientHttpRequestExecution execution)
        throws IOException {
        final HttpHeaders headers = request.getHeaders();
        headers.add("User-Agent", "Rihan Shazih: bucks.bravecollective.com");
        // OkHttp will add encoding by default https://github.com/square/okhttp/wiki/Calls
        final ClientHttpResponse response = execution.execute(request, body);
        checkForDeprecation(response, request);
        return response;
    }

    private void checkForDeprecation(final HttpMessage response, final HttpRequest request) {
        final HttpHeaders headers = response.getHeaders();
        if (headers.containsKey("Warning")) {
            log.warn("Warning for {}: {}", headers.get("Warning").get(0), request.getURI().getPath());
        }
    }

}
