package io.polyrepo.analyser.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private DateUtil(){}
    public static String calculateDateFromDays(int days) {
        LocalDate date = LocalDate.now().minusDays(days);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatters);
    }
}
