package ru.practicum.stats.server;

import java.time.format.DateTimeFormatter;

public final class DateFormats {

    public static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateFormats() {
    }
}
