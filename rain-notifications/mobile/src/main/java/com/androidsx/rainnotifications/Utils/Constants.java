package com.androidsx.rainnotifications.Utils;

public class Constants {
    public static class Time{
        public static final String TIME_ZONE_MADRID = "Europe/Madrid";
        public static final String TIME_ZONE_NEW_YORK = "America/New_York";
        public static final String TIME_FORMAT = "HH:mm";

        public static final int TWO_MINUTES = 1000 * 60 * 2;
        public static final int TEN_MINUTES_AGO = 1000 * 60 * 10;
        public static final int HALF_HOUR_AGO = 1000 * 60 * 30;
        public static final int HOUR_AGO = 1000 * 60 * 60 * 1;
        public static final int TWO_HOURS_AGO = 1000 * 60 * 60 * 2;
    }
    public static class Distance{
        public static final int KM = 1000;
    }
    public static class Localization {
        public static final Double NEW_YORK_LAT = 40.72228267283148;
        public static final Double NEW_YORK_LON = -73.9434814453125;

        public static final long LOCATION_GPS_TIMEOUT = 1 * 30 * 1000;
        public static final long LOCATION_NETWORK_TIMEOUT = 2 * 60 * 1000;
        public static final long LOCATION_DISTANCE = 0;
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
}
