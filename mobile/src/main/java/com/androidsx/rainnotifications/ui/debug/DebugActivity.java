package com.androidsx.rainnotifications.ui.debug;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.alert.DayTemplateGenerator;
import com.androidsx.rainnotifications.alert.Setup;
import com.androidsx.rainnotifications.backgroundservice.WeatherService;
import com.androidsx.rainnotifications.backgroundservice.util.AlarmHelper;
import com.androidsx.rainnotifications.backgroundservice.util.NotificationHelper;
import com.androidsx.rainnotifications.backgroundservice.util.SharedPrefsHelper;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.AlertLevel;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.JsonDayTemplateLoader;
import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.model.WeatherWrapper;
import com.androidsx.rainnotifications.ui.main.MainMobileActivity;
import com.androidsx.rainnotifications.util.AnimationHelper;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Minutes;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import timber.log.Timber;

/**
 * Class used only for debug purpose. Launch fake notifications, and load the forecast info response
 * from shared preferences
 */
public class DebugActivity extends Activity {

    /** Additional minutes that we add on top of the previous row, for the new one. */
    private static final int DEFAULT_MINUTES_NEW_ROW = 60;
    private static final int DEFAULT_SPINNER_POSITION = 0;

    private AlertGenerator alertGenerator;

    private WeatherItemRow nowWeatherItemRow;
    private TextView nowTimeText;
    private Button nowTimeButton;
    private Spinner nowSpinner;

    private ListView transitionsListView;
    private List<WeatherItemRow> weatherTransitionsList;

    private View mockDaysView;
    private View addNewRowView;
    private View clearMockDayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.debug_layout);
        alertGenerator = new AlertGenerator(this);
        alertGenerator.init();

        final DateTime savedNextAlarmTime = new DateTime(SharedPrefsHelper.getLongValue(this, AlarmHelper.NEXT_ALARM_TIME));
        final DateTime savedDayAlarmTime = new DateTime(SharedPrefsHelper.getLongValue(this, AlarmHelper.DAY_ALARM_TIME));
        final TextView realAlarmTime = (TextView) findViewById(R.id.real_alarm_time);
        realAlarmTime.setText("Next alarm time at: " + savedNextAlarmTime.getHourOfDay() + ":" + savedNextAlarmTime.getMinuteOfHour() +
                " - Day alarm at " + savedDayAlarmTime.getHourOfDay() + ":" + savedDayAlarmTime.getMinuteOfHour());

        mockDaysView = findViewById(R.id.mock_days);
        addNewRowView = findViewById(R.id.add_new_row);
        clearMockDayView = findViewById(R.id.clear_mock_day);

        nowTimeText = (TextView) findViewById(R.id.now_text_view);
        nowTimeButton = (Button) findViewById(R.id.now_time_button);
        nowSpinner = (Spinner) findViewById(R.id.weather_now_spinner);
        transitionsListView = (ListView) findViewById(R.id.rows_weather_list);
        nowWeatherItemRow = new WeatherItemRow(0, new DateTime().now());

        configureTimeButton(nowTimeButton, nowTimeText, nowWeatherItemRow);
        configureWeatherSpinner(nowSpinner, nowWeatherItemRow);
        configureWeatherTransitionsList(transitionsListView);

        nowTimeButton.setText(timeToString(nowWeatherItemRow.getTime()));
    }

    public void generateNewMockDay(View view) {
        addNewRowView.setVisibility(View.GONE);
        mockDaysView.setVisibility(View.GONE);
        clearMockDayView.setVisibility(View.VISIBLE);

        switch(view.getId()) {
            case R.id.clearly_day:
                generateNewMockDayFor(WeatherType.CLEAR, WeatherType.CLEAR);
                break;
            case R.id.cloudy_day:
                generateNewMockDayFor(WeatherType.CLOUDY, WeatherType.CLOUDY);
                break;
            case R.id.rainy_day:
                generateNewMockDayFor(WeatherType.RAIN, WeatherType.RAIN);
                break;
            case R.id.cloudy_rainy_day:
                generateNewMockDayFor(WeatherType.CLOUDY, WeatherType.RAIN);
                break;
            case R.id.clearly_cloudy_day:
                generateNewMockDayFor(WeatherType.CLEAR, WeatherType.CLOUDY);
                break;
            case R.id.clearly_rainy_day:
                generateNewMockDayFor(WeatherType.CLEAR, WeatherType.RAIN);
                break;
        }
    }

    public void closeCard(View view) {
        findViewById(R.id.card_wrapper).setVisibility(View.GONE);
    }

    public void addNewRow(View view) {
        mockDaysView.setVisibility(View.GONE);
        DateTime newTime;
        if(weatherTransitionsList.isEmpty()) {
            newTime = nowWeatherItemRow.getTime().plus(Minutes.minutes(DEFAULT_MINUTES_NEW_ROW));
        } else {
            newTime = weatherTransitionsList.get(weatherTransitionsList.size() - 1).getTime().plus(Minutes.minutes(DEFAULT_MINUTES_NEW_ROW));
        }
        weatherTransitionsList.add(new WeatherItemRow(DEFAULT_SPINNER_POSITION, newTime));
        ((WeatherListAdapter)transitionsListView.getAdapter()).notifyDataSetChanged();

        closeCard(view);
    }

    public void resetMockDay(View view) {
        weatherTransitionsList.clear();
        ((WeatherListAdapter)transitionsListView.getAdapter()).notifyDataSetChanged();

        nowWeatherItemRow = new WeatherItemRow(0, new DateTime().now());
        configureTimeButton(nowTimeButton, nowTimeText, nowWeatherItemRow);
        configureWeatherSpinner(nowSpinner, nowWeatherItemRow);
        nowTimeButton.setText(timeToString(nowWeatherItemRow.getTime()));

        clearMockDayView.setVisibility(View.GONE);
        addNewRowView.setVisibility(View.VISIBLE);
        mockDaysView.setVisibility(View.VISIBLE);

        closeCard(view);
    }

    private void configureWeatherTransitionsList(ListView list) {
        weatherTransitionsList = new ArrayList<WeatherItemRow>();
        WeatherListAdapter adapter = new WeatherListAdapter(this, R.layout.debug_item_list, weatherTransitionsList);
        list.setAdapter(adapter);
    }

    private void configureTimeButton(final Button timeButton, final TextView laterText, final WeatherItemRow weatherItemRow) {
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
                        if (!weatherItemRow.equals(nowWeatherItemRow)) {
                            changedTime = fixCorrectDay(changedTime, laterText);
                        } else {
                            //Nothing because is nowWeatherItemRow and always marks today's hours
                        }
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

    private DateTime fixCorrectDay(DateTime changedTime, TextView laterText) {
        changedTime = changedTime.minusDays(1);
        if (changedTime.getDayOfMonth() == nowWeatherItemRow.getTime().getDayOfMonth()) {
            //Nothing because changedTime was tomorrow and now is today
        } else {
            changedTime = changedTime.plusDays(1);
            //Revert changedTime to today because it was today
        }
        if (changedTime.isBefore(nowWeatherItemRow.getTime())) {
            changedTime = changedTime.plusDays(1);
            laterText.setText("Tomorrow, at");
        } else {
            laterText.setText("Later, at");
            //Don't change changedTime because is today after time now
        }
        return changedTime;
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
        if (weatherTransitionsList.isEmpty()) {
            Toast.makeText(this, "Add a row to simulate some weather transition", Toast.LENGTH_LONG).show();
            closeCard(view);
        } else {
            ForecastTable forecastTable = getDebugForecastTable();
            if(forecastTable == null) {
                Toast.makeText(this, "Serious? All weathers unknown?", Toast.LENGTH_LONG).show();
                closeCard(view);
            }
            else {
                final DateTime timeLater = weatherTransitionsList.get(0).getTime();

                final TextView cardMessageTextView = (TextView) findViewById(R.id.card_message_text_view);
                final ImageView mascotImageView = (ImageView) findViewById(R.id.mascot_image_view);
                final TextView alertLevelTextView = (TextView) findViewById(R.id.alert_level_text_view);
                final TextView nextAlarmTextView = (TextView) findViewById(R.id.next_alarm_text_view);

                final DateTime timeNow = nowWeatherItemRow.getTime();
                final Interval intervalUntilWeatherChange = new Interval(timeNow, timeLater);
                final Alert alert = alertGenerator.generateAlert(nowWeatherItemRow.getWeatherType(), weatherTransitionsList.get(0).getWeatherType());
                cardMessageTextView.setText(alert.getAlertMessage().getNotificationMessage(intervalUntilWeatherChange));

                alertLevelTextView.setText("Alert level: " + alert.getAlertLevel());
                alertLevelTextView.setVisibility(View.VISIBLE);

                Interval alarmTime = new Interval(timeNow.getMillis(), AlarmHelper.computeNextAlarmTime(forecastTable).getMillis());
                nextAlarmTextView.setText("Next alarm: " + alarmTime.toPeriod().getHours() + " hours and " + alarmTime.toPeriod().getMinutes() + " minutes from now");
                nextAlarmTextView.setVisibility(View.VISIBLE);

                findViewById(R.id.card_wrapper).setVisibility(View.VISIBLE);
                AnimationHelper.applyCardAnimation(findViewById(R.id.card_layout));

                mascotImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), alert.getDressedMascot()));
                mascotImageView.setVisibility(View.VISIBLE);
                AnimationHelper.applyMascotAnimation(mascotImageView);
            }
        }
    }

    public void generateMessageOfTheDay(View v) {
        TextView cardMessageTextView = (TextView) findViewById(R.id.card_message_text_view);
        findViewById(R.id.mascot_image_view).setVisibility(View.GONE);
        findViewById(R.id.alert_level_text_view).setVisibility(View.GONE);
        findViewById(R.id.next_alarm_text_view).setVisibility(View.GONE);

        ForecastTable forecastTable = getDebugForecastTable();
        if(forecastTable != null) {
            Timber.d("FORECAST_TABLE: \n" + forecastTable.toString());
            cardMessageTextView.setText(new DayTemplateGenerator(new JsonDayTemplateLoader(Setup.getJsonDayTemplateReader(this))).generateMessage(forecastTable, "WORK IN PROGRESS")); //TODO: Revisar este mensaje a pelo.
        }
        else {
            cardMessageTextView.setText("Null ForecastTable, are all WeatherTypes UNKNOWN?");
        }

        findViewById(R.id.card_wrapper).setVisibility(View.VISIBLE);
        AnimationHelper.applyCardAnimation(findViewById(R.id.card_layout));
    }

    private ForecastTable getDebugForecastTable() {
        List<Forecast> forecastList = new ArrayList<Forecast>();
        List<WeatherItemRow> weatherItemRows = new ArrayList<WeatherItemRow>();

        weatherItemRows.add(nowWeatherItemRow);
        weatherItemRows.addAll(weatherTransitionsList);

        for (int i = 0 ; i< weatherItemRows.size() - 1 ; i++) {
            forecastList.add(new Forecast(getWeatherInterval(weatherItemRows.get(i), weatherItemRows.get(i + 1)),
                    new WeatherWrapper(weatherItemRows.get(i).getWeatherType())));
        }

        forecastList.add(new Forecast(getWeatherInterval(weatherItemRows.get(weatherItemRows.size() - 1), null),
                new WeatherWrapper(weatherItemRows.get(weatherItemRows.size() - 1).getWeatherType())));

        return ForecastTable.fromForecastList(forecastList);
    }

    private Interval getWeatherInterval(WeatherItemRow current, WeatherItemRow next) {
        if(next != null) {
            return new Interval(current.getTime(), next.getTime());
        }
        else {
            return new Interval(current.getTime(), current.getTime().plusHours(1));
        }
    }

    public void startWeatherService(View view) {
        startService(new Intent(this, WeatherService.class));
    }

    public void showWearOnlyNotification(View view) {
        Timber.d("Show a random notification");
        HashMap<String, List<String>> messages = new HashMap<String, List<String>>();
        List<String> messagesList = new ArrayList<String>();
        messagesList.add(getString(R.string.notif_long_text_fake));
        messages.put("messages", messagesList);

        Alert alert = new Alert(WeatherType.CLEAR, WeatherType.CLEAR, AlertLevel.INFO, messages, R.drawable.owlie_default);
        NotificationHelper.displayWearNotification(this, alert, new Interval(DateTime.now(), DateTime.now().plusHours(1)));
    }

    public void showStandardNotification(View v) {
        NotificationHelper.displayStandardNotification(
                DebugActivity.this,
                new Intent(DebugActivity.this, MainMobileActivity.class),
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
            TextView text = (TextView) rowView.findViewById(R.id.later_text_view);
            Button delete = (Button) rowView.findViewById(R.id.delete_item_button);
            Button button = (Button) rowView.findViewById(R.id.later_time_button);
            Spinner spinner = (Spinner) rowView.findViewById(R.id.weather_later_spinner);

            final WeatherItemRow item = getItem(position);

            configureTimeButton(button, text, item);
            configureWeatherSpinner(spinner, item);
            button.setText(timeToString(item.getTime()));
            spinner.setSelection(item.getWeatherTypeSpinnerPosition());
            if (nowWeatherItemRow.getTime().getDayOfMonth() == item.getTime().getDayOfMonth()) {
                text.setText("Later, at");
            } else {
                text.setText("Tomorrow, at");
            }
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    remove(item);
                    if(weatherTransitionsList.isEmpty()) {
                        mockDaysView.setVisibility(View.VISIBLE);
                    }
                }
            });

            return rowView;
        }
    }

    private DateTime getSunMockPhaseTime(int hour, int minute) {
        DateTime sunPhaseTime = DateTime.now();
        sunPhaseTime = sunPhaseTime.hourOfDay().setCopy(hour);
        sunPhaseTime = sunPhaseTime.minuteOfHour().setCopy(minute);
        return sunPhaseTime;
    }

    private void generateNewMockDayFor(WeatherType primary, WeatherType secondary) {
        Timber.d("Mock day: " + primary + " - " + secondary);
        Random random = new Random();
        DateTime nowTime = DateTime.now();
        DateTime startTime = nowTime.hourOfDay().setCopy(8).minuteOfHour().setCopy(0).secondOfMinute().setCopy(0).millisOfSecond().setCopy(0);
        int spinnerPrimaryPosition = 0;
        int spinnerSecondaryPosition = 0;

        final List<String> weatherTypeNames = new ArrayList<String>();
        for (WeatherType weatherType : WeatherType.values()) {
            weatherTypeNames.add(weatherType.toString());
        }

        for(int i=0; i<weatherTypeNames.size(); i++) {
            if(primary.toString().equals(weatherTypeNames.get(i).toString())) {
                spinnerPrimaryPosition = i;
            }
            if(secondary.toString().equals(weatherTypeNames.get(i).toString())) {
                spinnerSecondaryPosition = i;
            }
        }

        nowWeatherItemRow.setTime(startTime);
        nowWeatherItemRow.setWeatherTypeSpinnerPosition(spinnerPrimaryPosition);
        configureTimeButton(nowTimeButton, nowTimeText, nowWeatherItemRow);
        configureWeatherSpinner(nowSpinner, nowWeatherItemRow);
        nowTimeButton.setText(timeToString(nowWeatherItemRow.getTime()));
        nowSpinner.setSelection(spinnerPrimaryPosition);

        weatherTransitionsList.clear();
        for(int i = 1; i < 16; i++) {
            if(random.nextInt() % 2 == 0) {
                weatherTransitionsList.add(new WeatherItemRow(spinnerPrimaryPosition, startTime.plusHours(i)));
            } else {
                weatherTransitionsList.add(new WeatherItemRow(spinnerSecondaryPosition, startTime.plusHours(i)));
            }
        }
        ((WeatherListAdapter)transitionsListView.getAdapter()).notifyDataSetChanged();
    }
}
