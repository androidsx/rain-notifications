package com.androidsx.rainnotifications.model;

import org.joda.time.Interval;

/**
 * A weather forecast about a particular interval of time in the future.
 */
public class Forecast {
    private final Interval interval;
    private final WeatherWrapper weatherWrapper;

    /**
     * @param interval When the weather happens, from start until end.
     * @param weatherWrapper
     */
    public Forecast(Interval interval, WeatherWrapper weatherWrapper) {
        this.interval = interval;
        this.weatherWrapper = weatherWrapper;
    }

    /**
     * Interval of time during which this forecast is valid. For instance, there may be rain
     * forecasted for 4pm to 6pm certain day. Those two points in time are the start and the
     * end of the interval.
     */
    public Interval getInterval() {
        return interval;
    }

    public WeatherWrapper getWeatherWrapper() {
        return weatherWrapper;
    }

    @Override
    public String toString() {
        return weatherWrapper + " forecasted from " + interval.getStart() + " until " + interval.getEnd() + " (Duration: "+ interval.toDuration().getStandardMinutes() + ")";
    }
}
