package com.androidsx.rainnotifications.model;

/**
 * Main weather type, such as cloudy or snow.
 * <p>
 * Note that these are not tied to those in weather service providers.
 * </p>
 */
public enum WeatherType {
    SUNNY,
    RAIN,
    CLOUDY,
    PARTLYCLOUDY,
    CLEAR_NIGHT,
    RAIN_NIGHT,
    CLOUDY_NIGH,
    PARTLYCLOUDY_NIGHT,

    /**
     * This is a weather type that is unknown for us, but it could be a valid one in the data
     * source, after all.
     */
    UNKNOWN;
}
