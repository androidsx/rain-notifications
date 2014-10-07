package com.androidsx.rainnotifications.model;

import java.util.Arrays;
import java.util.List;

/**
 * Main weather type, such as cloudy or snow.
 * <p>
 * Note that these are not tied to those in weather service providers.
 * </p>
 */
public enum WeatherTypeV2 {
    CLEAR,
    RAIN,
    CLOUDY,
    SNOW,

    /**
     * This is a weather type that is unknown for us, but it could be a valid one in the data
     * source, after all.
     */
    UNKNOWN;

    public static List<WeatherTypeV2> getMeaningfulWeatherTypes() {
        return Arrays.asList(WeatherTypeV2.CLEAR, WeatherTypeV2.RAIN, WeatherTypeV2.CLOUDY, WeatherTypeV2.SNOW);
    }
}
