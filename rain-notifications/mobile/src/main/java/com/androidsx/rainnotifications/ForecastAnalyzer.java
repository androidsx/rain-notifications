package com.androidsx.rainnotifications;

import android.util.Log;

import com.androidsx.rainnotifications.Utils.AnalyzerHelper;
import com.androidsx.rainnotifications.Utils.Constants.Time;
import com.androidsx.rainnotifications.Utils.Constants.ForecastIO.Icon;

import com.androidsx.rainnotifications.Utils.DateHelper;
import com.forecast.io.v2.network.services.ForecastService.Response;
import com.forecast.io.v2.transfer.DataPoint;

import java.util.Locale;

public class ForecastAnalyzer {

    private static final String TAG = ForecastAnalyzer.class.getSimpleName();
    private static final long HOUR = Time.HOUR_AGO / 1000;

    private AnalyzerHelper analyzer;
    private long currentTime = System.currentTimeMillis() / 1000;
    private Response response;

    public void setResponse(Response res) {
        this.analyzer = new AnalyzerHelper(res);
        this.response = res;
    }

    public DataPoint analyzeForecastForRain(String currentlyIcon) {
        //showMinutely();
        if(AnalyzerHelper.compareTo(currentlyIcon, Icon.RAIN)) {
            return setNextApiCallTime(analyzer.nextRainChange());
        } else if (AnalyzerHelper.compareTo(currentlyIcon, Icon.CLEAR_DAY)) {
            return setNextApiCallTime(analyzer.nextClearChange());
        } else if (AnalyzerHelper.compareTo(currentlyIcon, Icon.PARTLY_CLOUDY_DAY)) {
            return setNextApiCallTime(analyzer.nextPartlyCloudyChange());
        } else if (AnalyzerHelper.compareTo(currentlyIcon, Icon.CLOUDY)) {
            return setNextApiCallTime(analyzer.nextCloudyChange());
        }

        return null;
    }

    private DataPoint setNextApiCallTime(DataPoint dp) {
        if(dp != null) {
            long time = (dp.getTime() - currentTime) * 70 / 100;
            //Log.d(TAG, "Schedule Time in: " + time / 60 + " min.");
            if(time < (Time.TEN_MINUTES_AGO / 1000) && time > (Time.TWO_MINUTES / 1000 / 2)) {
                if(time > (Time.TWO_MINUTES / 1000 * 2)) {
                    ForecastMobile.setNextApiCallTime(currentTime + time);
                } else {
                    ForecastMobile.setNextApiCallTime(dp.getTime());
                }
            } else {
                ForecastMobile.setNextApiCallTime(currentTime + time);
            }
        } else {
            ForecastMobile.setNextApiCallTime(currentTime + (HOUR * 4));
        }

        return dp;
    }

    private void showMinutely() {
        if (response.getForecast().getMinutely() != null) {
            for (DataPoint dp : response.getForecast().getMinutely().getData()) {
                String time = new DateHelper().formatTime(dp.getTime(), Time.TIME_FORMAT, Time.TIME_ZONE_NEW_YORK, Locale.US);
                Log.d(TAG, time + " - Icon: " + dp.getIcon());
            }
        }
    }
}