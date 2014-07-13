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

    public DataPoint analyzeForecastFor(String weatherIcon) {
        DataPoint dpHelp;
        //TODO: adjust for obtain the correct expected value
        if(weatherIcon.equals(Icon.RAIN)) {
            dpHelp = analyzer.nextRain();
            if(dpHelp == null) {
                return analyzer.highProbabilityRain();
            } else {
                return dpHelp;
            }
        } else if (weatherIcon.equals(Icon.CLEAR_DAY) || weatherIcon.equals(Icon.CLEAR_NIGHT)) {
            return analyzer.nextClear();
        } else if (weatherIcon.equals(Icon.PARTLY_CLOUDY_DAY) || weatherIcon.equals(Icon.PARTLY_CLOUDY_NIGHT)) {
            return analyzer.nextPartlyCloudy();
        } else if (weatherIcon.equals(Icon.CLOUDY)) {
            return analyzer.nextCloudy();
        }

        return null;
    }
}
