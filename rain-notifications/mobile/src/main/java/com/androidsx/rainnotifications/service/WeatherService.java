package com.androidsx.rainnotifications.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.androidsx.rainnotifications.ForecastAnalyzer;
import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.forecast_io.ForecastIoRequest;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.AlertLevel;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.Constants;
import com.androidsx.rainnotifications.util.SchedulerHelper;

import org.joda.time.LocalTime;

/**
 * This service is responsible to make API calls to forecast.io
 * Once it starts, make an API call to forecast.io with the received coordinates into extras
 * from LocationService or from its alarm (if it's not the first call).
 *
 * The response is analyzed for determine the next alarm time, and if it's appropriate
 * notify to user the next significant weather change.
 *
 * So, if the location doesn't has a significant change, this alarm, will be responsible to call
 * this service again. Each time period will be recalculated with each API response.
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

                            Forecast nextForecast = new ForecastAnalyzer(forecastTable).getNextForecastTransition();
                            Log.i(TAG, "Next expected forecast: " + nextForecast);

                            Log.i(TAG, "We could generate the following alerts:");
                            final Weather currentWeather = forecastTable.getBaselineWeather();
                            for (Forecast forecast  : forecastTable.getForecasts()) {

                                final Alert alert = alertGenerator.generateAlert(currentWeather, forecast);
                                if (alert.getAlertLevel() == AlertLevel.INFO) {
                                    Log.i(TAG, "INFO alert: " + alert.getAlertMessage());
                                }
                            }
                            updateWeatherAlarm(nextForecast, latitude, longitude);
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

    private void updateWeatherAlarm(Forecast nextForecast, double latitude, double longitude) {
        SchedulerHelper.setAlarm(
                getApplicationContext(), Constants.AlarmId.WEATHER_ID, WeatherService.class,
                latitude, longitude,
                        nextForecast.getTimeFromNow().getEndMillis(),
                Constants.Time.TEN_MINUTES_MILLIS
        );
    }
}
