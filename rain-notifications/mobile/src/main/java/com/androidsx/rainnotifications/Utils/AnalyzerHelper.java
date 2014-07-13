package com.androidsx.rainnotifications.Utils;

import com.forecast.io.v2.network.services.ForecastService.Response;
import com.forecast.io.v2.transfer.DataBlock;
import com.forecast.io.v2.transfer.DataPoint;

import com.androidsx.rainnotifications.Utils.Constants.ForecastIO.Icon;

import java.util.List;

public class AnalyzerHelper {

    Response response;

    public AnalyzerHelper(Response res) {
        this.response = res;
    }

    //TODO: make methods for determine when weather going to occur

    public DataPoint nextRainTime() {
        return getNextIconTime(Icon.RAIN);
    }

    public DataPoint nextCloudyTime() {
        return getNextIconTime(Icon.CLOUDY);
    }

    public DataPoint nextPartlyCloudyTime() {
        return getNextIconTime(Icon.PARTLY_CLOUDY_DAY, Icon.PARTLY_CLOUDY_NIGHT);
    }

    public DataPoint nextClearTime() {
        return getNextIconTime(Icon.CLEAR_DAY, Icon.CLEAR_NIGHT);
    }

    public DataPoint nextSnowTime() {
        return getNextIconTime(Icon.SNOW);
    }

    public DataPoint nextSleetTime() {
        return getNextIconTime(Icon.SLEET);
    }

    public DataPoint nextWindTime() {
        return getNextIconTime(Icon.WIND);
    }

    public DataPoint nextFogTime() {
        return getNextIconTime(Icon.FOG);
    }

    public DataPoint nextHailTime() {
        return getNextIconTime(Icon.HAIL);
    }

    public DataPoint nextThunderStormTime() {
        return getNextIconTime(Icon.THUNDERSTORM);
    }

    public DataPoint nextTornadoTime() {
        return getNextIconTime(Icon.TORNADO);
    }

    private DataPoint getNextIconTime(String icon) {

        DataBlock hourly = response.getForecast().getHourly();
        DataBlock minutely = response.getForecast().getMinutely();
        String currentForecast = response.getForecast().getCurrently().getIcon();

        if(currentForecast.equals(icon)) {
            if(minutely != null) {
                for(DataPoint dpM : minutely.getData()) {
                    if(!dpM.getIcon().equals(icon) && !dpM.getIcon().equals("")) {
                        return dpM;
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (!dpH.getIcon().equals(icon) && !dpH.getIcon().equals("")) {
                        return dpH;
                    }
                }
            }
        } else {
            if (minutely != null) {
                for (DataPoint dpM : minutely.getData()) {
                    if (dpM.getIcon().equals(icon)  && !dpM.getIcon().equals("")) {
                        return dpM;
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (dpH.getIcon().equals(icon)  && !dpH.getIcon().equals("")) {
                        return dpH;
                    }
                }
            }
        }

        return null;
    }
    private DataPoint getNextIconTime(String icon1, String icon2) {
        DataBlock hourly = response.getForecast().getHourly();
        DataBlock minutely = response.getForecast().getMinutely();
        String currentForecast = response.getForecast().getCurrently().getIcon();

        if(currentForecast.equals(icon1) || currentForecast.equals(icon2)) {
            if(minutely != null) {
                for(DataPoint dpM : minutely.getData()) {
                    if(!dpM.getIcon().equals(icon1) || !dpM.getIcon().equals(icon2)
                            && !dpM.getIcon().equals("")) {
                        return dpM;
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (!dpH.getIcon().equals(icon1) || !dpH.getIcon().equals(icon2)
                            && !dpH.getIcon().equals("")) {
                        return dpH;
                    }
                }
            }
        } else {
            if (minutely != null) {
                for (DataPoint dpM : minutely.getData()) {
                    if (dpM.getIcon().equals(icon1) || dpM.getIcon().equals(icon2)
                            && !dpM.getIcon().equals("")) {
                        return dpM;
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (dpH.getIcon().equals(icon1) || dpH.getIcon().equals(icon2)
                            && !dpH.getIcon().equals("")) {
                        return dpH;
                    }
                }
            }
        }

        return null;
    }
}
