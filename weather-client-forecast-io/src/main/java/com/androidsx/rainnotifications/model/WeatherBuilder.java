package com.androidsx.rainnotifications.model;

import com.forecast.io.v2.transfer.DataPoint;

/**
 * Builder for {@link com.androidsx.rainnotifications.model.WeatherWrapperV2}.
 * <p/>
 * Should not be used from outside of this project.
 */
public class WeatherBuilder {

    public static WeatherWrapperV2 buildFromForecastIo(DataPoint dataPoint) {
        return new WeatherWrapperV2(WeatherTypeBuilder.buildFromForecastIo(dataPoint.getIcon()));
    }
}
