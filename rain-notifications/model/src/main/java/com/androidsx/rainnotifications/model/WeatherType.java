package com.androidsx.rainnotifications.model;

/**
 * Main weather type, such as cloudy or snow.
 */
public enum WeatherType {
    CLEAR_DAY,
    CLEAR_NIGHT,
    RAIN,
    SNOW,
    SLEET,
    WIND,
    FOG,
    CLOUDY,
    PARTLY_CLOUDY_DAY,
    PARTLY_CLOUDY_NIGHT,

    /**
     * This is a weather type that is unknown for us, but it could be a valid one in the data
     * source, after all.
     */
    UNKNOWN;
}
