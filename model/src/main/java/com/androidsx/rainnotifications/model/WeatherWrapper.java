package com.androidsx.rainnotifications.model;

/**
 * Status of the weather at a particular point in time (past, present of future).
 *
 * @see #equals
 */
public class WeatherWrapper {
    private final WeatherType type;

    public WeatherWrapper(WeatherType type) {
        this.type = type;
    }

    public WeatherType getType() {
        return type;
    }

    public boolean isUnknownWeather() {
        return getType().equals(WeatherType.UNKNOWN);
    }

    @Override
    public String toString() {
        return type.toString();
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
            if (getType() == WeatherType.UNKNOWN && !(otherWeather.getType() == WeatherType.UNKNOWN)) {
                return false;
            } else if (getType() == WeatherType.UNKNOWN || ((WeatherWrapper) other).getType() == WeatherType.UNKNOWN) {
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
