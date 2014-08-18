package com.androidsx.rainnotifications.model;

/**
 * Status of the weather at a particular point in time (past, present of future).
 */
public class Weather {
    private final WeatherType type;
    private final double precipIntensity;

    public Weather(WeatherType type, double precipIntensity) {
        this.type = type;
        this.precipIntensity = precipIntensity;
    }

    public WeatherType getType() {
        return type;
    }

    /**
     * A very rough guide is that a value of 0 in./hr. corresponds to no precipitation, 0.002 in./hr.
     * corresponds to very light precipitation, 0.017 in./hr. corresponds to light precipitation,
     * 0.1 in./hr. corresponds to moderate precipitation, and 0.4 in./hr. corresponds to heavy precipitation.
     *
     * @return precipIntensity
     */
    public double getPrecipIntensity() {
        return precipIntensity;
    }

    @Override
    public String toString() {
        return type.toString();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Important note: Unknown weather types are ignored, that means, cloudy-unknown-unknown-cloudy
     * is not considered a weather transition.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null || getClass() != other.getClass()) {
            return false;
        } else {
            final Weather otherWeather = (Weather) other;
            if (getType() == WeatherType.UNKNOWN || ((Weather) other).getType() == WeatherType.UNKNOWN) {
                return true;
            } else {
                return getType() == otherWeather.getType();
            }
        }
    }

    @Override
    public int hashCode() {
        return getType() != null ? getType().hashCode() : 0;
    }
}
