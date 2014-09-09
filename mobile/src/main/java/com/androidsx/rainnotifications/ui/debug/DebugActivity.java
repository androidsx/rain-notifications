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
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.ForecastTableBuilder;
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

    private static final int DEFAULT_MINUTES_NEW_ROW = 15;
    private static final int DEFAULT_SPINNER_POSITION = 0;

    private AlertGenerator alertGenerator;

    private WeatherItemRow nowWeatherItemRow;

    private ListView transitionsListView;
    private List<WeatherItemRow> weatherTransitionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.debug_layout);
        alertGenerator = new AlertGenerator(getResources());

        final Button nowTimeButton = (Button) findViewById(R.id.now_time_button);
        final Spinner nowSpinner = (Spinner) findViewById(R.id.weather_now_spinner);
        transitionsListView = (ListView) findViewById(R.id.rows_weather_list);
        nowWeatherItemRow = new WeatherItemRow(0, new DateTime().now());

        configureTimeButton(nowTimeButton, nowWeatherItemRow);
        configureWeatherSpinner(nowSpinner, nowWeatherItemRow);
        configureWeatherTransitionsList(transitionsListView);

        nowTimeButton.setText(timeToString(nowWeatherItemRow.getTime()));
    }

    public void resetDay(View view) {
        weatherTransitionsList.clear();
        findViewById(R.id.card_wrapper).setVisibility(View.GONE);
    }

    public void addNewRow(View view) {
        DateTime newTime;
        if(weatherTransitionsList.isEmpty()) {
            newTime = nowWeatherItemRow.getTime().plus(Minutes.minutes(DEFAULT_MINUTES_NEW_ROW));
        } else {
            newTime = weatherTransitionsList.get(weatherTransitionsList.size() - 1).getTime().plus(Minutes.minutes(DEFAULT_MINUTES_NEW_ROW));
        }
        weatherTransitionsList.add(new WeatherItemRow(DEFAULT_SPINNER_POSITION, newTime));
        WeatherListAdapter adapter = (WeatherListAdapter)transitionsListView.getAdapter();
        adapter.notifyDataSetChanged();
    }

    private void configureWeatherTransitionsList(ListView list) {
        weatherTransitionsList = new ArrayList<WeatherItemRow>();
        WeatherListAdapter adapter = new WeatherListAdapter(this, R.layout.debug_item_list, weatherTransitionsList);
        list.setAdapter(adapter);
    }

    private void configureTimeButton(final Button timeButton, final WeatherItemRow weatherItemRow) {
        final TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    DateTime changedTime;
                    @Override
                    public void onTimeSet(TimePicker view,
                                          int newHourOfDay,
                                          int newMinuteOfHour) {
                        changedTime = new DateTime(
                                weatherItemRow.getTime().getYear(),
                                weatherItemRow.getTime().getMonthOfYear(),
                                weatherItemRow.getTime().getDayOfMonth(),
                                newHourOfDay,
                                newMinuteOfHour);
                        timeButton.setText(timeToString(newHourOfDay, newMinuteOfHour));
                        weatherItemRow.setTime(changedTime);
                    }
                }, weatherItemRow.getTime().getHourOfDay(), weatherItemRow.getTime().getMinuteOfHour(), false);

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tpd.show();
            }
        });
    }

    private void configureWeatherSpinner(Spinner laterSpinner, final WeatherItemRow weatherItemRow) {
        final List<String> weatherTypeNames = new ArrayList<String>();
        for (WeatherType weatherType : WeatherType.values()) {
            weatherTypeNames.add(weatherType.toString());
        }
        laterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                weatherItemRow.setWeatherTypeSpinnerPosition(position);
                weatherItemRow.setWeatherType(WeatherType.values()[position]);
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
        List<Forecast> mockForecasts = new ArrayList<Forecast>();
        for(WeatherItemRow w : weatherTransitionsList) {
            mockForecasts.add(new Forecast(new Weather(w.getWeatherType()), new Interval(new DateTime().now(), w.getTime()), Forecast.Granularity.MINUTE));
        }
        ForecastTable forecastTable = ForecastTable.create(new Weather(nowWeatherItemRow.getWeatherType()), nowWeatherItemRow.getTime(), mockForecasts);
        //Timber.d(forecastTable.toString());


        /*final Interval intervalUntilWeatherChange = new Interval(timeNow, timeLater);
        final Alert alert = alertGenerator.generateAlert(new Weather(weatherTypeNow), new Forecast(new Weather(weatherTypeLater), intervalUntilWeatherChange, Forecast.Granularity.MINUTE));
        */
        findViewById(R.id.card_wrapper).setVisibility(View.VISIBLE);

        final TextView cardMessageTextView = (TextView) findViewById(R.id.card_message_text_view);
        //cardMessageTextView.setText(alert.getAlertMessage().getNotificationMessage());
        cardMessageTextView.setText(forecastTable.toString());
        AnimationHelper.applyCardAnimation(findViewById(R.id.card_layout));

        final ImageView mascotImageView = (ImageView) findViewById(R.id.mascot_image_view);
        //mascotImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), alert.getDressedMascot()));
        mascotImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.owlie_sunny_02));
        AnimationHelper.applyMascotAnimation(mascotImageView);

        //((TextView) findViewById(R.id.alert_level_text_view)).setText("Alert level: " + alert.getAlertLevel());
        //((TextView) findViewById(R.id.next_alarm_text_view)).setText("Next alarm: " + AlarmHelper.nextWeatherCallAlarmTime(intervalUntilWeatherChange).toPeriod().getMinutes() + " minutes from now");
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

    private class WeatherItemRow {

        private WeatherType weatherType;
        private int weatherTypeSpinnerPosition;
        private DateTime dateTime;

        public WeatherItemRow(int weatherTypeSpinnerPosition, DateTime dateTime) {
            this.weatherTypeSpinnerPosition = weatherTypeSpinnerPosition;
            this.dateTime = dateTime;
            this.weatherType = WeatherType.values()[weatherTypeSpinnerPosition];
        }

        public WeatherType getWeatherType() {
            return weatherType;
        }

        public void setWeatherType(WeatherType weatherType) {
            this.weatherType = weatherType;
        }

        public int getWeatherTypeSpinnerPosition() {
            return weatherTypeSpinnerPosition;
        }

        public void setWeatherTypeSpinnerPosition(int weatherTypeSpinnerPosition) {
            this.weatherTypeSpinnerPosition = weatherTypeSpinnerPosition;
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
            View rowView = inflater.inflate(layoutResId, parent, false);
            Button delete = (Button) rowView.findViewById(R.id.delete_item_button);
            Button button = (Button) rowView.findViewById(R.id.later_time_button);
            Spinner spinner = (Spinner) rowView.findViewById(R.id.weather_later_spinner);

            final WeatherItemRow item = getItem(position);

            configureTimeButton(button, item);
            configureWeatherSpinner(spinner, item);
            button.setText(timeToString(item.getTime()));
            spinner.setSelection(item.getWeatherTypeSpinnerPosition());

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
