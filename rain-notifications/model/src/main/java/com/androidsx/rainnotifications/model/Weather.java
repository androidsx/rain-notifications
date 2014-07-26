package com.androidsx.rainnotifications.model;

/**
 * Status of the weather at a particular point in time (past, present of future).
 */
public class Weather {
    private final WeatherType type;

    /** Do not use outside of this package. Visibility raised to public for testing purposes. */
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
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null || getClass() != other.getClass()) {
            return false;
        } else {
            final Weather otherWeather = (Weather) other;
            if((getType() == WeatherType.CLEAR_DAY || getType() == WeatherType.CLEAR_NIGHT)
                    && (otherWeather.getType() == WeatherType.CLEAR_DAY || otherWeather.getType() == WeatherType.CLEAR_NIGHT)) {
                return true;
            } else if((getType() == WeatherType.PARTLY_CLOUDY_DAY || getType() == WeatherType.PARTLY_CLOUDY_NIGHT)
                    && (otherWeather.getType() == WeatherType.PARTLY_CLOUDY_DAY || otherWeather.getType() == WeatherType.PARTLY_CLOUDY_NIGHT)) {
                return true;
            } else if (getType() == WeatherType.UNKNOWN || otherWeather.getType() == WeatherType.UNKNOWN) {
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
