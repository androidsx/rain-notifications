package com.androidsx.rainnotifications;

import com.androidsx.rainnotifications.model.WeatherType;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static class AlarmId{
        public static final int LOCATION_ID = 0;
        public static final int WEATHER_ID = 1;
    }
    public static class SharedPref {
        public static final String ADDRESS = "address";
        public static final String CURRENTLY = "currently";
        public static final String CURRENTLY_ICON = "currently_icon";
        public static final String NEXT_FORECAST_ICON = "next_forecast_icon";
    }
    public static class Assets {
        public static final String ROBOTO_REGULAR_URL = "roboto/Roboto-Regular.ttf";
        public static final String ROBOTO_SLAB_REGULAR_URL = "roboto-slab/RobotoSlab-Regular.ttf";
    }
}
