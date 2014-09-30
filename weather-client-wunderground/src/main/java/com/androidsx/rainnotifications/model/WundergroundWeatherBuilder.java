package com.androidsx.rainnotifications.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Builder for {@link com.androidsx.rainnotifications.model.WeatherWrapperV2}.
 * <p/>
 * Should not be used from outside of this project.
 */
public class WundergroundWeatherBuilder {
    public static WeatherWrapperV2 buildFromWunderground(JSONObject weather) throws JSONException {
        return new WeatherWrapperV2(WundergroundWeatherTypeBuilder.buildFromWunderground(weather.getString("icon")));
    }
}
