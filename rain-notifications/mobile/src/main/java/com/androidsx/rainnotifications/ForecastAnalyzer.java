package com.androidsx.rainnotifications;

import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;

/**
 * This class, must be the main class for analyze the API response, and determine the next
 * significant weather change.
 */

public class ForecastAnalyzer {

    private static final String TAG = ForecastAnalyzer.class.getSimpleName();

    private ForecastTable forecastTable;
    private long currentTime;

    public ForecastAnalyzer (ForecastTable fT) {
        this.forecastTable = fT;
        this.currentTime = System.currentTimeMillis();
    }

    //TODO: make algorithms to determine the weather the user wants
    public Forecast getNextForecastTransition() {
        if(forecastTable != null && !forecastTable.getForecasts().isEmpty()) {
            return forecastTable.getForecasts().get(0);
        } else {
            return null;
        }
    }
}