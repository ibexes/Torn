package com.torn.assistant.service;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTrackerServiceTest {
    @Test
    public void testDate() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = simpleDateFormat.parse("2021-06-15 21:03:00+0000");
        assertEquals("2021-06-15 21:00:00+0000", simpleDateFormat.format(PlayerTrackerService.nearestMinuteMultiple(5, date)));


        Date date1 = simpleDateFormat.parse("2021-06-15 21:03:00+0000");
        assertEquals("2021-06-15 21:00:00+0000", simpleDateFormat.format(PlayerTrackerService.nearestMinuteMultiple(10, date1)));

        Date date2 = simpleDateFormat.parse("2021-06-15 21:45:01+0000");
        assertEquals("2021-06-15 21:45:00+0000", simpleDateFormat.format(PlayerTrackerService.nearestMinuteMultiple(5, date2)));
    }
}
