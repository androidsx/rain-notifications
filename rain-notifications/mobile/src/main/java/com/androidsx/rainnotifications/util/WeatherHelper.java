package com.androidsx.rainnotifications.util;

import com.androidsx.rainnotifications.Constants;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.WeatherType;

public class WeatherHelper {

    private WeatherHelper() {
        //No-instantiate
    }

    /**
     * Method for obtain the resource icon, depending on weather passed as a param, using its type.
     *
     * @param weather
     * @return int - resource id
     */
    public static int getIconFromWeather(Weather weather) {
        if (weather == null) return Constants.FORECAST_ICONS.get(WeatherType.UNKNOWN);
        return Constants.FORECAST_ICONS.containsKey(weather.getType())
                ? Constants.FORECAST_ICONS.get(weather.getType())
                : Constants.FORECAST_ICONS.get(WeatherType.UNKNOWN);
    }
}
