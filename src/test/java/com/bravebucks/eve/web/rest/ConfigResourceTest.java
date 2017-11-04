package com.bravebucks.eve.web.rest;

import org.junit.Test;
import static org.junit.Assert.*;

public class ConfigResourceTest {
    @Test
    public void getSsoUrl() throws Exception {
        ConfigResource sut = new ConfigResource("aUrl");
        assertEquals("aUrl", sut.getSsoUrl().getBody());
    }
}
