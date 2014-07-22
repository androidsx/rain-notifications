package com.androidsx.rainnotifications.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.androidsx.rainnotifications.ForecastAnalyzer;
import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.AlertLevel;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.util.DateHelper;
import com.androidsx.rainnotifications.util.DebugHelper;
import com.androidsx.rainnotifications.util.SchedulerHelper;
import com.androidsx.rainnotifications.util.AddressHelper;
import com.androidsx.rainnotifications.Constants;
import com.androidsx.rainnotifications.util.SharedPrefsHelper;

import com.forecast.io.network.responses.INetworkResponse;
import com.forecast.io.v2.network.services.ForecastService;
import com.forecast.io.v2.transfer.DataPoint;
import com.forecast.io.v2.transfer.LatLng;

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

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private int weatherAlarmID = 0;

    public SharedPreferences sharedPrefs;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        sharedPrefs = getSharedPreferences(Constants.SharedPref.SHARED_RAIN, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            Bundle mBundle = intent.getExtras();
            if(mBundle != null) {
                double latitude = mBundle.getDouble(Constants.Extras.EXTRA_LAT, 1000);
                double longitude = mBundle.getDouble(Constants.Extras.EXTRA_LON, 1000);

                // Para comprobar que se han recibido coordenadas.
                if (latitude != 1000 && longitude != 1000) {
                    new ForecastIoNetworkServiceTask() {

                        // TODO: REMOVE this method and make the parent final
                        @Override
                        protected void onPostExecute(INetworkResponse rawNetworkResponse) {
                            super.onPostExecute(rawNetworkResponse);

                            updateWeather((ForecastService.Response) rawNetworkResponse);
                        }

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
                        }

                        @Override
                        protected void onFailure() {
                            // TODO: And here is where we do something smart about failures
                        }
                    }.execute(getRequest(latitude, longitude));
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private ForecastService.Request getRequest(Double latitude, Double longitude) {
        LatLng.Builder builderL = LatLng.newBuilder();
        builderL.setLatitude(latitude)
                .setLongitude(longitude)
                .build();
        LatLng latlng = new LatLng(builderL);
        ForecastService.Builder builderF = ForecastService.Request.newBuilder(Constants.ForecastIO.API_KEY);
        builderF.setLatLng(latlng).build();

        return new ForecastService.Request(builderF);
    }

    private void updateWeather(ForecastService.Response response) {
        DataPoint currently = response.getForecast().getCurrently();

        String address = AddressHelper.getLocationAddress(this,
                response.getForecast().getLatitude(), response.getForecast().getLongitude());

        SharedPrefsHelper.setForecastAddress(address, sharedPrefs.edit());

        //Icon that the user wants to determine when going to occur, maybe received in a Extra
        String searchingIcon = Constants.ForecastIO.Icon.RAIN;

        ForecastAnalyzer fa = new ForecastAnalyzer();
        fa.setResponse(response);
        DataPoint dpRain = fa.analyzeForecastFor(searchingIcon, currently.getIcon());

        Intent mIntent = new Intent(this, WeatherService.class);
        Bundle mBundle = new Bundle();
        mBundle.putDouble(Constants.Extras.EXTRA_LAT, response.getForecast().getLatitude());
        mBundle.putDouble(Constants.Extras.EXTRA_LON, response.getForecast().getLongitude());
        mIntent.putExtras(mBundle);
        alarmIntent = PendingIntent.getService(getApplicationContext(), weatherAlarmID, mIntent, 0);

        if(alarmMgr != null) {
            if(dpRain != null){
                SchedulerHelper.setNextWeatherCallAlarm(alarmMgr, alarmIntent, SchedulerHelper.nextWeatherCallAlarm(dpRain.getTime() * 1000));
                Log.d(TAG, ".\nNext weather call at calculated time: " + DateHelper.formatTimeMadrid(SchedulerHelper.nextWeatherCallAlarm(dpRain.getTime() * 1000)));
            } else {
                SchedulerHelper.setNextWeatherCallAlarm(alarmMgr, alarmIntent, SchedulerHelper.nextWeatherCallAlarm(0));
                Log.d(TAG, ".\nNext weather call by default 2 hours later: " + DateHelper.formatTimeMadrid(SchedulerHelper.nextWeatherCallAlarm(0)));
            }
        }

        Log.d(TAG, ".\nWeather Observer update...");

        if(dpRain != null && currently != null) {
            DebugHelper.displayDebugResults(this, sharedPrefs, dpRain.getTime() * 1000, dpRain.getIcon(), currently.getIcon(), searchingIcon);
        } else if(dpRain == null && currently != null) {
            DebugHelper.displayDebugResults(this, sharedPrefs, 0, "", currently.getIcon(), searchingIcon);
        }

        stopSelf();
    }
}
