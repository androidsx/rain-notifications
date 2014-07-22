package com.androidsx.rainnotifications.util;

import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;

/*
 * Clase auxiliar de ForecastAnalyzer desde la que se realiza el análisis de la respuesta,
 * dependiendo de los requerimientos solicitados (aun por implementar).
 */

public class AnalyzerHelper {

    private static final String TAG = AnalyzerHelper.class.getSimpleName();

    public static Forecast getNextChange(ForecastTable fT) {
        // TODO: for the moment, we return the first forecast transition.
        return fT.getForecasts().get(0);
    }
}