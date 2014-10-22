package com.androidsx.rainnotifications.model;

/**
 * Status of the weather at a particular point in time (past, present of future).
 *
 * @see #equals
 */
public class WeatherWrapper {

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

    public float getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public float getTemperatureFahrenheit() {
        return temperatureFahrenheit;
    }

    @Override
    public String toString() {
        return type.toString() + ", " + temperatureCelsius + " °C, " + temperatureFahrenheit + " °F";
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
