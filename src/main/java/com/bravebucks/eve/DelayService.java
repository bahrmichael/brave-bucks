package com.bravebucks.eve;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class DelayService {

    private static final String LIMIT_RESET = "x-esi-error-limit-reset";
    private static final String LIMIT_REMAIN = "x-esi-error-limit-remain";
    private final Logger log = LoggerFactory.getLogger(getClass());

    private Instant delayExpiry = Instant.now();

    public void enhanceYourCalm(final HttpHeaders headers) {
        if (headers.containsKey(LIMIT_RESET) && headers.containsKey(LIMIT_REMAIN)) {
            final int errorLimitRemain = Integer.parseInt(headers.get(LIMIT_REMAIN).get(0));
            if (errorLimitRemain < 50) {
                final int errorLimitReset = Integer.parseInt(headers.get(LIMIT_RESET).get(0));
                log.info("Setting delay to {} seconds from now.", errorLimitReset);
                setExpirySeconds(errorLimitReset);
            }
        }
    }

    void setExpirySeconds(final int errorLimitReset) {
        delayExpiry = Instant.now().plusSeconds(errorLimitReset);
    }

    public boolean shouldIChill() {
        final boolean shouldChill = delayExpiry.isAfter(Instant.now());
        if (shouldChill) {
            log.info("Delay is active until {}.", delayExpiry);
        }
        return shouldChill;
    }
}
