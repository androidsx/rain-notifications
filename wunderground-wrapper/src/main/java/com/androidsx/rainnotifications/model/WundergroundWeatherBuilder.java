package com.androidsx.rainnotifications.model;

/**
 * Builder for {@link Weather}.
 * <p/>
 * Should not be used from outside of this project.
 */
public class WundergroundWeatherBuilder {

    public static Weather buildFromForecastIo(String icon) {
        return new Weather(
                WundergroundWeatherTypeBuilder.buildFromForecastIo(icon));
    }
}
