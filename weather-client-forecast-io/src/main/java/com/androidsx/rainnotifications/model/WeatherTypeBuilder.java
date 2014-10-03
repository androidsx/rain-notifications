package com.androidsx.rainnotifications.model;

import java.util.HashMap;
import java.util.Map;

import static com.androidsx.rainnotifications.model.WeatherType.*;

/**
 * Builder for {@link WeatherType}, where the mapping of "icon"s to our weather types is made. Try
 * disabling some of them to reduce the number of transitions we detect.
 * <p/>
 * Should not be used from outside of this project.
 */
public class WeatherTypeBuilder {
    final static private Map<String, WeatherType> ICON_TO_WEATHER_TYPE = new HashMap<String, WeatherType>() {{
        // Source: https://developer.forecast.io/docs/v2

        put("clear-day", CLEAR);
        put("clear-night", CLEAR);
        put("rain", RAIN);
        put("cloudy", CLOUDY);
        put("partly-cloudy-day", CLOUDY);
        put("partly-cloudy-night", CLOUDY);

        // added in October 2014
        put("snow", SNOW);
        put("sleet", RAIN); // Exists in Forecast.io. Not supported by us yet
        put("wind", CLOUDY); // Exists in Forecast.io. Not supported by us yet
        put("fog", CLOUDY); // Exists in Forecast.io. Not supported by us yet
    }};

    public static WeatherType buildFromForecastIo(String icon) {
        if (ICON_TO_WEATHER_TYPE.containsKey(icon)) {
            return ICON_TO_WEATHER_TYPE.get(icon);
        } else {
            return WeatherType.UNKNOWN;
        }
    }
}
