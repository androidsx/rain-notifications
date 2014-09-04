package com.androidsx.rainnotifications.model;

/**
 * Builder for {@link Weather}.
 * <p/>
 * Should not be used from outside of this project.
 */
public class WeatherBuilder {

    public static Weather buildFromForecastIo(String icon) {
        return new Weather(
                WeatherTypeBuilder.buildFromForecastIo(icon));
    }
}
