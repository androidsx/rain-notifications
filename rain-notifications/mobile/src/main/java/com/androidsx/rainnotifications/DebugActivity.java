package com.androidsx.rainnotifications;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.androidsx.rainnotifications.service.WeatherService;
import com.androidsx.rainnotifications.ui.main.MainMobileActivity;
import com.androidsx.rainnotifications.util.NotificationHelper;
import com.google.android.gms.wearable.NodeApi;

import timber.log.Timber;

/**
 * Class used only for debug purpose. Launch fake notifications, and load the forecast info response
 * from shared preferences
 */
public class DebugActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.debug_layout);
    }

    public void startWeatherService(View view) {
        startService(new Intent(this, WeatherService.class));
    }

    public void showWearOnlyNotification(View view) {
        Timber.d("Show a random notification");
        new WearNotificationManager(this) {
            @Override
            public void onWearManagerSuccess(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                if (getConnectedNodesResult.getNodes() != null) {
                    if (getConnectedNodesResult.getNodes().size() > 0) {
                        Toast.makeText(DebugActivity.this, "Check your wear!", Toast.LENGTH_LONG).show();
                        sendWearNotification(
                                DebugActivity.this,
                                getString(R.string.notif_long_text_fake),
                                R.drawable.owlie_debug
                        );
                    } else {
                        Toast.makeText(DebugActivity.this, "Wear is not connected (no nodes)", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(DebugActivity.this, "Wear is not connected (null nodes)", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onWearManagerFailure(WearNotificationManagerException exception) {
                Toast.makeText(DebugActivity.this, "Failed to connect to wear", Toast.LENGTH_LONG).show();
            }
        }.connect();
    }

    public void showStandardNotification(View v) {
        NotificationHelper.displayStandardNotification(
                DebugActivity.this,
                MainMobileActivity.class,
                getString(R.string.notif_long_text_fake),
                BitmapFactory.decodeResource(getResources(), R.drawable.owlie_debug));
    }
}
