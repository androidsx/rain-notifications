package com.androidsx.rainnotifications.util;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateHelper {

    private DateHelper() {
        //No-instantiate
    }

    public static String deltaTime(long time, long currentTime) {
        CharSequence deltaTime = DateUtils.getRelativeTimeSpanString(
                time,
                currentTime,
                DateUtils.MINUTE_IN_MILLIS);

        return deltaTime.toString();
    }

    public static String formatTime(long forecastTime, String time_zone) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.Time.TIME_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone(time_zone));

        String time =  sdf.format(new Date(forecastTime));

        return time;
    }

    public static String formatTimeMadrid(long forecastTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.Time.TIME_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone(Constants.Time.TIME_ZONE_MADRID));

        String time =  sdf.format(new Date(forecastTime));

        return time;
    }

    public static String formatTimeNewYork(long forecastTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.Time.TIME_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone(Constants.Time.TIME_ZONE_NEW_YORK));

        String time =  sdf.format(new Date(forecastTime));

        return time;
    }
}
