package com.androidsx.rainnotifications.model;

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

    /**
     * This is a weather type that is unknown for us, but it could be a valid one in the data
     * source, after all.
     */
    UNKNOWN;
}
