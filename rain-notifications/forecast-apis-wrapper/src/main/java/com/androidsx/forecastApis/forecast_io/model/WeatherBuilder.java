package com.androidsx.forecastApis.forecast_io.model;

import com.androidsx.rainnotifications.model.Weather;
import com.forecast.io.v2.transfer.DataPoint;

/**
 * Builder for {@link com.androidsx.rainnotifications.model.Weather}.
 * <p/>
 * Should not be used from outside of this project.
 */
public class WeatherBuilder {

    public static Weather buildFromForecastIo(DataPoint dataPoint) {
        return new Weather(WeatherTypeBuilder.buildFromForecastIo(dataPoint.getIcon()));
    }
}
