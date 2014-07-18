package com.androidsx.rainnotifications.util;

public class Constants {
    public static class Time{
        public static final String TIME_ZONE_MADRID = "Europe/Madrid";
        public static final String TIME_ZONE_NEW_YORK = "America/New_York";
        public static final String TIME_FORMAT = "HH:mm";

        public static final int ONE_MINUTE_MILLIS = 1000 * 60 * 1;
        public static final int TEN_MINUTES_MILLIS = 1000 * 60 * 10;
        public static final int HALF_HOUR_MILLIS = 1000 * 60 * 30;
        public static final int HOUR_MILLIS = 1000 * 60 * 60 * 1;
    }
    public static class Distance{
        public static final float KM = (float) 1.0;
    }
    public static class Localization {
        public static final Double NEW_YORK_LAT = 40.71310899271792;
        public static final Double NEW_YORK_LON = -74.005758909787;
        public static final Double WASHINGTON_LAT = 38.9072423927665;
        public static final Double WASHINGTON_LON = -77.03653810608216;
        public static final Double HOUSTON_LAT = 29.75974563585816;
        public static final Double HOUSTON_LON = -95.36844546242673;

        public static final long LOCATION_GPS_TIMEOUT = 30 * 60 * 1000; // 30 minutes
        public static final long LOCATION_NETWORK_TIMEOUT = 1 * 60 * 60 * 1000; // 1 hour
        public static final long LOCATION_DISTANCE = 0; // 0 km
    }
    public static class Extras {
        public static final String EXTRA_LAT = "extra_lat";
        public static final String EXTRA_LON = "extra_lon";
    }
    public static class ForecastIO {
        public static final String API_KEY = "f1fd27e70564bd6765bf40b3497cbf4f";

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
        }
    }
    public static class SharedPref {
        public static final String SHARED_RAIN = "shared_rain";

        public static final String ADDRESS = "address";
        public static final String CURRENTLY = "currently";
        public static final String HISTORY = "history";
        public static final String CURRENTLY_ICON = "currently_icon";
        public static final String NEXT_FORECAST_ICON = "next_forecast_icon";
        public static final String NEXT_ALARM_TIME = "next_alarm_time";
        public static final String LAST_LOCATION_LAT = "last_location_lat";
        public static final String LAST_LOCATION_LON = "last_location_lon";
    }
}
