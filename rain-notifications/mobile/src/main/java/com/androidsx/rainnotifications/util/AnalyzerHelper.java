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

    public static DataPoint getNextChange(String currentlyForecastIcon, String icon, Response response, long currentTime) {
        if(compareIconToIcon(currentlyForecastIcon, icon)) {
            if(response.getForecast().getMinutely() != null) {
                for(DataPoint dpM : response.getForecast().getMinutely().getData()) {
                    if(!compareIconToIcon(dpM.getIcon(), icon) && !dpM.getIcon().equals("") && dpM.getTime() * 1000 > currentTime) {
                        return dpM;
                    }
                }
            }
            if(response.getForecast().getHourly() != null) {
                for (DataPoint dpH : response.getForecast().getHourly().getData()) {
                    if (!compareIconToIcon(dpH.getIcon(), icon) && !dpH.getIcon().equals("") && dpH.getTime() * 1000 > currentTime) {
                        return dpH;
                    }
                }
            }
        } else {
            if (response.getForecast().getMinutely() != null) {
                for (DataPoint dpM : response.getForecast().getMinutely().getData()) {
                    if (compareIconToIcon(dpM.getIcon(), icon)  && !dpM.getIcon().equals("") && dpM.getTime() * 1000 > currentTime) {
                        return dpM;
                    }
                }
            }
            if(response.getForecast().getHourly() != null) {
                for (DataPoint dpH : response.getForecast().getHourly().getData()) {
                    if (compareIconToIcon(dpH.getIcon(), icon)  && !dpH.getIcon().equals("") && dpH.getTime() * 1000 > currentTime) {
                        return dpH;
                    }
                }
            }
        }

        return null;
    }

    public static boolean compareIconToIcon(String icon1, String icon2) {
        if (icon2.equals(Icon.CLEAR_NIGHT) || icon2.equals(Icon.CLEAR_DAY)) {
            if (icon1.equals(Icon.CLEAR_NIGHT) || icon1.equals(Icon.CLEAR_DAY)) {
                return true;
            }
        } else if(icon2.equals(Icon.PARTLY_CLOUDY_NIGHT) || icon2.equals(Icon.PARTLY_CLOUDY_DAY)) {
            if (icon1.equals(Icon.PARTLY_CLOUDY_NIGHT) || icon1.equals(Icon.PARTLY_CLOUDY_DAY)) {
                return true;
            }
        } else if(icon1.equals(icon2)) {
            return true;
        }

        return false;
    }
}