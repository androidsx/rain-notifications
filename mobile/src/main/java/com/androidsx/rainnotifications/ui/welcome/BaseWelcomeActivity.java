package com.androidsx.rainnotifications.ui.welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.androidsx.commonlibrary.util.ApplicationVersionHelper;

/**
 * Base activity that shows the welcome screens if necessary.
 */
abstract public class BaseWelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        if (ApplicationVersionHelper.getNumUses(this) == 1) {
            startActivity(new Intent(this, WelcomeActivity.class));
        }
	}
}
