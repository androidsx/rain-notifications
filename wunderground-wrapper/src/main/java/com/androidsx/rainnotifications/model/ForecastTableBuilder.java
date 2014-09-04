package com.androidsx.rainnotifications.model;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for {@link ForecastTable}.
 * <p>
 * Should not be used from outside of this project.
 */
public class ForecastTableBuilder {

    public static ForecastTable buildFromForecastIo(JSONObject response) throws JSONException {
        final JSONObject currently = (JSONObject)response.get("hourly_forecast");
        final JSONArray hourly = (JSONArray)response.get("hourly_forecast");
        final DateTime currentTime = new DateTime(Long.getLong(currently.get("epoch").toString()) * 1000);

        final List<Forecast> allForecasts = new ArrayList<Forecast>();
        allForecasts.addAll(extractAllValidForecast(currentTime, hourly, Forecast.Granularity.HOUR));

        final Weather currentWeather = WeatherBuilder.buildFromForecastIo(currently.get("icon").toString());
        final List<Forecast> transitions = extractTransitions(currentWeather, allForecasts);

        return new ForecastTable(currentWeather, currentTime, transitions);
    }

    private static List<Forecast> extractAllValidForecast(DateTime fromTime,
                                                          JSONArray dataBlock,
                                                          Forecast.Granularity granularity) throws JSONException{
        final List<Forecast> forecasts = new ArrayList<Forecast>();
        if (dataBlock != null) {
            for(int i=0; i < dataBlock.length(); i++) {
                final Forecast forecast = extractForecastIfValid(fromTime, (JSONObject)dataBlock.get(i), granularity);
                if (forecast != null) {
                    forecasts.add(forecast);
                }
            }
        }
        return forecasts;
    }

    private static Forecast extractForecastIfValid(DateTime fromTime,
                                                   JSONObject dataPoint,
                                                   Forecast.Granularity granularity) throws JSONException {
        final Weather forecastedWeather = WeatherBuilder.buildFromForecastIo(dataPoint.get("icon").toString());
        final DateTime forecastTime = new DateTime(Long.getLong(dataPoint.get("epoch").toString()) * 1000);

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
