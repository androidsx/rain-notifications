package com.androidsx.rainnotifications.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.forecast_io.ForecastIoRequest;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.AlertLevel;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.Constants;
import com.androidsx.rainnotifications.model.util.UiUtil;
import com.androidsx.rainnotifications.util.DateHelper;
import com.androidsx.rainnotifications.util.SchedulerHelper;

/*
 * Este servicio es el encargado de realizar las llamdas a forecast.io.
 * Cada vez que se inicia, realiza una llamada a la API de forecast.io con las coordenadas recibidas
 * por extras desde LocationService o desde la propia alarma iniciada después de la primera ejecución.
 *
 * Analizamos la respuesta con las diferentes clases auxiliares, y se determina la hora de la siguiente
 * ejecución, con la que se registrará una alarma, para volver a realizar una llamada a WeatherService.
 *
 * De esta manera, si la posición no sufriera ninguna modificación considerable, esta alarma, será la
 * encargada de ir llamando a WeatherService, cada periodo de tiempo que se irá recalculando, con cada
 * respuesta de la API forecast.io.
 */

public class WeatherService extends Service {

    private static final String TAG = WeatherService.class.getSimpleName();

    private final AlertGenerator alertGenerator = new AlertGenerator();

    public SharedPreferences sharedPrefs;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPrefs = getSharedPreferences(Constants.SharedPref.SHARED_RAIN, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            Bundle mBundle = intent.getExtras();
            if(mBundle != null) {
                final double latitude = mBundle.getDouble(Constants.Extras.EXTRA_LAT, 1000);
                final double longitude = mBundle.getDouble(Constants.Extras.EXTRA_LON, 1000);

                // Para comprobar que se han recibido coordenadas.
                if (latitude != 1000 && longitude != 1000) {
                    new ForecastIoNetworkServiceTask() {

                        @Override
                        protected void onSuccess(ForecastTable forecastTable) {
                            // TODO: Here is where we should apply our logic
                            Log.d(TAG, "Forecast table: " + forecastTable);

                            Log.i(TAG, "We could generate the following alerts:");
                            final Weather currentWeather = forecastTable.getBaselineWeather();
                            for (Forecast forecast  : forecastTable.getForecasts()) {

                                final Alert alert = alertGenerator.generateAlert(currentWeather, forecast);
                                if (alert.getAlertLevel() == AlertLevel.INFO) {
                                    Log.i(TAG, "INFO alert: " + alert.getAlertMessage());
                                }
                            }
                            SchedulerHelper.setAlarm(WeatherService.this, Constants.AlarmId.WEATHER_ID, latitude, longitude,
                                    SchedulerHelper.nextWeatherCallAlarm(
                                            forecastTable.getForecasts().get(0).getTimeFromNow().getEndMillis()), Constants.Time.TEN_MINUTES_MILLIS
                            );
                            Log.i(TAG, "Next weather alarm at: " + DateHelper.formatTimeMadrid(SchedulerHelper.nextWeatherCallAlarm(
                                    forecastTable.getForecasts().get(0).getTimeFromNow().getEndMillis())));
                            stopSelf();
                        }

                        @Override
                        protected void onFailure() {
                            // TODO: And here is where we do something smart about failures
                            stopSelf();
                        }
                    }.execute(new ForecastIoRequest(latitude, longitude).getRequest());
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
