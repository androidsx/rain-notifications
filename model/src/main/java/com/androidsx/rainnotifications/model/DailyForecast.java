package com.androidsx.rainnotifications.model;

import org.joda.time.DateTime;

/**
 * A weather forecast about a particular interval of time in the future.
 */
public class DailyForecast {
    private final DateTime day;
    private final DailyWeatherWrapper weatherWrapper;

    /**
     * @param day
     * @param weatherWrapper
     */
    public DailyForecast(DateTime day, DailyWeatherWrapper weatherWrapper) {
        this.day = day;
        this.weatherWrapper = weatherWrapper;
    }

    public DateTime getDay() {
        return day;
    }

    public DailyWeatherWrapper getWeatherWrapper() {
        return weatherWrapper;
    }

    @Override
    public String toString() {
        return weatherWrapper + " forecasted on " + day;
    }
}
