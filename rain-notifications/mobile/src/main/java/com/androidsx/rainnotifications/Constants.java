package com.androidsx.rainnotifications;

import com.androidsx.rainnotifications.model.WeatherType;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static class AlarmId{
        public static final int LOCATION_ID = 0;
        public static final int WEATHER_ID = 1;
    }
    public static class Localization {
        public static final Double NEW_YORK_LAT = 40.71310899271792;
        public static final Double NEW_YORK_LON = -74.005758909787;
        public static final Double WASHINGTON_LAT = 38.9072423927665;
        public static final Double WASHINGTON_LON = -77.03653810608216;
        public static final Double HOUSTON_LAT = 29.75974563585816;
        public static final Double HOUSTON_LON = -95.36844546242673;
    }
    public static class Extras {
        public static final String EXTRA_LAT = "extra_lat";
        public static final String EXTRA_LON = "extra_lon";
    }

    public static final Map<WeatherType, Integer> FORECAST_ICONS = new HashMap<WeatherType, Integer>() {
        {
            put(WeatherType.CLEAR_DAY, R.drawable.clear_day);
            put(WeatherType.CLEAR_NIGHT, R.drawable.clear_night);
            put(WeatherType.RAIN, R.drawable.rain);
            put(WeatherType.SNOW, R.drawable.snow);
            put(WeatherType.CLOUDY, R.drawable.cloudy);
            put(WeatherType.PARTLY_CLOUDY_DAY, R.drawable.partly_cloudy_day);
            put(WeatherType.PARTLY_CLOUDY_NIGHT, R.drawable.partly_cloudy_night);
            put(WeatherType.UNKNOWN, R.drawable.unknown);
        }
    };

    public static class SharedPref {
        public static final String SHARED_RAIN = "shared_rain";

        public static final String ADDRESS = "address";
        public static final String CURRENTLY = "currently";
        public static final String HISTORY = "history";
        public static final String CURRENTLY_ICON = "currently_icon";
        public static final String NEXT_FORECAST_ICON = "next_forecast_icon";
    }
}
