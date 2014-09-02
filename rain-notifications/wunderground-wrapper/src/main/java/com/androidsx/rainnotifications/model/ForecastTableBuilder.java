package com.androidsx.rainnotifications.model;

import com.fortysevendeg.android.wunderground.api.service.response.HourlyResponse;
import com.fortysevendeg.android.wunderground.api.service.response.ObservationResponse;
import com.fortysevendeg.android.wunderground.api.service.response.WundergroundResponse;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for {@link ForecastTable}.
 * <p>
 * Should not be used from outside of this project.
 */
public class ForecastTableBuilder {

    public static ForecastTable buildFromWunderground(WundergroundResponse response) {
        final ObservationResponse currently = response.getCurrentObservation();
        final List<HourlyResponse> hourly = response.getHourlyList();
        final DateTime currentTime = new DateTime(Long.getLong(currently.getLocalEpoch()) * 1000);

        final List<Forecast> allForecasts = new ArrayList<Forecast>();
        allForecasts.addAll(extractAllValidForecast(currentTime, hourly, Forecast.Granularity.HOUR));

        final Weather currentWeather = WeatherBuilder.buildFromWunderground(currently);
        final List<Forecast> transitions = extractTransitions(currentWeather, allForecasts);

        return new ForecastTable(currentWeather, currentTime, transitions);
    }

    private static List<Forecast> extractAllValidForecast(DateTime fromTime,
                                                          List<HourlyResponse> hourly,
                                                          Forecast.Granularity granularity) {
        final List<Forecast> forecasts = new ArrayList<Forecast>();
        if (hourly != null) {
            for (HourlyResponse forecastHour : hourly) {
                final Forecast forecast = extractForecastIfValid(fromTime, forecastHour, granularity);
                if (forecast != null) {
                    forecasts.add(forecast);
                }
            }
        }
        return forecasts;
    }

    private static Forecast extractForecastIfValid(DateTime fromTime,
                                                   HourlyResponse hourlyResponse,
                                                   Forecast.Granularity granularity) {
        final Weather forecastedWeather = WeatherBuilder.buildFromWunderground(hourlyResponse);
        final DateTime forecastTime = new DateTime(Long.getLong(hourlyResponse.getFctTime().getEpoch()) * 1000);

        if (forecastTime.isBefore(fromTime.toInstant())) {
            //Log.v(TAG, "Skip the forecast for the present interval at " + forecastTime);
            return null;
        } else {
            final Interval timeFromNow = new Interval(fromTime, forecastTime);
            return new Forecast(forecastedWeather, timeFromNow, granularity);
        }
    }

    private static List<Forecast> extractTransitions(Weather currentWeather, List<Forecast> allForecasts) {
        final List<Forecast> transitions = new ArrayList<Forecast>();

        Weather latestWeather = currentWeather;
        for (Forecast forecast : allForecasts) {
            final Weather forecastedWeather = forecast.getForecastedWeather();
            if (latestWeather.equals(forecastedWeather)) {
                // Skip it
            } else {
                transitions.add(forecast);
                latestWeather = forecastedWeather;
            }
        }
        return transitions;
    }
}
