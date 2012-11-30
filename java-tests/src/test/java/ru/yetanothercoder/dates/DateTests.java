package ru.yetanothercoder.dates;

import junit.framework.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Mikhail Baturov | www.yetanothercoder.ru
 */
public class DateTests {

    @Test
    public void testTimeAndDateAdding() throws ParseException {

        SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("dd.MM.yyyy");
        TimeZone utc = TimeZone.getTimeZone("UTC");
        dateOnlyFormat.setTimeZone(utc);

        String date = "01.01.2012";
        Date parsedDate = dateOnlyFormat.parse(date);
        System.out.println("date: " + parsedDate);

        SimpleDateFormat timeOnlyFormat = new SimpleDateFormat("HH:mm:ss");
        timeOnlyFormat.setTimeZone(utc);

        String time = "01:01:01";
        Date parsedTime = timeOnlyFormat.parse(time);
        System.out.println("time: " + parsedTime);

        long timeAndDateMillisAdded = parsedDate.getTime() + parsedTime.getTime();
        Date dateTimeFromExport = new Date(timeAndDateMillisAdded);
        System.out.printf("addedMillis: %s, added date: %s%n", timeAndDateMillisAdded, dateTimeFromExport);


        Calendar c = Calendar.getInstance(utc);

        // date >>
        c.set(Calendar.YEAR, 2012);
        c.set(Calendar.MONTH, 1-1);
        c.set(Calendar.DAY_OF_MONTH, 01);

        // time >>
        c.set(Calendar.HOUR_OF_DAY, 01);
        c.set(Calendar.MINUTE, 01);
        c.set(Calendar.SECOND, 01);
        c.set(Calendar.MILLISECOND, 0);

        long calendarMillis = c.getTimeInMillis();

        System.out.printf("calendarMillis: %s, calendar date: %s%n", calendarMillis, c.getTime());

        Assert.assertEquals(timeAndDateMillisAdded, calendarMillis);
    }
}
