package com.androidsx.rainnotifications;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.androidsx.rainnotifications.ui.welcome.WelcomeActivity;
import com.androidsx.rainnotifications.utils.ApplicationVersionHelper;

import com.androidsx.rainnotifications.R;

/**
 * Base activity that shows the welcome screens if necessary.
 */
abstract class BaseWelcomeSlidingFragmentActivity extends ActionBarActivity implements WelcomeTutorialAware {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (ApplicationVersionHelper.getNumUses(this) == 0) {
			WelcomeActivity.startWelcomeActivity(this,
			        "",
			        getWelcomeNumPages());
		}

		setContentView(getLayoutResId());
	}
	
    protected int getLayoutResId() {
        return R.layout.activity_forecast_mobile;
    }
}
