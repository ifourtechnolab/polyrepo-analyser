package io.polyrepo.analyser.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class DateUtil {
    private DateUtil(){}
    public static String calculateDateFromDays(int days) {
        LocalDate date = LocalDate.now().minusDays(days);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatters);
    }

    public static Long calculateDiffBetweenDates(String createdAt, String closedAt) {
        try{
            Date date1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(createdAt);
            Date date2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(closedAt);
            return Math.abs(date2.getTime()-date1.getTime());
        }
        catch (ParseException e){
            return null;
        }
    }

    public static String calculateAverage(List<Long> timeDiffList) {
        long sum = 0;
        for (Long timeDiff : timeDiffList) {
            sum += timeDiff;
        }
        long result = sum / timeDiffList.size();

        long differenceInSeconds = (result / 1000) % 60;

        long differenceInMinutes = (result / (1000 * 60)) % 60;

        long differenceInHours = (result / (1000 * 60 * 60)) % 24;

        long differenceInDays = (result / (1000 * 60 * 60 * 24));

        return (String.format("%d days, %d hours, %d minutes, %d seconds", differenceInDays, differenceInHours, differenceInMinutes, differenceInSeconds));
    }
}
