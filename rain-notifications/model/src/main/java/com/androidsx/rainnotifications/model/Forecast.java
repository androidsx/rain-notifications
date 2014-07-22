package com.androidsx.rainnotifications.model;

import com.androidsx.rainnotifications.model.util.UiUtil;

import org.joda.time.Interval;

import java.util.Locale;

/**
 * A weather forecast about a particular point in the future, with certain granularity.
 */
public class Forecast {
    private final Weather forecastedWeather;
    private final Interval timeFromNow;
    private final Granularity granularity;

    public Forecast(Weather forecastedWeather, Interval timeFromNow, Granularity granularity) {
        this.forecastedWeather = forecastedWeather;
        this.timeFromNow = timeFromNow;
        this.granularity = granularity;
    }

    public Weather getForecastedWeather() {
        return forecastedWeather;
    }

    public Interval getTimeFromNow() {
        return timeFromNow;
    }

    public Granularity getGranularity() {
        return granularity;
    }

    @Override
    public String toString() {
        return forecastedWeather + " forecasted for "
                + UiUtil.getDebugOnlyPeriodFormatter().print(timeFromNow.toPeriod()) + " from now, "
                + granularity;
    }

    /**
     * The granularity of a forecast, usually connected to the precision of it. Going wider than
     * hours doesn't make much sense for our app.
     */
    public enum Granularity {
        MINUTE,
        HOUR;

        @Override
        public String toString() {
            return super.toString().toLowerCase(Locale.ENGLISH) + "ly";
        }
    }
}
