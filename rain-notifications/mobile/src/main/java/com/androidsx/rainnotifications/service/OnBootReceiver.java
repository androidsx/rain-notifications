package com.androidsx.rainnotifications.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * This receiver is responsible to init LocationService on system boot, for start all the processes.
 */

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle mBundle = new Bundle();
        mBundle.putInt(ForecastService.EXTRA_ALARM_TYPE, ForecastService.LOCATION_ALARM_ID);
        context.startService(new Intent(context, ForecastService.class).putExtras(mBundle));
    }
}
