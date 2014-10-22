package com.androidsx.rainnotifications.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Builder for {@link WeatherWrapper}.
 * <p/>
 * Should not be used from outside of this project.
 */
public class WundergroundWeatherBuilder {
    public static WeatherWrapper buildFromWunderground(JSONObject weather) throws JSONException {
        return new WeatherWrapper(retrieveWeatherType(weather), retrieveCelsiusTemperature(weather), WeatherWrapper.TemperatureScale.CELSIUS);
    }

    private static WeatherType retrieveWeatherType(JSONObject weather) throws JSONException {
        return WundergroundWeatherTypeBuilder.buildFromWunderground(weather.getString("icon"));
    }

    private static float retrieveCelsiusTemperature(JSONObject weather) throws JSONException {
        if(weather.has("temp_c")) { // Case current_observation
            return (float) weather.getDouble("temp_c");
        }
        else { // Case hourly_forecast
            return (float) weather.getJSONObject("temp").getDouble("metric");
        }
    }
}
