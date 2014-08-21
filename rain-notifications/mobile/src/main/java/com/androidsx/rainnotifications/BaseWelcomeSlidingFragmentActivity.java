package com.androidsx.rainnotifications;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.androidsx.rainnotifications.ui.welcome.WelcomeActivity;
import com.androidsx.rainnotifications.util.ApplicationVersionHelper;

/**
 * Base activity that shows the welcome screens if necessary.
 */
abstract class BaseWelcomeSlidingFragmentActivity extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (ApplicationVersionHelper.getNumUses(this) == 0) {
            startActivity(new Intent(this, WelcomeActivity.class));
		}

		setContentView(getLayoutResId());
	}
	
    protected int getLayoutResId() {
        return R.layout.activity_forecast_mobile;
    }
}
