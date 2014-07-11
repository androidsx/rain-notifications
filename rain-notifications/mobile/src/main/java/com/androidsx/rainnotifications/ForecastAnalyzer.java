package com.androidsx.rainnotifications;

import android.util.Log;

import com.androidsx.rainnotifications.Utils.Constants.Time;
import com.androidsx.rainnotifications.Utils.Constants.ForecastIO;
import com.androidsx.rainnotifications.Utils.DateHelper;
import com.forecast.io.v2.network.services.ForecastService.Response;
import com.forecast.io.v2.transfer.DataBlock;
import com.forecast.io.v2.transfer.DataPoint;

import java.util.Locale;

public class ForecastAnalyzer {

    private static final String TAG = ForecastAnalyzer.class.getSimpleName();

    private long forecastChangeTime = -1;
    private String forecastChangeIcon = null;

    public ForecastAnalyzer(Response response, String forecastIcon) {
        forecastChangeIcon = forecastIcon;
        setNextForecastChangeTime(response, forecastIcon);
    }

    public long getNextForecastChangeTime() {
        return forecastChangeTime;
    }

    public String getNextForecastChangeIcon() {
        return forecastChangeIcon;
    }

    private void setNextForecastChangeTime(Response response, String forecast) {

        DataBlock hourly = response.getForecast().getHourly();
        DataBlock minutely = response.getForecast().getMinutely();
        String currentForecast = response.getForecast().getCurrently().getIcon();

        Log.d(TAG, "Currently Time: " + new DateHelper().getForecastTime(response.getForecast().getCurrently().getTime(), Time.TIME_FORMAT, Time.TIME_ZONE_MADRID, Locale.US) +
                " - Forecast: " + currentForecast);

        if(currentForecast.equals(forecast)) {
            if(minutely != null) {
                for(DataPoint dpM1 : minutely.getData()) {
                    Log.d(TAG, "Forecast minutely: " + new DateHelper().getForecastTime(dpM1.getTime(), Time.TIME_FORMAT, Time.TIME_ZONE_MADRID, Locale.US) +
                            " - Forecast: " + dpM1.getIcon());
                    if(!dpM1.getIcon().equals(forecast) && !dpM1.getIcon().equals("")) {
                        Log.d(TAG, "Forecast choice: " + new DateHelper().getForecastTime(dpM1.getTime(), Time.TIME_FORMAT, Time.TIME_ZONE_MADRID, Locale.US) +
                                " - Forecast: " + dpM1.getIcon());
                        forecastChangeIcon = dpM1.getIcon();
                        forecastChangeTime = dpM1.getTime();
                        return;
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH1 : hourly.getData()) {
                    if (!dpH1.getIcon().equals(forecast) && !dpH1.getIcon().equals("")) {
                        Log.d(TAG, "Forecast choice: " + new DateHelper().getForecastTime(dpH1.getTime(), Time.TIME_FORMAT, Time.TIME_ZONE_MADRID, Locale.US) +
                                " - Forecast: " + dpH1.getIcon());
                        forecastChangeIcon = dpH1.getIcon();
                        forecastChangeTime = dpH1.getTime();
                        return;
                    }
                }
            }
        } else {
            if (minutely != null) {
                for (DataPoint dpM2 : minutely.getData()) {
                    Log.d(TAG, "Forecast minutely: " + new DateHelper().getForecastTime(dpM2.getTime(), Time.TIME_FORMAT, Time.TIME_ZONE_MADRID, Locale.US) +
                            " - Forecast: " + dpM2.getIcon());
                    if (dpM2.getIcon().equals(forecast)  && !dpM2.getIcon().equals("")) {
                        Log.d(TAG, "Forecast choice: " + new DateHelper().getForecastTime(dpM2.getTime(), Time.TIME_FORMAT, Time.TIME_ZONE_MADRID, Locale.US) +
                                " - Forecast: " + dpM2.getIcon());
                        forecastChangeIcon = dpM2.getIcon();
                        forecastChangeTime = dpM2.getTime();
                        return;
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH2 : hourly.getData()) {
                    if (dpH2.getIcon().equals(forecast)  && !dpH2.getIcon().equals("")) {
                        Log.d(TAG, "Forecast choice: " + new DateHelper().getForecastTime(dpH2.getTime(), Time.TIME_FORMAT, Time.TIME_ZONE_MADRID, Locale.US) +
                                " - Forecast: " + dpH2.getIcon());
                        forecastChangeIcon = dpH2.getIcon();
                        forecastChangeTime = dpH2.getTime();
                        return;
                    }
                }
            }
        }
    }
}
