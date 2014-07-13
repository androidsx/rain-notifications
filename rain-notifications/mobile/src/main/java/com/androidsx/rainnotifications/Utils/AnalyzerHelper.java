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

    public long nextRainTime() {

        DataBlock hourly = response.getForecast().getHourly();
        DataBlock minutely = response.getForecast().getMinutely();
        String currentForecast = response.getForecast().getCurrently().getIcon();

        if(currentForecast.equals(Icon.RAIN)) {
            if(minutely != null) {
                for(DataPoint dpM : minutely.getData()) {
                    if(!dpM.getIcon().equals(Icon.RAIN) && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (!dpH.getIcon().equals(Icon.RAIN) && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        } else {
            if (minutely != null) {
                for (DataPoint dpM : minutely.getData()) {
                    if (dpM.getIcon().equals(Icon.RAIN)  && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (dpH.getIcon().equals(Icon.RAIN)  && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        }

        return -1;
    }

    public long nextCloudyTime() {

        DataBlock hourly = response.getForecast().getHourly();
        DataBlock minutely = response.getForecast().getMinutely();
        String currentForecast = response.getForecast().getCurrently().getIcon();

        if(currentForecast.equals(Icon.CLOUDY)) {
            if(minutely != null) {
                for(DataPoint dpM : minutely.getData()) {
                    if(!dpM.getIcon().equals(Icon.CLOUDY) && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (!dpH.getIcon().equals(Icon.CLOUDY) && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        } else {
            if (minutely != null) {
                for (DataPoint dpM : minutely.getData()) {
                    if (dpM.getIcon().equals(Icon.CLOUDY)  && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (dpH.getIcon().equals(Icon.CLOUDY)  && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        }

        return -1;
    }

    public long nextPartlyCloudyTime() {

        DataBlock hourly = response.getForecast().getHourly();
        DataBlock minutely = response.getForecast().getMinutely();
        String currentForecast = response.getForecast().getCurrently().getIcon();

        if(currentForecast.equals(Icon.PARTLY_CLOUDY_DAY) || currentForecast.equals(Icon.PARTLY_CLOUDY_NIGHT)) {
            if(minutely != null) {
                for(DataPoint dpM : minutely.getData()) {
                    if(!dpM.getIcon().equals(Icon.PARTLY_CLOUDY_DAY) || !dpM.getIcon().equals(Icon.PARTLY_CLOUDY_NIGHT)
                            && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (!dpH.getIcon().equals(Icon.PARTLY_CLOUDY_DAY) || !dpH.getIcon().equals(Icon.PARTLY_CLOUDY_NIGHT)
                            && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        } else {
            if (minutely != null) {
                for (DataPoint dpM : minutely.getData()) {
                    if (dpM.getIcon().equals(Icon.PARTLY_CLOUDY_DAY) || dpM.getIcon().equals(Icon.PARTLY_CLOUDY_NIGHT)
                            && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (dpH.getIcon().equals(Icon.PARTLY_CLOUDY_DAY) || dpH.getIcon().equals(Icon.PARTLY_CLOUDY_NIGHT)
                            && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        }

        return -1;
    }

    public long nextClearTime() {

        DataBlock hourly = response.getForecast().getHourly();
        DataBlock minutely = response.getForecast().getMinutely();
        String currentForecast = response.getForecast().getCurrently().getIcon();

        if(currentForecast.equals(Icon.CLEAR_DAY) || currentForecast.equals(Icon.CLEAR_NIGHT)) {
            if(minutely != null) {
                for(DataPoint dpM : minutely.getData()) {
                    if(!dpM.getIcon().equals(Icon.CLEAR_DAY) || !dpM.getIcon().equals(Icon.CLEAR_NIGHT)
                            && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (!dpH.getIcon().equals(Icon.CLEAR_DAY) || !dpH.getIcon().equals(Icon.CLEAR_NIGHT)
                            && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        } else {
            if (minutely != null) {
                for (DataPoint dpM : minutely.getData()) {
                    if (dpM.getIcon().equals(Icon.CLEAR_DAY) || dpM.getIcon().equals(Icon.CLEAR_NIGHT)
                            && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (dpH.getIcon().equals(Icon.CLEAR_DAY) || dpH.getIcon().equals(Icon.CLEAR_NIGHT)
                            && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        }

        return -1;
    }

    public long nextSnowTime() {

        DataBlock hourly = response.getForecast().getHourly();
        DataBlock minutely = response.getForecast().getMinutely();
        String currentForecast = response.getForecast().getCurrently().getIcon();

        if(currentForecast.equals(Icon.SNOW)) {
            if(minutely != null) {
                for(DataPoint dpM : minutely.getData()) {
                    if(!dpM.getIcon().equals(Icon.SNOW) && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (!dpH.getIcon().equals(Icon.SNOW) && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        } else {
            if (minutely != null) {
                for (DataPoint dpM : minutely.getData()) {
                    if (dpM.getIcon().equals(Icon.SNOW)  && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (dpH.getIcon().equals(Icon.SNOW)  && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        }

        return -1;
    }

    public long nextSleetTime() {

        DataBlock hourly = response.getForecast().getHourly();
        DataBlock minutely = response.getForecast().getMinutely();
        String currentForecast = response.getForecast().getCurrently().getIcon();

        if(currentForecast.equals(Icon.SLEET)) {
            if(minutely != null) {
                for(DataPoint dpM : minutely.getData()) {
                    if(!dpM.getIcon().equals(Icon.SLEET) && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (!dpH.getIcon().equals(Icon.SLEET) && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        } else {
            if (minutely != null) {
                for (DataPoint dpM : minutely.getData()) {
                    if (dpM.getIcon().equals(Icon.SLEET)  && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (dpH.getIcon().equals(Icon.SLEET)  && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        }

        return -1;
    }

    public long nextWindTime() {

        DataBlock hourly = response.getForecast().getHourly();
        DataBlock minutely = response.getForecast().getMinutely();
        String currentForecast = response.getForecast().getCurrently().getIcon();

        if(currentForecast.equals(Icon.WIND)) {
            if(minutely != null) {
                for(DataPoint dpM : minutely.getData()) {
                    if(!dpM.getIcon().equals(Icon.WIND) && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (!dpH.getIcon().equals(Icon.WIND) && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        } else {
            if (minutely != null) {
                for (DataPoint dpM : minutely.getData()) {
                    if (dpM.getIcon().equals(Icon.WIND)  && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (dpH.getIcon().equals(Icon.WIND)  && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        }

        return -1;
    }

    public long nextFogTime() {

        DataBlock hourly = response.getForecast().getHourly();
        DataBlock minutely = response.getForecast().getMinutely();
        String currentForecast = response.getForecast().getCurrently().getIcon();

        if(currentForecast.equals(Icon.FOG)) {
            if(minutely != null) {
                for(DataPoint dpM : minutely.getData()) {
                    if(!dpM.getIcon().equals(Icon.FOG) && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (!dpH.getIcon().equals(Icon.FOG) && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        } else {
            if (minutely != null) {
                for (DataPoint dpM : minutely.getData()) {
                    if (dpM.getIcon().equals(Icon.FOG)  && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (dpH.getIcon().equals(Icon.FOG)  && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        }

        return -1;
    }

    public long nextHailTime() {

        DataBlock hourly = response.getForecast().getHourly();
        DataBlock minutely = response.getForecast().getMinutely();
        String currentForecast = response.getForecast().getCurrently().getIcon();

        if(currentForecast.equals(Icon.HAIL)) {
            if(minutely != null) {
                for(DataPoint dpM : minutely.getData()) {
                    if(!dpM.getIcon().equals(Icon.HAIL) && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (!dpH.getIcon().equals(Icon.HAIL) && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        } else {
            if (minutely != null) {
                for (DataPoint dpM : minutely.getData()) {
                    if (dpM.getIcon().equals(Icon.HAIL)  && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (dpH.getIcon().equals(Icon.HAIL)  && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        }

        return -1;
    }

    public long nextThunderStormTime() {

        DataBlock hourly = response.getForecast().getHourly();
        DataBlock minutely = response.getForecast().getMinutely();
        String currentForecast = response.getForecast().getCurrently().getIcon();

        if(currentForecast.equals(Icon.THUNDERSTORM)) {
            if(minutely != null) {
                for(DataPoint dpM : minutely.getData()) {
                    if(!dpM.getIcon().equals(Icon.THUNDERSTORM) && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (!dpH.getIcon().equals(Icon.THUNDERSTORM) && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        } else {
            if (minutely != null) {
                for (DataPoint dpM : minutely.getData()) {
                    if (dpM.getIcon().equals(Icon.THUNDERSTORM)  && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (dpH.getIcon().equals(Icon.THUNDERSTORM)  && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        }

        return -1;
    }

    public long nextTornadoTime() {

        DataBlock hourly = response.getForecast().getHourly();
        DataBlock minutely = response.getForecast().getMinutely();
        String currentForecast = response.getForecast().getCurrently().getIcon();

        if(currentForecast.equals(Icon.TORNADO)) {
            if(minutely != null) {
                for(DataPoint dpM : minutely.getData()) {
                    if(!dpM.getIcon().equals(Icon.TORNADO) && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (!dpH.getIcon().equals(Icon.TORNADO) && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        } else {
            if (minutely != null) {
                for (DataPoint dpM : minutely.getData()) {
                    if (dpM.getIcon().equals(Icon.TORNADO)  && !dpM.getIcon().equals("")) {
                        return dpM.getTime();
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (dpH.getIcon().equals(Icon.TORNADO)  && !dpH.getIcon().equals("")) {
                        return dpH.getTime();
                    }
                }
            }
        }

        return -1;
    }
}
