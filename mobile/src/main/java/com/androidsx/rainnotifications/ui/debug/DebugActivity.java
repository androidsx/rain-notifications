package com.androidsx.rainnotifications.ui.debug;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

/**
 * Class used only for debug purpose. Launch fake notifications, and load the forecast info response
 * from shared preferences
 */
public class DebugActivity extends Activity {
    private AlertGenerator alertGenerator;

    private WeatherType weatherTypeNow;
    private DateTime timeNow;
    private DateTime timeLater;

    private ListView transitionsListView;
    private List<WeatherItemRow> weatherTransitionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.debug_layout);
        alertGenerator = new AlertGenerator(getResources());

        final Button now_time_button = (Button) findViewById(R.id.now_time_button);
        final Spinner nowSpinner = (Spinner) findViewById(R.id.weather_now_spinner);
        transitionsListView = (ListView) findViewById(R.id.rows_weather_list);

        configureNowTime(now_time_button);
        configureNowWeatherSpinner(nowSpinner);
        configureWeatherTransitionsList(transitionsListView);
    }

    public void addNewRow(View view) {
        weatherTransitionsList.add(new WeatherItemRow(0, new DateTime(System.currentTimeMillis())));
        WeatherListAdapter adapter = (WeatherListAdapter)transitionsListView.getAdapter();
        adapter.notifyDataSetChanged();
    }

    private void configureWeatherTransitionsList(ListView list) {
        weatherTransitionsList = new ArrayList<WeatherItemRow>();
        WeatherListAdapter adapter = new WeatherListAdapter(this, R.layout.debug_item_list, weatherTransitionsList);
        list.setAdapter(adapter);
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

    private void configureLaterTime(final Button laterButton, final WeatherItemRow weatherItemRow) {
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
                        weatherItemRow.setTime(timeLater);
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

    private void configureLaterWeatherSpinner(Spinner laterSpinner, final WeatherItemRow weatherItemRow) {
        final List<String> weatherTypeNames = new ArrayList<String>();
        for (WeatherType weatherType : WeatherType.values()) {
            weatherTypeNames.add(weatherType.toString());
        }
        laterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                weatherItemRow.setWeatherType(position);
                Log.i(DebugActivity.class.getSimpleName(), "Selection saved: " + weatherItemRow.getWeatherType());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.debug_spinner_row, weatherTypeNames);
        laterSpinner.setAdapter(adapter);
    }

    private String timeToString(DateTime dateTime) {
        return timeToString(dateTime.getHourOfDay(), dateTime.getMinuteOfHour());
    }

    private String timeToString(int hourOfDay, int minuteOfHour) {
        final DecimalFormat df = new DecimalFormat("##");
        return df.format(hourOfDay) + ":" + df.format(minuteOfHour);
    }

    public void generateAlert(View view) {
        /*final Interval intervalUntilWeatherChange = new Interval(timeNow, timeLater);
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
    */}

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

    private class WeatherItemRow {
        private int weatherType;
        private DateTime dateTime;

        public WeatherItemRow(int weatherType, DateTime dateTime) {
            this.weatherType = weatherType;
            this.dateTime = dateTime;
        }

        public int getWeatherType() {
            return weatherType;
        }

        public void setWeatherType(int weatherType) {
            this.weatherType = weatherType;
        }

        public DateTime getTime() {
            return dateTime;
        }

        public void setTime(DateTime dateTime) {
            this.dateTime = dateTime;
        }
    }

    private class WeatherListAdapter extends ArrayAdapter<WeatherItemRow> {

        Context context;
        int layoutResId;
        List<WeatherItemRow> weatherItemRows;

        public WeatherListAdapter(Context context, int layoutResId, List<WeatherItemRow> weatherItemRows) {
            super(context, layoutResId, weatherItemRows);
            this.context = context;
            this.layoutResId = layoutResId;
            this.weatherItemRows = weatherItemRows;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.debug_item_list, parent, false);
            Button delete = (Button) rowView.findViewById(R.id.delete_item_button);
            Button button = (Button) rowView.findViewById(R.id.later_time_button);
            Spinner spinner = (Spinner) rowView.findViewById(R.id.weather_later_spinner);

            final WeatherItemRow item = getItem(position);

            configureLaterTime(button, item);
            configureLaterWeatherSpinner(spinner, item);
            button.setText(timeToString(item.getTime()));
            spinner.setSelection(item.getWeatherType());

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    remove(item);
                }
            });

            return rowView;
        }
    }
}
