package com.androidsx.rainnotifications.model;

/**
 * Status of the weather at a particular point in time (past, present of future).
 */
public class Weather {
    private final WeatherType type;

    public Weather(WeatherType type) {
        this.type = type;
    }

    public WeatherType getType() {
        return type;
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
     * If currentWeather is Unknown weather, the first checking is for prevent it and make a good forecastTable.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null || getClass() != other.getClass()) {
            return false;
        } else {
            final Weather otherWeather = (Weather) other;
            if (getType() == WeatherType.UNKNOWN && !(otherWeather.getType() == WeatherType.UNKNOWN)) {
                return false;
            } else if (getType() == WeatherType.UNKNOWN || ((Weather) other).getType() == WeatherType.UNKNOWN) {
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
