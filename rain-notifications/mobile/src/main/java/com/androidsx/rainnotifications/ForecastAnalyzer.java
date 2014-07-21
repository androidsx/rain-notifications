package com.androidsx.rainnotifications;

import com.androidsx.rainnotifications.util.AnalyzerHelper;

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

    private Response response;
    private long currentTime;

    public void setResponse(Response res) {
        this.response = res;
        this.currentTime = System.currentTimeMillis();
    }

    //TODO: make algorithms to determine the weather the user wants

    public DataPoint analyzeForecastFor(String searchingIcon, String currentlyIcon) {
        return AnalyzerHelper.getNextChange(currentlyIcon, searchingIcon, response, currentTime);
    }
}