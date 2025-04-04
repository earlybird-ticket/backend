package com.earlybirdticket.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CommonUtil {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DayDateTimeFormatter = DateTimeFormatter.ofPattern(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);

    public static String LocalDateTimetoString(LocalDateTime localDateTime) {

        return localDateTime == null ? null : localDateTime.format(dateTimeFormatter);
    }

    public static LocalDateTime StringToLocalDateTime(String localDateTime) {
        return localDateTime == null ? null : LocalDateTime.parse(localDateTime, dateTimeFormatter);
    }

    public static LocalDateTime gmtStringToDefaultLocalDateTime(String gmtDateString) {
        ZonedDateTime gmtDateTime = ZonedDateTime.parse(gmtDateString, DayDateTimeFormatter);
        ZonedDateTime localDateTime = gmtDateTime.withZoneSameInstant(ZoneId.systemDefault());

        return localDateTime.toLocalDateTime();

    }

}
