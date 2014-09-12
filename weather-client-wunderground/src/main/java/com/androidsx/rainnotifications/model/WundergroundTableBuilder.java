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

            final DateTime sunriseTime = getSunPhaseTime(sunPhase, "sunrise");
            final DateTime sunsetTime = getSunPhaseTime(sunPhase, "sunset");
            final DateTime currentTime = new DateTime(Long.parseLong(currently.get("local_epoch").toString()) * 1000);

            final List<Forecast> allForecasts = new ArrayList<Forecast>();
            allForecasts.addAll(extractAllValidForecast(currentTime, sunriseTime, sunsetTime, hourly, Forecast.Granularity.HOUR));

            String icon = getProperIcon(currentTime, sunriseTime, sunsetTime, currently.getString("icon"));
            final Weather currentWeather = WundergroundWeatherBuilder.buildFromWunderground(icon);

            return ForecastTable.create(currentWeather, currentTime, allForecasts);
        } else {
            return null;
        }
    }

    private static List<Forecast> extractAllValidForecast(DateTime fromTime, DateTime sunrise, DateTime sunset,
                                                          JSONArray dataBlock,
                                                          Forecast.Granularity granularity) throws JSONException{
        final List<Forecast> forecasts = new ArrayList<Forecast>();
        if (dataBlock != null) {
            for(int i=0; i < dataBlock.length(); i++) {
                final Forecast forecast = extractForecastIfValid(fromTime, sunrise, sunset, (JSONObject)dataBlock.get(i), granularity);
                if (forecast != null) {
                    forecasts.add(forecast);
                }
            }
        }
        return forecasts;
    }

    private static Forecast extractForecastIfValid(DateTime fromTime, DateTime sunrise, DateTime sunset,
                                                   JSONObject dataPoint,
                                                   Forecast.Granularity granularity) throws JSONException {
        JSONObject time = (JSONObject)dataPoint.get("FCTTIME");
        final DateTime forecastTime = new DateTime(Long.parseLong(time.get("epoch").toString()) * 1000);
        String icon = getProperIcon(forecastTime, sunrise, sunset, dataPoint.getString("icon"));

        final Weather forecastedWeather = WundergroundWeatherBuilder.buildFromWunderground(icon);
        if (forecastTime.isBefore(fromTime.toInstant())) {
            //Log.v(TAG, "Skip the forecast for the present interval at " + forecastTime);
            return null;
        } else {
            final Interval timeFromNow = new Interval(fromTime, forecastTime);
            return new Forecast(forecastedWeather, timeFromNow, granularity);
        }
    }

    private static DateTime getSunPhaseTime(JSONObject sunPhase, String phase) throws JSONException {
        final JSONObject sunrise = (JSONObject) sunPhase.get(phase);
        DateTime sunriseTime = DateTime.now();
        sunriseTime = sunriseTime.hourOfDay().setCopy(sunrise.getString("hour"));
        sunriseTime = sunriseTime.minuteOfHour().setCopy(sunrise.getString("minute"));
        return sunriseTime;
    }

    private static String getProperIcon(DateTime weatherTime, DateTime sunrise, DateTime sunset, String icon) {
        if (sunrise.isBefore(weatherTime) && weatherTime.isBefore(sunset)) {
            // Do nothing
        } else {
            icon += "_night";
        }
        return icon;
    }
}
