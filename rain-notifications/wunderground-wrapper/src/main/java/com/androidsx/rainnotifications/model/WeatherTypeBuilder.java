package com.androidsx.rainnotifications.model;

import java.util.HashMap;
import java.util.Map;

import static com.androidsx.rainnotifications.model.WeatherType.RAIN;
import static com.androidsx.rainnotifications.model.WeatherType.SUNNY;

/**
 * Builder for {@link WeatherType}, where the mapping of "icon"s to our weather types is made. Try
 * disabling some of them to reduce the number of transitions we detect.
 * <p/>
 * Should not be used from outside of this project.
 */
public class WeatherTypeBuilder {
    final static private Map<String, WeatherType> ICON_TO_WEATHER_TYPE = new HashMap<String, WeatherType>() {{
        put("clear", SUNNY);
        put("sunny", SUNNY);
        //put("clear-night", CLEAR_NIGHT); // Exists in Forecast.io. Not supported by us yet
        put("rain", RAIN);
        //put("snow", SNOW); // Exists in Forecast.io. Not supported by us yet
        //put("sleet", SLEET); // Exists in Forecast.io. Not supported by us yet
        //put("wind", WIND); // Exists in Forecast.io. Not supported by us yet
        //put("fog", FOG); // Exists in Forecast.io. Not supported by us yet
        //put("cloudy", CLOUDY); // Exists in Forecast.io. Not supported by us yet
        //put("partlycloudy", PARTLY_CLOUDY_DAY); // Exists in Forecast.io. Not supported by us yet
    }};

    public static WeatherType buildFromWunderground(String icon) {
        if (ICON_TO_WEATHER_TYPE.containsKey(icon)) {
            return ICON_TO_WEATHER_TYPE.get(icon);
        } else {
            return WeatherType.UNKNOWN;
        }
    }
}
