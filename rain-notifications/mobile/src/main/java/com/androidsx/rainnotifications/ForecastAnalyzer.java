package com.androidsx.rainnotifications;

import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;

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

    private ForecastTable forecastTable;
    private long currentTime;

    public ForecastAnalyzer (ForecastTable fT) {
        this.forecastTable = fT;
        this.currentTime = System.currentTimeMillis();
    }

    //TODO: make algorithms to determine the weather the user wants
    public Forecast getNextForecastTransition() {
        return forecastTable.getForecasts().get(0);
    }
}