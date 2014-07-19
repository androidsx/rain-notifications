package com.androidsx.rainnotifications.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*
 * Este receiver es el encargado de inicializar el proceso registro de alarmas, realizando una
 * llamada al serivicio LocationService, una vez se ha iniciado el terminal del usuario (cuando se
 * reciba el 'android.intent.action.BOOT_COMPLETED' (registrado en el AndroidManifest.xml)
 */

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, LocationService.class);
        context.startService(i);
    }
}
