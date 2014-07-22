package com.androidsx.rainnotifications.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.androidsx.rainnotifications.util.LocationHelper;
import com.androidsx.rainnotifications.util.SchedulerHelper;
import com.androidsx.rainnotifications.util.AddressHelper;
import com.androidsx.rainnotifications.Constants;
import com.androidsx.rainnotifications.util.SharedPrefsHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

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

public class LocationService extends Service implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = LocationService.class.getSimpleName();

    private Location lastLocation;
    private LocationClient mLocationClient;

    private SharedPreferences sharedPrefs;

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
                double latitude = mBundle.getDouble(Constants.Extras.EXTRA_LAT);
                double longitude = mBundle.getDouble(Constants.Extras.EXTRA_LON);

                lastLocation = new Location(LocationManager.NETWORK_PROVIDER);
                lastLocation.setLatitude(latitude);
                lastLocation.setLongitude(longitude);
            }
        }
        mLocationClient = new LocationClient(this, this, this);
        mLocationClient.connect();
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateLocation(Location loc) {
        String address = AddressHelper.getLocationAddress(this,
                loc.getLatitude(), loc.getLongitude());

        // Solo para la primera llamada, para iniciar el proceso de alarmas.
        if(lastLocation == null) {
            callWeatherService(loc);

            Log.d(TAG, ".\nLocation Observer update...\nLocation: " + address +
                    " --> lat: " + loc.getLatitude() +
                    " - long: " + loc.getLongitude());
        } else {
            if (loc.distanceTo(lastLocation) > 5) {
                callWeatherService(loc);

                // Only for debug
                float distance = (float) 0.0;
                if(lastLocation != null) {
                    distance = lastLocation.distanceTo(loc);
                }

                Log.d(TAG, ".\nLocation Observer update...\nLocation: " + address +
                        " --> lat: " + loc.getLatitude() +
                        " - long: " + loc.getLongitude() +
                        "\nDistance: " + distance);
            } else {
                Log.d(TAG, ".\nLocation Observer update...\nLocation: " + address +
                        " --> lat: " + loc.getLatitude() +
                        " - long: " + loc.getLongitude() +
                        "\nSame location");
            }

        }
        stopSelf();
    }

    private void callWeatherService(Location location) {
        SchedulerHelper.setAlarm(
                LocationService.this, Constants.AlarmId.LOCATION_ID, LocationService.class,
                location.getLatitude(), location.getLongitude(),
                System.currentTimeMillis() + Constants.Time.HOUR_MILLIS, Constants.Time.HOUR_MILLIS);

        Bundle mBundle = new Bundle();
        mBundle.putDouble(Constants.Extras.EXTRA_LAT, location.getLatitude());
        mBundle.putDouble(Constants.Extras.EXTRA_LON, location.getLongitude());

        startService(new Intent(this, WeatherService.class).putExtras(mBundle));

        String address = AddressHelper.getLocationAddress(this,
                location.getLatitude(), location.getLongitude());

        SharedPrefsHelper.setForecastAddress(address, sharedPrefs.edit());

        lastLocation = location;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(mLocationClient.isConnected()) {
            Location loc = mLocationClient.getLastLocation();
            if(LocationHelper.isBetterLocation(loc, lastLocation)) {
                updateLocation(loc);
            }
        }
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
