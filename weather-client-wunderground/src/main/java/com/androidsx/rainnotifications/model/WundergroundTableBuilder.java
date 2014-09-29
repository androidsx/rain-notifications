package com.androidsx.rainnotifications.model;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for {@link com.androidsx.rainnotifications.model.ForecastTableV2}.
 * <p>
 * Should not be used from outside of this project.
 */
public class WundergroundTableBuilder {
    public static ForecastTableV2 buildFromWunderground(JSONObject response) throws JSONException {
        if (response.has("current_observation") && response.has("hourly_forecast")) {
            JSONObject current = (JSONObject) response.get("current_observation");
            List<ForecastV2> forecastList = new ArrayList<ForecastV2>();
            List<ForecastV2> hourlyForecastList = getForecastListFromHourly(response.getJSONArray("hourly_forecast"));

            forecastList.add(new ForecastV2(getCurrentInterval(current, hourlyForecastList.size() != 0 ? hourlyForecastList.get(0) : null),
                    WundergroundWeatherBuilder.buildFromWunderground(current)));

            forecastList.addAll(hourlyForecastList);

            return new ForecastTableV2(forecastList);
        } else {
            return null;
        }
    }

    private static List<ForecastV2> getForecastListFromHourly(JSONArray hourly) throws JSONException {
        List<ForecastV2> forecasts = new ArrayList<ForecastV2>();

        if(hourly.length() != 0) {
            for (int i = 0 ; i < hourly.length() - 1 ; i++) {
                forecasts.add(new ForecastV2(getHourlyForecastInterval(hourly.getJSONObject(i), hourly.getJSONObject(i + 1)), WundergroundWeatherBuilder.buildFromWunderground(hourly.getJSONObject(i))));
            }
            forecasts.add(new ForecastV2(getHourlyForecastInterval(hourly.getJSONObject(hourly.length() - 1), null), WundergroundWeatherBuilder.buildFromWunderground(hourly.getJSONObject(hourly.length()))));
        }

        return forecasts;
    }

    private static Interval getCurrentInterval(JSONObject current, ForecastV2 forecast) throws JSONException {
        if(forecast == null) {
            DateTime currentStart = getCurrentStartDateTime(current);
            return new Interval(currentStart, DayPeriod.night.getInterval(currentStart).getEnd());
        }
        else {
            return new Interval(getCurrentStartDateTime(current), forecast.getInterval().getStart());
        }
    }

    private static Interval getHourlyForecastInterval(JSONObject hourlyForecast, JSONObject nextHourlyForecast) throws JSONException {
        if(nextHourlyForecast == null) {
            DateTime hourlyForecastStart = getHourlyForecastStartDateTime(hourlyForecast);
            return new Interval(hourlyForecastStart, DayPeriod.night.getInterval(hourlyForecastStart).getEnd());
        }
        else {
            return new Interval(getHourlyForecastStartDateTime(hourlyForecast), getHourlyForecastStartDateTime(nextHourlyForecast));
        }
    }

    private static DateTime getCurrentStartDateTime(JSONObject current) throws JSONException {
        return new DateTime(Long.parseLong(current.get("local_epoch").toString()) * 1000);
    }

    private static DateTime getHourlyForecastStartDateTime(JSONObject hourlyForecast) throws JSONException {
        return new DateTime(Long.parseLong(hourlyForecast.getJSONObject("FCTTIME").get("epoch").toString()) * 1000);
    }
}
