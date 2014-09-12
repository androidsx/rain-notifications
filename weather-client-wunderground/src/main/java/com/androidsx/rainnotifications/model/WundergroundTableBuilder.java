package com.androidsx.rainnotifications.model;

import android.util.Log;

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
public class WundergroundTableBuilder {

    public static ForecastTable buildFromWunderground(JSONObject response) throws JSONException {
        if (response.has("current_observation") && response.has("hourly_forecast") && response.has("sun_phase")) {
            final JSONObject currently = (JSONObject) response.get("current_observation");
            final JSONArray hourly = (JSONArray) response.get("hourly_forecast");
            final JSONObject sunPhase = (JSONObject) response.get("sun_phase");

            final DateTime currentTime = new DateTime(Long.parseLong(currently.get("local_epoch").toString()) * 1000);

            final List<Forecast> allForecasts = new ArrayList<Forecast>();
            allForecasts.addAll(extractAllValidForecast(currentTime, sunPhase, hourly, Forecast.Granularity.HOUR));

            final Weather currentWeather = WundergroundWeatherBuilder.buildFromWunderground(currently.getString("icon"));
            setProperPhase(currentTime, sunPhase, currentWeather);

            return ForecastTable.create(currentWeather, currentTime, allForecasts);
        } else {
            return null;
        }
    }

    private static List<Forecast> extractAllValidForecast(DateTime fromTime, JSONObject sunPhase,
                                                          JSONArray dataBlock,
                                                          Forecast.Granularity granularity) throws JSONException{
        final List<Forecast> forecasts = new ArrayList<Forecast>();
        if (dataBlock != null) {
            for(int i=0; i < dataBlock.length(); i++) {
                final Forecast forecast = extractForecastIfValid(fromTime, sunPhase, (JSONObject)dataBlock.get(i), granularity);
                if (forecast != null) {
                    forecasts.add(forecast);
                }
            }
        }
        return forecasts;
    }

    private static Forecast extractForecastIfValid(DateTime fromTime, JSONObject sunPhase,
                                                   JSONObject dataPoint,
                                                   Forecast.Granularity granularity) throws JSONException {
        JSONObject time = (JSONObject)dataPoint.get("FCTTIME");
        final DateTime forecastTime = new DateTime(Long.parseLong(time.get("epoch").toString()) * 1000);
        final Weather forecastedWeather = WundergroundWeatherBuilder.buildFromWunderground(dataPoint.getString("icon"));
        setProperPhase(forecastTime, sunPhase, forecastedWeather);

        if (forecastTime.isBefore(fromTime.toInstant())) {
            //Log.v(TAG, "Skip the forecast for the present interval at " + forecastTime);
            return null;
        } else {
            final Interval timeFromNow = new Interval(fromTime, forecastTime);
            return new Forecast(forecastedWeather, timeFromNow, granularity);
        }
    }

    private static void setProperPhase(DateTime weatherTime, JSONObject sunPhase, Weather weather) throws JSONException {
        DateTime sunriseTime = getSunPhaseTime(sunPhase, "sunrise");
        DateTime sunsetTime = getSunPhaseTime(sunPhase, "sunset");
        if (sunriseTime.isBefore(weatherTime) && weatherTime.isBefore(sunsetTime)) {
            // Do nothing
        } else {
            weather.setPhase(WeatherPhase.NIGHT);
        }
    }

    private static DateTime getSunPhaseTime(JSONObject sunPhase, String phase) throws JSONException {
        final JSONObject sunrise = (JSONObject) sunPhase.get(phase);
        DateTime sunPhaseTime = DateTime.now();
        sunPhaseTime = sunPhaseTime.hourOfDay().setCopy(sunrise.getString("hour"));
        sunPhaseTime = sunPhaseTime.minuteOfHour().setCopy(sunrise.getString("minute"));
        return sunPhaseTime;
    }
}
