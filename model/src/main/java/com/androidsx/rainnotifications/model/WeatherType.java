package com.androidsx.rainnotifications.model;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

/**
 * Main weather type, such as cloudy or snow.
 * <p>
 * Note that these are not tied to those in weather service providers.
 * </p>
 */
public enum WeatherType {
    CLEAR,
    RAIN,
    CLOUDY,
    PARTLY_CLOUDY,
    CLEAR_NIGHT,
    CLOUDY_NIGHT,
    PARTLY_CLOUDY_NIGHT,
    @SerializedName("*")
    WHATEVER,

    /**
     * This is a weather type that is undefined on dayMessages.json
     */
    UNDEFINED,

    /**
     * This is a weather type that is unknown for us, but it could be a valid one in the data
     * source, after all.
     */
    UNKNOWN;

    public static List<WeatherType> getMeaningfulWeatherTypes() {
        return Arrays.asList(WeatherType.CLEAR, WeatherType.RAIN, WeatherType.CLOUDY, WeatherType.PARTLY_CLOUDY);
    }
}
