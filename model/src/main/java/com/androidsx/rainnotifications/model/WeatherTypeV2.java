package com.androidsx.rainnotifications.model;

import android.content.Context;

import java.util.Arrays;
import java.util.List;


/**
 * Main weather type, such as cloudy or snow.
 * <p>
 * Note that these are not tied to those in weather service providers.
 * </p>
 */
public enum WeatherTypeV2 {
    CLEAR(R.string.clear_adjective, R.string.clear_gerund),
    RAIN(R.string.rain_adjective, R.string.rain_gerund),
    CLOUDY(R.string.cloudy_adjective, R.string.cloudy_gerund),
    SNOW(R.string.snow_adjective, R.string.snow_gerund),

    /**
     * This is a weather type that is unknown for us, but it could be a valid one in the data
     * source, after all.
     */
    UNKNOWN(R.string.clear_adjective, R.string.clear_gerund); // This values are never used for UNKNOWN Weather

    private int adjective_resource;
    private int gerund_resource;

    private WeatherTypeV2(int adjective_resource, int gerund_resource) {
        this.adjective_resource = adjective_resource;
        this.gerund_resource = gerund_resource;
    }

    public String getAdjective(Context context) {
        return context.getString(adjective_resource);
    }

    public String getGerund(Context context) {
        return context.getString(gerund_resource);
    }

    public static List<WeatherTypeV2> getMeaningfulWeatherTypes() {
        return Arrays.asList(WeatherTypeV2.CLEAR, WeatherTypeV2.RAIN, WeatherTypeV2.CLOUDY, WeatherTypeV2.SNOW);
    }
}
