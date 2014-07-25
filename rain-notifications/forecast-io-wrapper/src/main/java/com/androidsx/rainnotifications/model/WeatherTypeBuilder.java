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
        put("clear-day", CLEAR_DAY);
        put("clear-night", CLEAR_NIGHT);
        put("rain", RAIN);
        put("snow", SNOW);
        put("sleet", SLEET);
        put("wind", WIND);
        put("fog", FOG);
        put("cloudy", CLOUDY);
        put("partly-cloudy-day", PARTLY_CLOUDY_DAY);
        put("partly-cloudy-night", PARTLY_CLOUDY_NIGHT);
    }};

    public static WeatherType buildFromForecastIo(String icon) {
        if (ICON_TO_WEATHER_TYPE.containsKey(icon)) {
            return ICON_TO_WEATHER_TYPE.get(icon);
        } else {
            return WeatherType.UNKNOWN;
        }
    }
}
