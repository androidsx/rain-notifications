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
        put("clear-day", SUNNY);
        put("clear-night", CLEAR_NIGHT);
        put("rain", RAIN);
        put("rain_night", RAIN_NIGHT); // ATTENTION: Not exists in Forecast.io, we must simulate it
        put("cloudy", CLOUDY);
        put("cloudy_night", CLOUDY_NIGHT); // ATTENTION: Not exists in Forecast.io, we must simulate it
        put("partly-cloudy-day", PARTLY_CLOUDY);
        put("partly-cloudy-night", PARTLY_CLOUDY_NIGHT);
        //put("snow", SNOW); // Exists in Forecast.io. Not supported by us yet
        //put("sleet", SLEET); // Exists in Forecast.io. Not supported by us yet
        //put("wind", WIND); // Exists in Forecast.io. Not supported by us yet
        //put("fog", FOG); // Exists in Forecast.io. Not supported by us yet
    }};

    public static WeatherType buildFromForecastIo(String icon) {
        if (ICON_TO_WEATHER_TYPE.containsKey(icon)) {
            return ICON_TO_WEATHER_TYPE.get(icon);
        } else {
            return WeatherType.UNKNOWN;
        }
    }
}
