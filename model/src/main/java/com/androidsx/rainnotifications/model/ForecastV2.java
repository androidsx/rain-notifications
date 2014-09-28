package com.androidsx.rainnotifications.model;

import org.joda.time.Interval;

/**
 * A weather forecast about a particular point in the future.
 */
public class ForecastV2 {
    private final Interval interval;
    private final WeatherWrapperV2 weatherWrapper;

    /**
     * @param interval When the weather happens, from start until end.
     * @param weatherWrapper
     */
    public ForecastV2(Interval interval, WeatherWrapperV2 weatherWrapper) {
        this.interval = interval;
        this.weatherWrapper = weatherWrapper;
    }

    public Interval getInterval() {
        return interval;
    }

    public WeatherWrapperV2 getWeatherWrapper() {
        return weatherWrapper;
    }

    @Override
    public String toString() {
        return weatherWrapper + " forecasted from " + interval.getStart() + " until " + interval.getEnd() + " (Duration: "+ interval.toDuration().getStandardMinutes() + ")";
    }
}
