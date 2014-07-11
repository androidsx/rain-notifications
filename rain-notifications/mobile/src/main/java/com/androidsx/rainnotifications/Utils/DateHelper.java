package com.androidsx.rainnotifications.Utils;

import android.text.format.DateUtils;

import com.forecast.io.v2.network.services.ForecastService.Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateHelper {

    public String deltaTime(Response response, long currentTime, long type) {
        CharSequence deltaTime = DateUtils.getRelativeTimeSpanString(
                response.getForecast().getCurrently().getTime() * 1000,
                currentTime,
                type);

        return deltaTime.toString();
    }

    public String getForecastTime(Response response, String time_format, String time_zone, Locale locale) {
        SimpleDateFormat sdf = new SimpleDateFormat(time_format, locale);
        sdf.setTimeZone(TimeZone.getTimeZone(time_zone));

        String time =  sdf.format(new Date(response.getForecast().getCurrently().getTime() * 1000));

        return time;
    }
}
