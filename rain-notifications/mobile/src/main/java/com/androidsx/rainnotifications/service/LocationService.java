package com.androidsx.rainnotifications.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.androidsx.rainnotifications.util.SchedulerHelper;
import com.androidsx.rainnotifications.model.LocationObservable;
import com.androidsx.rainnotifications.util.AddressHelper;
import com.androidsx.rainnotifications.util.Constants;
import com.androidsx.rainnotifications.util.SharedPrefsHelper;

import java.util.Observable;
import java.util.Observer;

/*
 * Este servicio es el encargado de obtener la posición gps del usuario.
 *
 * Se inicia por primera vez al ser llamada por la activity (botón Call the Forecast API).
 * (En su defecto, también está registrado en el OnBootReceiver, para inicar el proceso una vez se inicia el sistema).
 *
 * Una vez iniciado, calcula la posición actual, e inicia el servicio WeatherService con dicha posición y se
 * registra una alarma para que vuelva a llamar a LocationService (actualmente 1 hora) con las coordenadas de
 * la posición obtenida.
 *
 * Cuando la alarma llama de nuevo de LocationService, este recibe las última coordenadas por extras, y las compara
 * con la nueva posición obtenida, para determinar si se va a llamar a WeatherService con las nuevas coordenadas;
 * debido a que si la posición ha sufrido un cambio considerable, la respuesta de WeatherService podría variar.
 * (Actualmente 5 km de distancia entre posiciones)
 */

public class LocationService extends Service implements Observer {

    private static final String TAG = LocationService.class.getSimpleName();

    private LocationObservable locationObservable;
    private Location lastLocation;
    private AlarmManager alarmMgr;

    private int alarmID = 1;
    private SharedPrefsHelper shared;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        locationObservable = new LocationObservable(getApplicationContext());
        locationObservable.addObserver(this);

        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        shared = new SharedPrefsHelper(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            Bundle mBundle = intent.getExtras();
            if(mBundle != null) {
                double latitude = mBundle.getDouble(Constants.Extras.EXTRA_LAT);
                double longitude = mBundle.getDouble(Constants.Extras.EXTRA_LON);

                lastLocation = new Location(LocationManager.NETWORK_PROVIDER);
                lastLocation.setLatitude(latitude);
                lastLocation.setLongitude(longitude);
            }
        }
        locationObservable.getLastLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void update(Observable observable, Object o) {
        if(observable.getClass().equals(LocationObservable.class)) {
            Location location = (Location) o;

            // Solo para la primera llamada, para iniciar el proceso de alarmas.
            if(lastLocation == null) {
                callWeatherService(location);
            } else {
                if (location.distanceTo(lastLocation) > 5) {
                    callWeatherService(location);
                }
            }
        }
        stopSelf();
    }

    private void callWeatherService(Location location) {
        Intent mIntent = new Intent(this, LocationService.class);
        Bundle mBundle = new Bundle();
        mBundle.putDouble(Constants.Extras.EXTRA_LAT, location.getLatitude());
        mBundle.putDouble(Constants.Extras.EXTRA_LON, location.getLongitude());
        mIntent.putExtras(mBundle);

        PendingIntent alarmIntent = PendingIntent.getService(getApplicationContext(), alarmID, mIntent, 0);
        if (alarmMgr != null) {
            SchedulerHelper.setNextLocationAlarm(alarmMgr, alarmIntent, Constants.Time.HOUR_MILLIS);
        }

        startService(new Intent(this, WeatherService.class).putExtras(mBundle));

        String address = AddressHelper.getLocationAddress(this,
                location.getLatitude(), location.getLongitude());

        shared.setForecastAddress(address);

        // Only for debug
        float distance = (float) 0.0;
        if(lastLocation != null) {
            distance = lastLocation.distanceTo(location);
        }

        lastLocation = location;

        Log.d(TAG, "Location Observer update...\nLocation: " + address +
                " --> lat: " + location.getLatitude() +
                " - long: " + location.getLongitude() +
                "\nDistance: " + distance);
    }
}
