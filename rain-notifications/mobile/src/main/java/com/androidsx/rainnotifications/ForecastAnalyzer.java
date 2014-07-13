package com.androidsx.rainnotifications;

import com.forecast.io.v2.network.services.ForecastService.Response;

import com.androidsx.rainnotifications.Utils.AnalyzerHelper;
import com.androidsx.rainnotifications.Utils.Constants.ForecastIO.Icon;

public class ForecastAnalyzer {

    private static final String TAG = ForecastAnalyzer.class.getSimpleName();

    private AnalyzerHelper analyzer;

    public void setResponse(Response res) {
        this.analyzer = new AnalyzerHelper(res);
    }

    public long analyzeForecastFor(String weatherIcon) {

        long time = -1;

        if(weatherIcon.equals(Icon.RAIN)) {
            time = analyzer.nextRainTime();
        } else if (weatherIcon.equals(Icon.CLEAR_DAY) || weatherIcon.equals(Icon.CLEAR_NIGHT)) {
            time = analyzer.nextClearTime();
        } else if (weatherIcon.equals(Icon.PARTLY_CLOUDY_DAY) || weatherIcon.equals(Icon.PARTLY_CLOUDY_NIGHT)) {
            time = analyzer.nextPartlyCloudyTime();
        } else if (weatherIcon.equals(Icon.CLOUDY)) {
            time = analyzer.nextCloudyTime();
        }

        return time;
    }
}
