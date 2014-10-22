package com.androidsx.rainnotifications.model;

import com.forecast.io.v2.transfer.DataPoint;

/**
 * Builder for {@link WeatherWrapper}.
 * <p/>
 * Should not be used from outside of this project.
 */
public class WeatherBuilder {

    public static WeatherWrapper buildFromForecastIo(DataPoint dataPoint) {
        // TODO: FIXME: Retrieve temperature from dataPoint (if we want to use forecast.io)
        return new WeatherWrapper(WeatherTypeBuilder.buildFromForecastIo(dataPoint.getIcon()), 0, WeatherWrapper.TemperatureScale.CELSIUS);
    }
}
