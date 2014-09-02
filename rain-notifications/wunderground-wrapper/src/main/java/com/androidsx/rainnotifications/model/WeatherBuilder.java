package com.androidsx.rainnotifications.model;

import com.fortysevendeg.android.wunderground.api.service.response.HourlyResponse;
import com.fortysevendeg.android.wunderground.api.service.response.ObservationResponse;

/**
 * Builder for {@link Weather}.
 * <p/>
 * Should not be used from outside of this project.
 */
public class WeatherBuilder {

    public static Weather buildFromWunderground(ObservationResponse current) {
        return new Weather(
                WeatherTypeBuilder.buildFromWunderground(current.getIcon()));
    }

    public static Weather buildFromWunderground(HourlyResponse hourly) {
        return new Weather(
                WeatherTypeBuilder.buildFromWunderground(hourly.getIcon()));
    }
}
