package com.androidsx.rainnotifications.util;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateHelper {

    public String deltaTime(long time, long currentTime, long type) {
        CharSequence deltaTime = DateUtils.getRelativeTimeSpanString(
                time * 1000,
                currentTime,
                type);

        return deltaTime.toString();
    }

    public String formatTime(long forecastTime, String time_format, String time_zone, Locale locale) {
        SimpleDateFormat sdf = new SimpleDateFormat(time_format, locale);
        sdf.setTimeZone(TimeZone.getTimeZone(time_zone));

        String time =  sdf.format(new Date(forecastTime * 1000));

        return time;
    }
}
