package com.buyback.eve.web.rest;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class EnvironmentTestConfiguration implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        System.setProperty("SSO_URL", "nothingForTest");
    }
}
