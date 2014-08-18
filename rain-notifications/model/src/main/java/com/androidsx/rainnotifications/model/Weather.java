package com.androidsx.rainnotifications.model;

/**
 * Status of the weather at a particular point in time (past, present of future).
 */
public class Weather {
    private final WeatherType type;
    private final double precipProbability;
    private final double precipIntensity;

    private Weather(Builder builder) {
        type = builder.type;
        precipProbability = builder.precipProbability;
        precipIntensity = builder.precipIntensity;
    }

    public WeatherType getType() {
        return type;
    }

    /**
     * A numerical value between 0 and 1 (inclusive) representing the probability
     * of precipitation occuring at the given time.
     *
     * @return precipProbability
     */
    public double getPrecipProbability() {
        return precipProbability;
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

    public static class Builder {
        //Required parameter
        private final WeatherType type;

        //Optional parameters - initialized to default values
        private double precipProbability = -1;
        private double precipIntensity = -1;

        public Builder(WeatherType type) {
            this.type = type;
        }

        public Builder precipProbability(double probability) {
            precipProbability = probability;
            return this;
        }

        public Builder precipIntensity(double intensity) {
            precipIntensity = intensity;
            return this;
        }

        public Weather build() {
            return new Weather(this);
        }
    }
}
