package com.androidsx.rainnotifications;

import com.androidsx.rainnotifications.Utils.AnalyzerHelper;
import com.androidsx.rainnotifications.Utils.Constants.Time;
import com.androidsx.rainnotifications.Utils.Constants.ForecastIO.Icon;

import com.forecast.io.v2.network.services.ForecastService.Response;
import com.forecast.io.v2.transfer.DataPoint;

public class ForecastAnalyzer {

    private static final String TAG = ForecastAnalyzer.class.getSimpleName();
    private static final long HOUR = Time.HOUR_AGO / 1000;

    private AnalyzerHelper analyzer;
    private long currentTime = System.currentTimeMillis() / 1000;

    public void setResponse(Response res) {
        this.analyzer = new AnalyzerHelper(res);
    }

    public DataPoint analyzeForecastForRain(String currentlyIcon) {
        DataPoint dpHelp;
        //TODO: adjust for obtain the correct expected value
        if(currentlyIcon.equals(Icon.RAIN)) {
            return setNextApiCallTime(analyzer.nextRainChange());
        } else if (AnalyzerHelper.compareTo(currentlyIcon, Icon.CLEAR_DAY)) {
            return setNextApiCallTime(analyzer.highProbabilityRain(analyzer.nextClearChange()));
        } else if (AnalyzerHelper.compareTo(currentlyIcon, Icon.PARTLY_CLOUDY_DAY)) {
            return setNextApiCallTime(analyzer.highProbabilityRain(analyzer.nextPartlyCloudyChange()));
        } else if (AnalyzerHelper.compareTo(currentlyIcon, Icon.CLOUDY)) {
            return setNextApiCallTime(analyzer.highProbabilityRain(analyzer.nextCloudyChange()));
        }

        return null;
    }

    private DataPoint setNextApiCallTime(DataPoint dp) {
        if(dp != null) {
            if(dp.getTime() - currentTime > HOUR * 4) {
                ForecastMobile.setNextApiCallTime(currentTime + (HOUR * 4));
            }else if (dp.getTime() - currentTime > HOUR * 2) {
                ForecastMobile.setNextApiCallTime(dp.getTime() - HOUR);
            } else if(dp.getTime() - currentTime > HOUR / 2) {
                ForecastMobile.setNextApiCallTime(dp.getTime() - (HOUR / 2));
            } else {
                ForecastMobile.setNextApiCallTime(dp.getTime());
            }
        } else {
            if(dp.getTime() - currentTime + (HOUR * 4) > (HOUR / 2)) {
                ForecastMobile.setNextApiCallTime(currentTime + (HOUR * 4));
            } else {
                ForecastMobile.setNextApiCallTime(currentTime + (HOUR * 3));
            }
        }

        return dp;
    }
}
