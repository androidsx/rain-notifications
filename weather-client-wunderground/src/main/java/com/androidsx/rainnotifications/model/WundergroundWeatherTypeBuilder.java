package com.androidsx.rainnotifications.model;

import java.util.HashMap;
import java.util.Map;

import static com.androidsx.rainnotifications.model.WeatherType.CLOUDY;
import static com.androidsx.rainnotifications.model.WeatherType.RAIN;
import static com.androidsx.rainnotifications.model.WeatherType.CLEAR;
import static com.androidsx.rainnotifications.model.WeatherType.SNOW;
import static com.androidsx.rainnotifications.model.WeatherType.UNKNOWN;

/**
 * Builder for {@link WeatherType}, where the mapping of "icon"s to our weather types is made. Try
 * disabling some of them to reduce the number of transitions we detect.
 * <p/>
 * Should not be used from outside of this project.
 */
public class WundergroundWeatherTypeBuilder {
    final static private Map<String, WeatherType> ICON_TO_WEATHER_TYPE = new HashMap<String, WeatherType>() {{
        // Source: http://www.wunderground.com/weather/api/d/docs?d=resources/phrase-glossary
        put("clear", CLEAR);
        put("sunny", CLEAR);
        put("mostlysunny", CLEAR);
        put("rain", RAIN);
        put("cloudy", CLOUDY);
        put("mostlycloudy", CLOUDY);
        put("partlycloudy", CLOUDY);
        put("partlysunny", CLEAR);

        // Added in October 2014
        put("chanceflurries", RAIN);
        put("chancerain", RAIN);
        put("chancesleet", RAIN);
        put("chancesnow", SNOW);
        put("chancetstorms", RAIN);
        put("flurries", RAIN);
        put("fog", CLOUDY);
        put("hazy", CLOUDY);
        put("sleet", CLOUDY);
        put("snow", SNOW);
        put("tstorms", RAIN); // TODO: is worth to define STORM?

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
