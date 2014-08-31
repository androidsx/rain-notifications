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
        }
	}
}
