package com.androidsx.rainnotifications;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import com.androidsx.rainnotifications.service.WeatherService;
import com.androidsx.rainnotifications.ui.welcome.WelcomeActivity;
import com.androidsx.rainnotifications.util.ApplicationVersionHelper;

import timber.log.Timber;

/**
 * Base activity that shows the welcome screens if necessary.
 */
abstract class BaseWelcomeActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        if (ApplicationVersionHelper.getNumUses(this) == 0) {
            startActivity(new Intent(this, WelcomeActivity.class));

        } else {
            final PendingIntent ongoingAlarm = PendingIntent.getService(this,
                    Constants.AlarmId.WEATHER_ID,
                    new Intent(getApplicationContext(), WeatherService.class),
                    PendingIntent.FLAG_NO_CREATE);

            if (ongoingAlarm == null) {
                Timber.d("The alarm is not set. Let's start the weather service now");
                startService(new Intent(this, WeatherService.class));
            } else {
                Timber.d("The alarm is already set");
            }
        }
	}
}
