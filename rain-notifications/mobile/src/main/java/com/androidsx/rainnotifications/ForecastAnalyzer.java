package com.androidsx.rainnotifications;

import com.forecast.io.v2.network.services.ForecastService.Response;
import com.forecast.io.v2.transfer.DataPoint;

import com.androidsx.rainnotifications.Utils.AnalyzerHelper;
import com.androidsx.rainnotifications.Utils.Constants.ForecastIO.Icon;

public class ForecastAnalyzer {

    private static final String TAG = ForecastAnalyzer.class.getSimpleName();

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
                return analyzer.highProbabilityRain();
            } else {
                return dpHelp;
            }
        } else if (currentlyIcon.equals(Icon.CLEAR_DAY) || currentlyIcon.equals(Icon.CLEAR_NIGHT)) {
            return analyzer.nextClearChange();
        } else if (currentlyIcon.equals(Icon.PARTLY_CLOUDY_DAY) || currentlyIcon.equals(Icon.PARTLY_CLOUDY_NIGHT)) {
            return analyzer.nextPartlyCloudyChange();
        } else if (currentlyIcon.equals(Icon.CLOUDY)) {
            return analyzer.nextCloudyChange();
        }

        return null;
    }
}
