package com.androidsx.rainnotifications.util;

import com.androidsx.rainnotifications.R;

public class WeatherIconHelper {
    private WeatherIconHelper() {
        //No-instantiate
    }

    public static int getWeatherIcon(String icon) {
        if(icon.equals(Constants.ForecastIO.Icon.RAIN)) {
            return R.drawable.rain;
        } else if(icon.equals(Constants.ForecastIO.Icon.CLEAR_DAY)) {
            return R.drawable.clear_day;
        } else if(icon.equals(Constants.ForecastIO.Icon.CLEAR_NIGHT)) {
            return R.drawable.clear_night;
        } else if(icon.equals(Constants.ForecastIO.Icon.CLOUDY)) {
            return R.drawable.cloudy;
        } else if(icon.equals(Constants.ForecastIO.Icon.PARTLY_CLOUDY_DAY)) {
            return R.drawable.partly_cloudy_day;
        } else if(icon.equals(Constants.ForecastIO.Icon.PARTLY_CLOUDY_NIGHT)) {
            return R.drawable.partly_cloudy_night;
        } else if(icon.equals(Constants.ForecastIO.Icon.SNOW)) {
            return R.drawable.snow;
        } else if(icon.equals(Constants.ForecastIO.Icon.THUNDERSTORM)) {
            return R.drawable.thunderstorm;
        } else if(icon.equals(Constants.ForecastIO.Icon.HAIL)) {
            return R.drawable.hail;
        } else {
            return R.drawable.unknown;
        }
    }
}
