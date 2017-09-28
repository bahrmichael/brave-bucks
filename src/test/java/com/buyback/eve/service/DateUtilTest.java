package com.buyback.eve.service;

import java.time.LocalDate;

import com.buyback.eve.domain.Killmail;

import org.junit.Test;
import static org.junit.Assert.*;

public class DateUtilTest {

    @Test
    public void getYearMonth() {
        String result = DateUtil.getYearMonth(LocalDate.of(2017, 10, 11));
        assertEquals("2017-10", result);
    }

    @Test
    public void getYearMonth2() {
        String result = DateUtil.getYearMonth(LocalDate.of(2017, 1, 11));
        assertEquals("2017-01", result);
    }

    @Test
    public void isCurrentMonth_false() throws Exception {
        Killmail killmail = new Killmail();
        killmail.setKillTime("2017-01-10 17:53:39");

        assertFalse(DateUtil.isCurrentMonth(killmail));
    }

    @Test
    public void isCurrentMonth_true() throws Exception {
        Killmail killmail = new Killmail();
        LocalDate now = LocalDate.now();
        killmail.setKillTime(String.format("%d-%02d-10 17:53:39", now.getYear(), now.getMonthValue()));

        assertTrue(DateUtil.isCurrentMonth(killmail));
    }

    @Test
    public void getLocalDate() throws Exception {
        LocalDate date = DateUtil.getLocalDate("2017-01-10 17:53:39");
        assertEquals(2017, date.getYear());
        assertEquals(1, date.getMonthValue());
        assertEquals(10, date.getDayOfMonth());
    }
}
