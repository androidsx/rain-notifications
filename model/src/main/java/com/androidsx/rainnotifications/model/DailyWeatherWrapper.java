package com.androidsx.rainnotifications.model;

import android.content.Context;

import java.text.DecimalFormat;

/**
 * Status of the weather at a particular point in time (past, present of future).
 *
 * @see #equals
 */
public class DailyWeatherWrapper {
    private final WeatherType type;
    private final float minTemperatureCelsius;
    private final float minTemperatureFahrenheit;
    private final float maxTemperatureCelsius;
    private final float maxTemperatureFahrenheit;

    public DailyWeatherWrapper(WeatherType type, float minTemperature, float maxTemperature, WeatherWrapper.TemperatureScale scale) {
        this.type = type;

        if(scale.equals(WeatherWrapper.TemperatureScale.CELSIUS)) {
            minTemperatureCelsius = minTemperature;
            minTemperatureFahrenheit = minTemperature * 1.8f + 32; //Celsius to Fahrenheit : (°C × 1.8) + 32 =°F

            maxTemperatureCelsius = maxTemperature;
            maxTemperatureFahrenheit = maxTemperature * 1.8f + 32; //Celsius to Fahrenheit : (°C × 1.8) + 32 =°F
        }
        else {
            minTemperatureFahrenheit = minTemperature;
            minTemperatureCelsius = (minTemperature - 32) / 1.8f; //Fahrenheit to Celsius : (°F − 32) ÷ 1.8 =°C

            maxTemperatureFahrenheit = maxTemperature;
            maxTemperatureCelsius = (maxTemperature - 32) / 1.8f; //Fahrenheit to Celsius : (°F − 32) ÷ 1.8 =°C
        }
    }

    public WeatherType getWeatherType() {
        return type;
    }

    public float getMinTemperature(WeatherWrapper.TemperatureScale scale) {
        if(scale.equals(WeatherWrapper.TemperatureScale.CELSIUS)) {
            return minTemperatureCelsius;
        }
        else {
            return minTemperatureFahrenheit;
        }
    }

    public float getMaxTemperature(WeatherWrapper.TemperatureScale scale) {
        if(scale.equals(WeatherWrapper.TemperatureScale.CELSIUS)) {
            return maxTemperatureCelsius;
        }
        else {
            return maxTemperatureFahrenheit;
        }
    }

    // TODO: Add some logic for allow user to choose celsius or Fahrenheit.
    public String getReadableMinTemperature(Context context) {
        if(WeatherWrapper.TemperatureScale.getLocaleScale(context).equals(WeatherWrapper.TemperatureScale.FAHRENHEIT)) {
            return new DecimalFormat("#").format(minTemperatureFahrenheit) + WeatherWrapper.FAHRENHEIT_SYMBOL;
        }
        else {
            return new DecimalFormat("#").format(minTemperatureCelsius) + WeatherWrapper.CELSIUS_SYMBOL;
        }
    }

    // TODO: Add some logic for allow user to choose celsius or Fahrenheit.
    public String getReadableMaxTemperature(Context context) {
        if(WeatherWrapper.TemperatureScale.getLocaleScale(context).equals(WeatherWrapper.TemperatureScale.FAHRENHEIT)) {
            return new DecimalFormat("#").format(maxTemperatureFahrenheit) + WeatherWrapper.FAHRENHEIT_SYMBOL;
        }
        else {
            return new DecimalFormat("#").format(maxTemperatureCelsius) + WeatherWrapper.CELSIUS_SYMBOL;
        }
    }

    @Override
    public String toString() {
        return type.toString() + ", " + minTemperatureCelsius + WeatherWrapper.CELSIUS_SYMBOL + ", " + minTemperatureFahrenheit + WeatherWrapper.FAHRENHEIT_SYMBOL
                + ", " + maxTemperatureCelsius + WeatherWrapper.CELSIUS_SYMBOL + ", " + maxTemperatureFahrenheit + WeatherWrapper.FAHRENHEIT_SYMBOL;
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
            final DailyWeatherWrapper otherWeather = (DailyWeatherWrapper) other;
            if (getWeatherType() == WeatherType.UNKNOWN && !(otherWeather.getWeatherType() == WeatherType.UNKNOWN)) {
                return false;
            } else if (getWeatherType() == WeatherType.UNKNOWN || ((DailyWeatherWrapper) other).getWeatherType() == WeatherType.UNKNOWN) {
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
