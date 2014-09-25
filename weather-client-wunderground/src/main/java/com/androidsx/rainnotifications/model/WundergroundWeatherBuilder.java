package com.androidsx.rainnotifications.model;

/**
 * Builder for {@link Weather}.
 * <p/>
 * Should not be used from outside of this project.
 */
public class WundergroundWeatherBuilder {

    public static Weather buildFromWunderground(String icon, int temp) {
        return new Weather(WundergroundWeatherTypeBuilder.buildFromWunderground(icon))
                .setMetaData(new WeatherMetaData.Builder().temp(temp).build());
    }
}
