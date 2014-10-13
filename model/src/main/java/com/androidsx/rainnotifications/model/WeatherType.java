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
public enum WeatherType {
    /**
     * This is a weather type that is unknown for us, but it could be a valid one in the data
     * source, after all.
     */
    UNKNOWN(0, R.string.clear_adjective, R.string.clear_gerund), // This values are never used for UNKNOWN Weather
    CLEAR(1, R.string.clear_adjective, R.string.clear_gerund),
    CLOUDY(2, R.string.cloudy_adjective, R.string.cloudy_gerund),
    RAIN(3, R.string.rain_adjective, R.string.rain_gerund),
    SNOW(4, R.string.snow_adjective, R.string.snow_gerund);

    /**
     * Higher values ​​indicate greater relevance
     */
    private int relevance;
    private int adjectiveResId;
    private int gerundResId;

    private WeatherType(int relevance, int adjectiveResId, int gerundResId) {
        this.relevance = relevance;
        this.adjectiveResId = adjectiveResId;
        this.gerundResId = gerundResId;
    }

    public int getRelevance() {
        return relevance;
    }

    public String getAdjective(Context context) {
        return context.getString(adjectiveResId);
    }

    public String getGerund(Context context) {
        return context.getString(gerundResId);
    }

    public static List<WeatherType> getMeaningfulWeatherTypes() {
        return Arrays.asList(WeatherType.CLEAR, WeatherType.RAIN, WeatherType.CLOUDY, WeatherType.SNOW);
    }
}
