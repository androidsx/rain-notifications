package com.androidsx.rainnotifications.model;

/**
 * Status of the weather at a particular point in time (past, present of future).
 */
public class Weather {
    private final WeatherType type;
    private final double precipProbability;
    private final double precipIntensity;
    private final String precipType;

    /** Do not use outside of this package. Visibility raised to public for testing purposes. */
    /*public Weather(WeatherType type, float precipProbability, float precipIntensity, String precipType) {
        this.type = type;
        this.precipProbability = precipProbability;
        this.precipIntensity = precipIntensity;
        this.precipType = precipType;
    }*/

    private Weather(Builder builder) {
        type = builder.type;
        precipProbability = builder.precipProbability;
        precipIntensity = builder.precipIntensity;
        precipType = builder.precipType;
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

    /**
     * A string representing the type of precipitation occurring at the given time. If defined,
     * this property will have one of the following values: rain, snow, sleet (which applies to
     * each of freezing rain, ice pellets, and “wintery mix”), or hail. (If precipIntensity is zero,
     * then this property will not be defined.)
     *
     * @return precipType
     */
    public String getPrecipType() {
        return precipType;
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
        private double precipProbability = 0;
        private double precipIntensity = 0;
        private String precipType = "";

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

        public Builder precipType(String type) {
            precipType = type;
            return this;
        }

        public Weather build() {
            return new Weather(this);
        }
    }
}
