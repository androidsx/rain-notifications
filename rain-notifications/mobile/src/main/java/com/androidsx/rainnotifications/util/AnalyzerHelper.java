package com.androidsx.rainnotifications.util;

import com.forecast.io.v2.network.services.ForecastService.Response;
import com.forecast.io.v2.transfer.DataBlock;
import com.forecast.io.v2.transfer.DataPoint;

import com.androidsx.rainnotifications.util.Constants.ForecastIO.Icon;

/*
 * Clase auxiliar de ForecastAnalyzer desde la que se realiza el análisis de la respuesta,
 * dependiendo de los requerimientos solicitados (aun por implementar).
 */

public class AnalyzerHelper {

    private static final String TAG = AnalyzerHelper.class.getSimpleName();

    DataBlock hourly;
    DataBlock minutely;
    String currentlyForecastIcon;
    long currentTime = System.currentTimeMillis() / 1000;

    public AnalyzerHelper(Response res) {
        this.hourly = res.getForecast().getHourly();
        this.minutely = res.getForecast().getMinutely();
        this.currentlyForecastIcon = res.getForecast().getCurrently().getIcon();
    }

    public DataPoint nextRainChange() {
        return getNextChange(Icon.RAIN);
    }

    public DataPoint nextCloudyChange() {
        return getNextChange(Icon.CLOUDY);
    }

    public DataPoint nextPartlyCloudyChange() {
        return getNextChange(Icon.PARTLY_CLOUDY_DAY);
    }

    public DataPoint nextClearChange() {
        return getNextChange(Icon.CLEAR_DAY);
    }

    public DataPoint nextSnowChange() {
        return getNextChange(Icon.SNOW);
    }

    public DataPoint nextSleetChange() {
        return getNextChange(Icon.SLEET);
    }

    public DataPoint nextWindChange() {
        return getNextChange(Icon.WIND);
    }

    public DataPoint nextFogChange() {
        return getNextChange(Icon.FOG);
    }

    public DataPoint nextHailChange() {
        return getNextChange(Icon.HAIL);
    }

    public DataPoint nextThunderStormChange() {
        return getNextChange(Icon.THUNDERSTORM);
    }

    public DataPoint nextTornadoChange() {
        return getNextChange(Icon.TORNADO);
    }

    private DataPoint getNextChange(String icon) {
        if(compareTo(currentlyForecastIcon, icon)) {
            if(minutely != null) {
                for(DataPoint dpM : minutely.getData()) {
                    if(!compareTo(dpM.getIcon(), icon) && !dpM.getIcon().equals("") && dpM.getTime() > currentTime) {
                        return dpM;
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (!compareTo(dpH.getIcon(), icon) && !dpH.getIcon().equals("") && dpH.getTime() > currentTime) {
                        return dpH;
                    }
                }
            }
        } else {
            if (minutely != null) {
                for (DataPoint dpM : minutely.getData()) {
                    if (compareTo(dpM.getIcon(), icon)  && !dpM.getIcon().equals("") && dpM.getTime() > currentTime) {
                        return dpM;
                    }
                }
            }
            if(hourly != null) {
                for (DataPoint dpH : hourly.getData()) {
                    if (compareTo(dpH.getIcon(), icon)  && !dpH.getIcon().equals("") && dpH.getTime() > currentTime) {
                        return dpH;
                    }
                }
            }
        }

        return null;
    }

    public static boolean compareTo(String icon1, String icon2) {
        if(icon1.equals(icon2)) {
            return true;
        } else if (icon2.equals(Icon.CLEAR_NIGHT) || icon2.equals(Icon.CLEAR_DAY)) {
            if (icon1.equals(Icon.CLEAR_NIGHT) || icon1.equals(Icon.CLEAR_DAY)) {
                return true;
            }
        } else if(icon2.equals(Icon.PARTLY_CLOUDY_NIGHT) || icon2.equals(Icon.PARTLY_CLOUDY_DAY)) {
            if (icon1.equals(Icon.PARTLY_CLOUDY_NIGHT) || icon1.equals(Icon.PARTLY_CLOUDY_DAY)) {
                return true;
            }
        }

        return false;
    }
}