package com.androidsx.rainnotifications;

import com.androidsx.rainnotifications.util.AnalyzerHelper;
import com.androidsx.rainnotifications.util.Constants.ForecastIO.Icon;

import com.forecast.io.v2.network.services.ForecastService.Response;
import com.forecast.io.v2.transfer.DataPoint;

/*
 * Esta clase, deberá ser la clase principal para realizar el análisis de la respuesta recibida
 * desde la API forecast.io; para así poder determinar el DataPoint de más interés encontrado.
 *
 * Actualmente, el DataPoint devuelto es el del siguiente cambio de tiempo, dependiendo del
 * estado actual.
 *
 * Futura implementación, definir diferentes funciones para obtener diferentes análisis.
 */

public class ForecastAnalyzer {

    private static final String TAG = ForecastAnalyzer.class.getSimpleName();

    private AnalyzerHelper analyzer;

    public void setResponse(Response res) {
        this.analyzer = new AnalyzerHelper(res);
    }

    public DataPoint analyzeForecastForRain(String currentlyIcon) {
        if(AnalyzerHelper.compareTo(currentlyIcon, Icon.RAIN)) {
            return analyzer.nextRainChange();
        } else if (AnalyzerHelper.compareTo(currentlyIcon, Icon.CLEAR_DAY)) {
            return analyzer.nextClearChange();
        } else if (AnalyzerHelper.compareTo(currentlyIcon, Icon.PARTLY_CLOUDY_DAY)) {
            return analyzer.nextPartlyCloudyChange();
        } else if (AnalyzerHelper.compareTo(currentlyIcon, Icon.CLOUDY)) {
            return analyzer.nextCloudyChange();
        }

        return null;
    }
}