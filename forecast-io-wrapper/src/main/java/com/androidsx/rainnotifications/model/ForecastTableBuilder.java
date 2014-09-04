package com.androidsx.rainnotifications.model;

import android.util.Log;

import com.forecast.io.v2.network.services.ForecastService;
import com.forecast.io.v2.transfer.DataBlock;
import com.forecast.io.v2.transfer.DataPoint;

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

    public static ForecastTable buildFromForecastIo(ForecastService.Response response) {
        final DataPoint currently = response.getForecast().getCurrently();
        final DataBlock minutely = response.getForecast().getMinutely();
        final DataBlock hourly = response.getForecast().getHourly();
        final DateTime currentTime = new DateTime(currently.getTime() * 1000);

        final List<Forecast> allForecasts = new ArrayList<Forecast>();
        allForecasts.addAll(extractAllValidForecast(currentTime, minutely, Forecast.Granularity.MINUTE));
        allForecasts.addAll(extractAllValidForecast(currentTime, hourly, Forecast.Granularity.HOUR));

        final Weather currentWeather = WeatherBuilder.buildFromForecastIo(currently);
        final List<Forecast> transitions = extractTransitions(currentWeather, allForecasts);

        return new ForecastTable(currentWeather, currentTime, transitions);
    }

    private static List<Forecast> extractAllValidForecast(DateTime fromTime,
                                                          DataBlock dataBlock,
                                                          Forecast.Granularity granularity) {
        final List<Forecast> forecasts = new ArrayList<Forecast>();
        if (dataBlock != null) {
            for (DataPoint dataPoint : dataBlock.getData()) {
                final Forecast forecast = extractForecastIfValid(fromTime, dataPoint, granularity);
                if (forecast != null) {
                    forecasts.add(forecast);
                }
            }
        }
        return forecasts;
    }

    private static Forecast extractForecastIfValid(DateTime fromTime,
                                                   DataPoint dataPoint,
                                                   Forecast.Granularity granularity) {
        final Weather forecastedWeather = WeatherBuilder.buildFromForecastIo(dataPoint);
        final DateTime forecastTime = new DateTime(dataPoint.getTime() * 1000);

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
