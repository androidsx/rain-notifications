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
        // TODO: FIXME: Retrieve temperature from weather
        return new WeatherWrapper(WundergroundWeatherTypeBuilder.buildFromWunderground(weather.getString("icon")), 0 , WeatherWrapper.TemperatureScale.CELSIUS);
    }
}
