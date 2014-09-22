package com.androidsx.rainnotifications;

public class Constants {
    public static class Alarms{
        public static final int WEATHER_ID = 1;
        public static final int DAY_ALARM_ID = 2;

        /** Hour of the day (0-23) to trigger the daily weather digest in the user timezone */
        public static final int HOUR_OF_THE_DAY_DIGEST_ALARM = 8;
    }
    public static class Extras{
        public static final String EXTRA_DAY_ALARM = "extra_day_alarm";
    }
    public static class Assets {
        public static final String ROBOTO_REGULAR_URL = "roboto/Roboto-Regular.ttf";
        public static final String ROBOTO_SLAB_REGULAR_URL = "roboto-slab/RobotoSlab-Regular.ttf";
    }
}
