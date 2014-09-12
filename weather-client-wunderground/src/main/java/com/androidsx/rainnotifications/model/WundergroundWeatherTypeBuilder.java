package com.androidsx.rainnotifications.model;

import java.util.HashMap;
import java.util.Map;

import static com.androidsx.rainnotifications.model.WeatherType.CLEAR_NIGHT;
import static com.androidsx.rainnotifications.model.WeatherType.CLOUDY;
import static com.androidsx.rainnotifications.model.WeatherType.CLOUDY_NIGHT;
import static com.androidsx.rainnotifications.model.WeatherType.PARTLY_CLOUDY;
import static com.androidsx.rainnotifications.model.WeatherType.PARTLY_CLOUDY_NIGHT;
import static com.androidsx.rainnotifications.model.WeatherType.RAIN;
import static com.androidsx.rainnotifications.model.WeatherType.RAIN_NIGHT;
import static com.androidsx.rainnotifications.model.WeatherType.SUNNY;
import static com.androidsx.rainnotifications.model.WeatherType.UNKNOWN;

/**
 * Builder for {@link WeatherType}, where the mapping of "icon"s to our weather types is made. Try
 * disabling some of them to reduce the number of transitions we detect.
 * <p/>
 * Should not be used from outside of this project.
 */
public class WundergroundWeatherTypeBuilder {
    final static private Map<String, WeatherType> ICON_TO_WEATHER_TYPE = new HashMap<String, WeatherType>() {{
        put("clear", SUNNY);
        put("clear_night", CLEAR_NIGHT); // ATTENTION: Not exists in Wunderground, we must simulate it
        put("sunny", SUNNY);
        put("mostlysunny", SUNNY);
        put("rain", RAIN);
        put("rain_night", RAIN_NIGHT); // ATTENTION: Not exists in Wunderground, we must simulate it
        put("cloudy", CLOUDY);
        put("mostlycloudy", CLOUDY);
        put("cloudy_night", CLOUDY_NIGHT); // ATTENTION: Not exists in Wunderground, we must simulate it
        put("partlycloudy", PARTLY_CLOUDY);
        put("partlysunny", PARTLY_CLOUDY);
        put("partlycloudy_night", PARTLY_CLOUDY_NIGHT); // ATTENTION: Not exists in Wunderground, we must simulate it
        //put("snow", SNOW); // Exists in Wunderground. Not supported by us yet
        //put("sleet", SLEET); // Exists in Wunderground. Not supported by us yet
        //put("fog", FOG); // Exists in Wunderground. Not supported by us yet
        put("unknown", UNKNOWN);
    }};

    public static WeatherType buildFromWunderground(String icon) {
        if (ICON_TO_WEATHER_TYPE.containsKey(icon)) {
            return ICON_TO_WEATHER_TYPE.get(icon);
        } else {
            return WeatherType.UNKNOWN;
        }
    }
}
