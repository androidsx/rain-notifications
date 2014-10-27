package com.androidsx.rainnotifications.model;

import android.content.Context;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Status of the weather at a particular point in time (past, present of future).
 *
 * @see #equals
 */
public class WeatherWrapper {
    // United States, Puerto Rico, Guam, the U.S. Virgin Islands, Bahamas, Belize, the Cayman Islands and Palau
    // http://en.wikipedia.org/wiki/Fahrenheit#Usage
    // http://www.mathguide.de/info/tools/countrycode.html
    public static final List<String> FAHRENHEIT_COUNTRY_CODES = Arrays.asList("US", "PR", "GU", "VI", "BS", "BZ", "KY", "PW");
    public static final char CELSIUS_SYMBOL = '\u2103';
    public static final char FAHRENHEIT_SYMBOL = '\u2109';

    public enum TemperatureScale {CELSIUS, FAHRENHEIT}

    private final WeatherType type;
    private final float temperatureCelsius;
    private final float temperatureFahrenheit;

    public WeatherWrapper(WeatherType type, float temperature, TemperatureScale scale) {
        this.type = type;

        if(scale.equals(TemperatureScale.CELSIUS)) {
            temperatureCelsius = temperature;
            temperatureFahrenheit = temperature * 1.8f + 32; //Celsius to Fahrenheit : (°C × 1.8) + 32 =°F
        }
        else {
            temperatureFahrenheit = temperature;
            temperatureCelsius = (temperature - 32) / 1.8f; //Fahrenheit to Celsius : (°F − 32) ÷ 1.8 =°C
        }
    }

    public WeatherType getWeatherType() {
        return type;
    }

    public float getTemperature(TemperatureScale scale) {
        if(scale.equals(TemperatureScale.CELSIUS)) {
            return temperatureCelsius;
        }
        else {
            return temperatureFahrenheit;
        }
    }

    // TODO: Add some logic for allow user to choose celsius or Fahrenheit.
    public String getReadableTemperature(Context context) {
        if(FAHRENHEIT_COUNTRY_CODES.contains(context.getResources().getConfiguration().locale.getCountry().toString())) {
            return new DecimalFormat("#").format(temperatureFahrenheit) + FAHRENHEIT_SYMBOL;
        }
        else {
            return new DecimalFormat("#").format(temperatureCelsius) + CELSIUS_SYMBOL;
        }
    }

    @Override
    public String toString() {
        return type.toString() + ", " + temperatureCelsius + CELSIUS_SYMBOL + ", " + temperatureFahrenheit + FAHRENHEIT_SYMBOL;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Important note about unknown transitions:
     * <p>
     * Transitions like UNKNOWN -> KNOWN are considered transitions.
     * Transitions like KNOWN -> UNKNOWN are NOT considered transitions.
     * <p>
     * That implies UNKNOWN -> sunny will end up being "It's gonna be sunny again", but sunny -> UNKNOWN won't trigger
     * a change. Then, sunny -> UNKNOWN -> rain will essentially be considered a sunny -> rain transition.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null || getClass() != other.getClass()) {
            return false;
        } else {
            final WeatherWrapper otherWeather = (WeatherWrapper) other;
            if (getWeatherType() == WeatherType.UNKNOWN && !(otherWeather.getWeatherType() == WeatherType.UNKNOWN)) {
                return false;
            } else if (getWeatherType() == WeatherType.UNKNOWN || ((WeatherWrapper) other).getWeatherType() == WeatherType.UNKNOWN) {
                return true;
            } else {
                return getWeatherType() == otherWeather.getWeatherType();
            }
        }
    }

    @Override
    public int hashCode() {
        return getWeatherType() != null ? getWeatherType().hashCode() : 0;
    }
}
