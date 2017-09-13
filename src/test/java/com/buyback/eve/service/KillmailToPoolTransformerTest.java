package com.buyback.eve.service;

import java.time.LocalDate;

import com.buyback.eve.domain.Killmail;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.sf.cglib.core.Local;

public class KillmailToPoolTransformerTest {

    private KillmailToPoolTransformer sut = new KillmailToPoolTransformer(null, null);

    @Test
    public void getYearMonth() {
        String result = sut.getYearMonth(LocalDate.of(2017, 10, 11));
        assertEquals("2017-10", result);
    }

    @Test
    public void getYearMonth2() {
        String result = sut.getYearMonth(LocalDate.of(2017, 1, 11));
        assertEquals("2017-01", result);
    }

    @Test
    public void isCurrentMonth_false() throws Exception {
        Killmail killmail = new Killmail();
        killmail.setKillTime("2017-01-10 17:53:39");

        assertFalse(sut.isCurrentMonth(killmail));
    }

    @Test
    public void isCurrentMonth_true() throws Exception {
        Killmail killmail = new Killmail();
        LocalDate now = LocalDate.now();
        killmail.setKillTime(String.format("%d-%02d-10 17:53:39", now.getYear(), now.getMonthValue()));

        assertTrue(sut.isCurrentMonth(killmail));
    }
}
