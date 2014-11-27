package com.androidsx.rainnotifications.model;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for {@link com.androidsx.rainnotifications.model.DailyForecastTable}.
 * <p>
 * Should not be used from outside of this project.
 */
public class WundergroundDailyTableBuilder {
    public static DailyForecastTable buildFromWunderground(JSONObject response) throws JSONException {

        if(response.has("forecast")) {
            JSONObject forecast = response.getJSONObject("forecast");
            if(forecast.has("simpleforecast")) {
                JSONObject simpleforecast = forecast.getJSONObject("simpleforecast");
                if(simpleforecast.has("forecastday")) {
                    JSONArray forecastday = simpleforecast.getJSONArray("forecastday");
                    if(forecastday != null && forecastday.length() > 0) {
                        return DailyForecastTable.fromForecastList(getForecastListFromDaily(forecastday));
                    }
                }

            }
        }

        return null;
    }

    private static List<DailyForecast> getForecastListFromDaily(JSONArray daily) throws JSONException {
        List<DailyForecast> forecasts = new ArrayList<DailyForecast>();
        for (int i = 0 ; i < daily.length() - 1 ; i++) {
            forecasts.add(new DailyForecast(getDay(daily.getJSONObject(i)), WundergroundDayWeatherBuilder.buildFromWunderground(daily.getJSONObject(i))));
        }
        return forecasts;
    }

    private static DateTime getDay(JSONObject dayForecast) throws JSONException {
        return new DateTime(Long.parseLong(dayForecast.getJSONObject("date").get("epoch").toString()) * 1000);
    }
}
