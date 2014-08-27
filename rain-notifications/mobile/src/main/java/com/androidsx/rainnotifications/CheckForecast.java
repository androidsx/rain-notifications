package com.androidsx.rainnotifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.androidsx.rainnotifications.alert.AlertGenerator;
import com.androidsx.rainnotifications.forecast_io.ForecastIoNetworkServiceTask;
import com.androidsx.rainnotifications.forecast_io.ForecastIoRequest;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.model.util.UiUtil;
import com.androidsx.rainnotifications.service.WeatherService;
import com.androidsx.rainnotifications.util.LocationHelper;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public abstract class CheckForecast {

    private static final String TAG = CheckForecast.class.getSimpleName();

    private static final long WEATHER_REPEATING_TIME_MILLIS = 10 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long TEN_MINUTES_MILLIS = 10 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long ONE_HOUR_MILLIS = 1 * 60 * DateTimeConstants.MILLIS_PER_MINUTE;
    private static final long DEFAULT_EXTRA_TIME_MILLIS = 1 * 60 * DateTimeConstants.MILLIS_PER_MINUTE;

    private final AlertGenerator alertGenerator = new AlertGenerator();
    private PendingIntent weatherAlarmIntent;

    private Context context;

    public CheckForecast(Context context, Intent intent) {
        this.context = context;
        weatherAlarmIntent = PendingIntent.getService(context, Constants.AlarmId.WEATHER_ID, intent, 0);
    }

    public abstract void summaryReady(String location, Weather currentWeather, Forecast forecast, Alert alert);

    public void start() {
        getLocation();
    }

    private void getLocation() {
        new UserLocation(context) {
            @Override
            public void obtainedLocation(Location loc) {
                if (loc != null) {
                    Timber.tag(TAG).i("\nTime: %s \nAsk forecast.io for the forecast in %s (GPS %f, %f).",
                            new LocalTime(System.currentTimeMillis()),
                            getLocationAddress(loc.getLatitude(), loc.getLongitude()),
                            loc.getLatitude(), loc.getLongitude());
                    callForecastIO(loc.getLatitude(), loc.getLongitude());
                } else {
                    // TODO: probably notify to user, that the gps is disabled or not available,
                    // if we try to obtain many times the location.
                }
            }
        }.getUserLocation();
    }

    private void callForecastIO(final double longitude, final double latitude) {
        if (LocationHelper.rightCoordinates(latitude, longitude)) {
            new ForecastIoNetworkServiceTask() {

                @Override
                protected void onSuccess(ForecastTable forecastTable) {
                    // TODO: Here is where we should apply our logic
                    final Weather currentWeather = forecastTable.getBaselineWeather();
                    final Forecast forecast;
                    if (forecastTable.getForecasts().isEmpty()) {
                        forecast = null;
                    } else {
                        forecast = forecastTable.getForecasts().get(0);
                    }
                    scheduleAlarm(
                            currentWeather,
                            forecastTable.getForecasts()
                    );
                    summaryReady(
                            getLocationAddress(longitude, latitude),
                            currentWeather,
                            forecast,
                            alertGenerator.generateAlert(currentWeather, forecast));
                }

                @Override
                protected void onFailure() {
                    // TODO: And here is where we do something smart about failures
                }
            }.execute(new ForecastIoRequest(latitude, longitude).getRequest());
        }
    }

    private void scheduleAlarm(Weather currentWeather, List<Forecast> forecasts) {
        long nextAlarmTime;
        if (forecasts.isEmpty()) {
            nextAlarmTime = System.currentTimeMillis() + DEFAULT_EXTRA_TIME_MILLIS;
        } else {
            nextAlarmTime = forecasts.get(0).getTimeFromNow().toDurationMillis();
        }
        long nextAlarmTimePeriod = nextWeatherCallAlarmTime(nextAlarmTime);

        weatherAlarmIntent.cancel();
        weatherAlarmIntent = PendingIntent.getService(
                context,
                Constants.AlarmId.WEATHER_ID,
                new Intent(context, WeatherService.class),
                0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.cancel(weatherAlarmIntent);
            am.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    nextWeatherCallAlarmTime(nextAlarmTime),
                    WEATHER_REPEATING_TIME_MILLIS,
                    weatherAlarmIntent);
            if (!forecasts.isEmpty()) {
                Timber.tag(TAG).i("Next transition is %s -> %s in %s.",
                        currentWeather.getType(),
                        forecasts.get(0).getForecastedWeather().getType(),
                        UiUtil.getDebugOnlyPeriodFormatter().print(
                                new Period(forecasts.get(0).getTimeFromNow()))
                );
                Timber.tag(TAG).i("Schedule an alarm for %s from now. Bye!",
                        UiUtil.getDebugOnlyPeriodFormatter().print(
                                new Period(nextAlarmTimePeriod))
                );
            } else {
                Timber.tag(TAG).i("Schedule an alarm for %s from now, we don't expect changes. Bye!",
                        UiUtil.getDebugOnlyPeriodFormatter().print(
                                new Period(nextAlarmTimePeriod))
                );
            }
        }
        Timber.tag(TAG).i("***********************");
    }

    /**
     * This method is for determine the next alarm hour,
     * depending on the interval from now to expected hour passed as a param
     *
     * @param expectedHour
     * @return next alarm hour in millis
     */
    public long nextWeatherCallAlarmTime(long expectedHour) {
        final long currentTime = System.currentTimeMillis();
        if ((expectedHour - currentTime) < TEN_MINUTES_MILLIS) {
            return expectedHour;
        } else if (expectedHour - currentTime < 2 * ONE_HOUR_MILLIS){
            return currentTime + getTimePeriodPercentage((expectedHour - currentTime), 70);
        } else {
            return currentTime + ONE_HOUR_MILLIS;
        }
    }

    private String getLocationAddress(double latitude, double longitude) {
        String address = context.getString(R.string.current_name_location);

        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            if (addresses.get(0).getSubLocality() != null) address = addresses.get(0).getSubLocality();
            else if (addresses.get(0).getLocality() != null) address = addresses.get(0).getLocality();
            else if (addresses.get(0).getCountryName() != null) address = addresses.get(0).getCountryName();
        }

        return address;
    }

    private long getTimePeriodPercentage(long time, int percentage) {
        return time * percentage / 100;
    }

    public int getIconFromWeather(Weather weather) {
        if (weather == null) return Constants.FORECAST_ICONS.get(WeatherType.UNKNOWN);
        return Constants.FORECAST_ICONS.containsKey(weather.getType())
                ? Constants.FORECAST_ICONS.get(weather.getType())
                : Constants.FORECAST_ICONS.get(WeatherType.UNKNOWN);
    }
}
