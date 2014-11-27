package com.androidsx.rainnotifications.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Builder for {@link com.androidsx.rainnotifications.model.WeatherWrapper}.
 * <p/>
 * Should not be used from outside of this project.
 */
public class WundergroundDayWeatherBuilder {
    public static DailyWeatherWrapper buildFromWunderground(JSONObject weather) throws JSONException {
        return new DailyWeatherWrapper(retrieveWeatherType(weather), retrieveMinCelsiusTemperature(weather), retrieveMaxCelsiusTemperature(weather), WeatherWrapper.TemperatureScale.CELSIUS);
    }

    private static WeatherType retrieveWeatherType(JSONObject weather) throws JSONException {
        return WundergroundWeatherTypeBuilder.buildFromWunderground(weather.getString("icon"));
    }

    private static float retrieveMinCelsiusTemperature(JSONObject weather) throws JSONException {
        return (float) weather.getJSONObject("low").getDouble("celsius");
    }

    private static float retrieveMaxCelsiusTemperature(JSONObject weather) throws JSONException {
        return (float) weather.getJSONObject("high").getDouble("celsius");
    }
}
