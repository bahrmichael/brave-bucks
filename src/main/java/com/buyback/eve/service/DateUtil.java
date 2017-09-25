package com.buyback.eve.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.buyback.eve.domain.Killmail;

public class DateUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static boolean isCurrentMonth(final Killmail killmail) {
        LocalDateTime killTime = LocalDateTime.parse(killmail.getKillTime(), FORMATTER);
        LocalDate now = LocalDate.now();
        return killTime.getYear() == now.getYear() && killTime.getMonthValue() == now.getMonthValue();
    }

    public static String getYearMonth(final LocalDate localDate) {
        return String.format("%d-%02d", localDate.getYear(), localDate.getMonthValue());
    }

    static LocalDate getLocalDate(final String killTime) {
        return LocalDateTime.parse(killTime, FORMATTER).toLocalDate();
    }
}
