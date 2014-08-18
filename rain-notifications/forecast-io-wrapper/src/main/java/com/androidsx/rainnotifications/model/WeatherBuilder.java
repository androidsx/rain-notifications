package com.androidsx.rainnotifications.model;

import com.forecast.io.v2.transfer.DataPoint;

/**
 * Builder for {@link Weather}.
 * <p/>
 * Should not be used from outside of this project.
 */
public class WeatherBuilder {

    public static Weather buildFromForecastIo(DataPoint dataPoint) {
        Weather.Builder weatherBuilder = new Weather.Builder(WeatherTypeBuilder.buildFromForecastIo(dataPoint.getIcon()));
        weatherBuilder.precipProbability(dataPoint.getPrecipProbability());
        if(dataPoint.getPrecipIntensity() > 0) {
            weatherBuilder
                    .precipIntensity(dataPoint.getPrecipIntensity())
                    .precipType(dataPoint.getPrecipType());
        }
        return weatherBuilder.build();
    }
}
