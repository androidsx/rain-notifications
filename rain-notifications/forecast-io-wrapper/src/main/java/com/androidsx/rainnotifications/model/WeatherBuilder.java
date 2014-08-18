package com.androidsx.rainnotifications.model;

import com.forecast.io.v2.transfer.DataPoint;

/**
 * Builder for {@link Weather}.
 * <p/>
 * Should not be used from outside of this project.
 */
public class WeatherBuilder {

    public static Weather buildFromForecastIo(DataPoint dataPoint) {
        return new Weather(
                WeatherTypeBuilder.buildFromForecastIo(dataPoint.getIcon()),
                dataPoint.getPrecipProbability(),
                dataPoint.getPrecipIntensity());
    }
}
