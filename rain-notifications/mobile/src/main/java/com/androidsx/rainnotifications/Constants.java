package com.androidsx.rainnotifications;

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
    public static class ForecastIO {
        public static final Map<String, Integer> FORECAST_ICON = new HashMap<String, Integer>() {
            {
                put(Icon.CLEAR_DAY, R.drawable.clear_day);
                put(Icon.CLEAR_NIGHT, R.drawable.clear_night);
                put(Icon.RAIN, R.drawable.rain);
                put(Icon.SNOW, R.drawable.snow);
                put(Icon.CLOUDY, R.drawable.cloudy);
                put(Icon.PARTLY_CLOUDY_DAY, R.drawable.partly_cloudy_day);
                put(Icon.PARTLY_CLOUDY_NIGHT, R.drawable.partly_cloudy_night);
                put(Icon.HAIL, R.drawable.hail);
                put(Icon.THUNDERSTORM, R.drawable.thunderstorm);
                put(Icon.UNKNOWN, R.drawable.unknown);
            }
        };

        public static class Icon {
            public static final String CLEAR_DAY = "clear-day";
            public static final String CLEAR_NIGHT = "clear-night";
            public static final String RAIN = "rain";
            public static final String SNOW = "snow";
            public static final String SLEET = "sleet";
            public static final String WIND = "wind";
            public static final String FOG = "fog";
            public static final String CLOUDY = "cloudy";
            public static final String PARTLY_CLOUDY_DAY = "partly-cloudy-day";
            public static final String PARTLY_CLOUDY_NIGHT = "partly-cloudy-night";
            public static final String HAIL = "hail";
            public static final String THUNDERSTORM = "thunderstorm";
            public static final String TORNADO = "tornado";
            public static final String UNKNOWN = "unknown";
        }
    }
    public static class SharedPref {
        public static final String SHARED_RAIN = "shared_rain";

        public static final String ADDRESS = "address";
        public static final String CURRENTLY = "currently";
        public static final String HISTORY = "history";
        public static final String CURRENTLY_ICON = "currently_icon";
        public static final String NEXT_FORECAST_ICON = "next_forecast_icon";
    }
}
