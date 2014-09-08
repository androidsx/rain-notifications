package com.androidsx.rainnotifications.ui.debug;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androidsx.rainnotifications.R;
import com.androidsx.rainnotifications.WearNotificationManager;
import com.androidsx.rainnotifications.WearNotificationManagerException;
import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.service.WeatherService;
import com.androidsx.rainnotifications.ui.main.MainMobileActivity;
import com.androidsx.rainnotifications.util.AlarmHelper;
import com.androidsx.rainnotifications.util.AnimationHelper;
import com.androidsx.rainnotifications.util.NotificationHelper;
import com.google.android.gms.wearable.NodeApi;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Minutes;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Class used only for debug purpose. Launch fake notifications, and load the forecast info response
 * from shared preferences
 */
public class DebugActivity extends Activity {
    private AlertGenerator alertGenerator;

    private WeatherType weatherTypeNow;
    private WeatherType weatherTypeLater;
    private DateTime timeNow;
    private DateTime timeLater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.debug_layout);
        alertGenerator = new AlertGenerator(getResources());

        final Button now_time_button = (Button) findViewById(R.id.now_time_button);
        final Spinner nowSpinner = (Spinner) findViewById(R.id.weather_now_spinner);

        configureNowTime(now_time_button);
        configureNowWeatherSpinner(nowSpinner);
    }

    public void addNewRow(View view) {

    }

    private void configureNowTime(final Button nowButton) {
        timeNow = DateTime.now();
        nowButton.setText(timeToString(timeNow));

        final TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view,
                                          int newHourOfDay,
                                          int newMinuteOfHour) {
                        timeLater = new DateTime(
                                timeNow.getYear(),
                                timeNow.getMonthOfYear(),
                                timeNow.getDayOfMonth(),
                                newHourOfDay,
                                newMinuteOfHour);
                        nowButton.setText(timeToString(newHourOfDay, newMinuteOfHour));
                    }
                }, timeNow.getHourOfDay(), timeNow.getMinuteOfHour(), false);

        nowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tpd.show();
            }
        });
    }

    private void configureLaterTime(final Button laterButton) {
        timeLater = new DateTime(timeNow).plus(Minutes.minutes(15));
        laterButton.setText(timeToString(timeLater));

        final TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view,
                                          int newHourOfDay,
                                          int newMinuteOfHour) {
                        timeLater = new DateTime(
                                timeNow.getYear(),
                                timeNow.getMonthOfYear(),
                                timeNow.getDayOfMonth(),
                                newHourOfDay,
                                newMinuteOfHour);
                        laterButton.setText(timeToString(newHourOfDay, newMinuteOfHour));
                    }
                }, timeNow.getHourOfDay(), timeNow.getMinuteOfHour(), false);

        laterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tpd.show();
            }
        });
    }

    private void configureNowWeatherSpinner(Spinner nowSpinner) {
        final List<String> weatherTypeNames = new ArrayList<String>();
        for (WeatherType weatherType : WeatherType.values()) {
            weatherTypeNames.add(weatherType.toString());
        }

        nowSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                weatherTypeNow = WeatherType.values()[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.debug_spinner_row, weatherTypeNames);
        nowSpinner.setAdapter(adapter);
    }

    private String timeToString(DateTime dateTime) {
        return timeToString(dateTime.getHourOfDay(), dateTime.getMinuteOfHour());
    }

    private String timeToString(int hourOfDay, int minuteOfHour) {
        final DecimalFormat df = new DecimalFormat("##");
        return df.format(hourOfDay) + ":" + df.format(minuteOfHour);
    }

    public void generateAlert(View view) {
        final Interval intervalUntilWeatherChange = new Interval(timeNow, timeLater);
        final Alert alert = alertGenerator.generateAlert(new Weather(weatherTypeNow), new Forecast(new Weather(weatherTypeLater), intervalUntilWeatherChange, Forecast.Granularity.MINUTE));

        findViewById(R.id.card_wrapper).setVisibility(View.VISIBLE);

        final TextView cardMessageTextView = (TextView) findViewById(R.id.card_message_text_view);
        cardMessageTextView.setText(alert.getAlertMessage().getNotificationMessage());
        AnimationHelper.applyCardAnimation(findViewById(R.id.card_layout));

        final ImageView mascotImageView = (ImageView) findViewById(R.id.mascot_image_view);
        mascotImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), alert.getDressedMascot()));
        AnimationHelper.applyMascotAnimation(mascotImageView);

        ((TextView) findViewById(R.id.alert_level_text_view)).setText("Alert level: " + alert.getAlertLevel());
        ((TextView) findViewById(R.id.next_alarm_text_view)).setText("Next alarm: " + AlarmHelper.nextWeatherCallAlarmTime(intervalUntilWeatherChange).toPeriod().getMinutes() + " minutes from now");
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
