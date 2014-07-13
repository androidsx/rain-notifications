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
                if(!compareTo(dpHelp.getIcon(), expectedIcon)) {
                    ForecastMobile.setNextApiCall(dpHelp.getTime() - HOUR);
                }
                return dpHelp;
            }
        } else if (currentlyIcon.equals(Icon.CLEAR_DAY) || currentlyIcon.equals(Icon.CLEAR_NIGHT)) {
            dpHelp = analyzer.nextClearChange();
            if(dpHelp == null) {
                return null;
            } else {
                if(!compareTo(dpHelp.getIcon(), expectedIcon)) {
                    ForecastMobile.setNextApiCall(dpHelp.getTime() - HOUR);
                }
                return dpHelp;
            }
        } else if (currentlyIcon.equals(Icon.PARTLY_CLOUDY_DAY) || currentlyIcon.equals(Icon.PARTLY_CLOUDY_NIGHT)) {
            dpHelp = analyzer.nextPartlyCloudyChange();
            if(dpHelp == null) {
                return null;
            } else {
                if(!compareTo(dpHelp.getIcon(), expectedIcon)) {
                    ForecastMobile.setNextApiCall(dpHelp.getTime() - HOUR);
                }
                return dpHelp;
            }
        } else if (currentlyIcon.equals(Icon.CLOUDY)) {
            dpHelp = analyzer.nextCloudyChange();
            if(dpHelp == null) {
                return null;
            } else {
                if(!compareTo(dpHelp.getIcon(), expectedIcon)) {
                    ForecastMobile.setNextApiCall(dpHelp.getTime() - HOUR);
                }
                return dpHelp;
            }
        }

        return null;
    }

    private boolean compareTo(String dpIcon, String expectedIcon) {
        if(dpIcon.equals(expectedIcon)) {
            return true;
        } else if (expectedIcon.equals(Icon.CLEAR_NIGHT) || expectedIcon.equals(Icon.CLEAR_DAY)) {
            if (dpIcon.equals(Icon.CLEAR_NIGHT) || dpIcon.equals(Icon.CLEAR_DAY)) {
                return true;
            }
        } else if(expectedIcon.equals(Icon.PARTLY_CLOUDY_NIGHT) || expectedIcon.equals(Icon.PARTLY_CLOUDY_DAY)) {
            if (dpIcon.equals(Icon.PARTLY_CLOUDY_NIGHT) || dpIcon.equals(Icon.PARTLY_CLOUDY_DAY)) {
                return true;
            }
        }

        return false;
    }
}
