package com.androidsx.rainnotifications.backgroundservice;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.IBinder;

import com.androidsx.commonlibrary.CommonConstants;
import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.alert.DayTemplateGenerator;
import com.androidsx.rainnotifications.backgroundservice.util.AlarmHelper;
import com.androidsx.rainnotifications.backgroundservice.util.NotificationHelper;
import com.androidsx.rainnotifications.backgroundservice.util.UserLocationFetcher;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientException;
import com.androidsx.rainnotifications.forecastapislibrary.WeatherClientResponseListener;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.Day;
import com.androidsx.rainnotifications.model.DayTemplate;
import com.androidsx.rainnotifications.model.DayTemplateLoaderFactory;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.weatherclientfactory.WeatherClientFactory;

import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;

import timber.log.Timber;

/**
 * This service is responsible to make API calls to forecast.io
 * Once it starts, make an API call to forecast.io with the obtained coordinates.
 *
 * The response is analyzed for determine the next alarm time, and if it's appropriate
 * notify to user the next significant weather change.
 */
public class WeatherService extends Service {
    private static final long ONE_HOUR_MILLIS = 1 * 60 * DateTimeConstants.MILLIS_PER_MINUTE;
    private AlertGenerator alertGenerator;
    private DayTemplateGenerator dayTemplateGenerator;

    @Override
    public void onCreate() {
        super.onCreate();
        alertGenerator = new AlertGenerator(this);
        alertGenerator.init();
        dayTemplateGenerator = new DayTemplateGenerator(DayTemplateLoaderFactory.getDayTemplateLoader(this));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        UserLocationFetcher.getUserLocation(this, new UserLocationFetcher.UserLocationResultListener() {
            @Override
            public void onLocationSuccess(Location location) {
                WeatherClientFactory.requestForecastForLocation(getApplicationContext(), location.getLatitude(), location.getLongitude(), new WeatherClientResponseListener() {
                    @Override
                    public void onForecastSuccess (ForecastTable forecastTable){
                        if (intent != null && intent.getIntExtra(Constants.Extras.EXTRA_DAY_ALARM, 0) == Constants.Alarms.DAY_ALARM_ID) {
                            Day day = new Day(forecastTable);
                            DayTemplate template = dayTemplateGenerator.getDayTemplate(day);
                            String message;
                            if(template == null) {
                                message = getString(R.string.default_day_message);
                            }
                            else {
                                message = template.resolveMessage(WeatherService.this, day);
                            }

                            NotificationHelper.displayStandardNotification(
                                    getApplicationContext(),
                                    new Intent(Constants.CustomIntent.BACKGROUND_INTENT),
                                    message,
                                    BitmapFactory.decodeResource(getResources(), R.drawable.owlie_default),
                                    "Forecast report " + DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss").print(forecastTable.getBaselineStart()),
                                    generateForecastReportMessage(message, day, template, forecastTable));
                        } else {
                            if (!forecastTable.hasTransitions()) {
                                Timber.d("No transitions are expected, so there's no notifications to generate");
                            } else {
                                final Forecast transition = forecastTable.getFirstTransitionForecast();
                                final Alert alert = alertGenerator.generateAlert(forecastTable.getBaselineForecast().getWeatherWrapper().getWeatherType(), transition.getWeatherWrapper().getWeatherType());
                                if (shouldLaunchNotification(transition.getInterval().getStartMillis() - System.currentTimeMillis())) {
                                    Timber.i("Will display notification for " + alert); // FIXME: Desde Aqui es desde donde se produce el crash.
                                    NotificationHelper.displayWearNotification(getApplicationContext(), alert, new Interval(forecastTable.getBaselineStart(), transition.getInterval().getStart()));
                                } else {
                                    Timber.d("No notification for now. The alert was " + alert);
                                }
                            }

                            setNextAlarm(forecastTable);
                        }
                    }

                    @Override
                    public void onForecastFailure (WeatherClientException exception){
                        Timber.e(exception, "Failed to get the forecast");
                        NotificationHelper.displayStandardNotification(
                                WeatherService.this,
                                new Intent(Constants.CustomIntent.BACKGROUND_INTENT),
                                "Failed to get the forecast: " + exception.toString(),
                                BitmapFactory.decodeResource(getResources(),
                                R.drawable.owlie_default));
                    }
                });
            }

            @Override
            public void onLocationFailure(UserLocationFetcher.UserLocationException exception) {
                Timber.e(exception, "Failed to get the location");
                NotificationHelper.displayStandardNotification(WeatherService.this,
                        new Intent(Constants.CustomIntent.BACKGROUND_INTENT),
                        "Failed to get the location" + exception.toString(),
                        BitmapFactory.decodeResource(getResources(),
                        R.drawable.owlie_default));
            }
        });

        return START_NOT_STICKY;
    }

    private String generateForecastReportMessage(String message, Day day, DayTemplate template, ForecastTable forecastTable) {
        if(CommonConstants.ENV.equals(CommonConstants.Env.DEV)) {
            StringBuilder builder = new StringBuilder();
            builder.append("SUMMARY:\n     " + message);
            builder.append("\n\n" + day);
            builder.append("\n\n" + template);
            builder.append("\n\n" + forecastTable);
            return builder.toString();
        }
        else {
            return null;
        }
    }

    private void setNextAlarm(ForecastTable forecastTable) {
        final PendingIntent weatherAlarmIntent = PendingIntent.getService(
                WeatherService.this,
                Constants.Alarms.WEATHER_ID,
                new Intent(getApplicationContext(), WeatherService.class),
                0);
        AlarmHelper.setNextAlarm(
                WeatherService.this,
                weatherAlarmIntent,
                AlarmHelper.computeNextAlarmTime(forecastTable),
                forecastTable
        );
    }

    /**
     * Method for determine if a notification is launched,
     * depending on the next alarm time period passed as a param.
     *
     * @param nextForecastTimePeriod
     */
    private boolean shouldLaunchNotification(long nextForecastTimePeriod) {
        if (nextForecastTimePeriod < ONE_HOUR_MILLIS) {
            return true;
        } else {
            return false;
        }
    }
}
