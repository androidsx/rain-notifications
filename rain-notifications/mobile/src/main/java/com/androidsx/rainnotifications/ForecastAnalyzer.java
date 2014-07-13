package com.androidsx.rainnotifications;

import android.util.Log;

import com.androidsx.rainnotifications.Utils.Constants.Time;
import com.androidsx.rainnotifications.Utils.DateHelper;
import com.forecast.io.v2.network.services.ForecastService.Response;
import com.forecast.io.v2.transfer.DataPoint;

import com.androidsx.rainnotifications.Utils.AnalyzerHelper;
import com.androidsx.rainnotifications.Utils.Constants.ForecastIO.Icon;

import java.util.Locale;

public class ForecastAnalyzer {

    private static final String TAG = ForecastAnalyzer.class.getSimpleName();
    private static final long HOUR = Time.HOUR_AGO / 1000;

    private AnalyzerHelper analyzer;

    public void setResponse(Response res) {
        this.analyzer = new AnalyzerHelper(res);
    }

    public DataPoint analyzeForecastFor(String currentlyIcon, String expectedIcon) {
        DataPoint dpHelp;
        //TODO: adjust for obtain the correct expected value
        if(currentlyIcon.equals(Icon.RAIN)) {
            dpHelp = analyzer.nextRainChange();
            if(dpHelp == null) {
                dpHelp = analyzer.highProbabilityRain();
                if(dpHelp != null) {
                    ForecastMobile.setNextApiCall(dpHelp.getTime() - HOUR);
                }
                return null;
            } else {
                if(!dpHelp.getIcon().equals(expectedIcon)) {
                    ForecastMobile.setNextApiCall(dpHelp.getTime() - HOUR);
                }
                return dpHelp;
            }
        } else if (currentlyIcon.equals(Icon.CLEAR_DAY) || currentlyIcon.equals(Icon.CLEAR_NIGHT)) {
            dpHelp = analyzer.nextClearChange();
            if(dpHelp == null) {
                return null;
            } else {
                if(!dpHelp.getIcon().equals(expectedIcon)) {
                    ForecastMobile.setNextApiCall(dpHelp.getTime() - HOUR);
                }
                return dpHelp;
            }
        } else if (currentlyIcon.equals(Icon.PARTLY_CLOUDY_DAY) || currentlyIcon.equals(Icon.PARTLY_CLOUDY_NIGHT)) {
            return analyzer.nextPartlyCloudyChange();
        } else if (currentlyIcon.equals(Icon.CLOUDY)) {
            return analyzer.nextCloudyChange();
        }

        return null;
    }
}
