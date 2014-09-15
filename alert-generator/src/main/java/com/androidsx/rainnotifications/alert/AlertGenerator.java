package com.androidsx.rainnotifications.alert;

import android.content.Context;

import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.AlertLevel;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.model.WeatherTypeMascots;
import com.androidsx.rainnotifications.model.util.UiUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Generates weather alerts that are relevant to the user.
 * <p/>
 * Note that messages are not coupled with the corresponding alert levels. Hopefully we manage to
 * keep them separate, so that we can create messages for weather transitions and evolve them
 * independently or what alert level they have.
 */
public class AlertGenerator {
    private final Random random = new Random();
    private final Context context;

    public AlertGenerator(Context context) {
        this.context = context;
    }

    public List<Alert> getAlertsList() throws IOException {
        Reader jsonReader = new InputStreamReader(context.getResources().getAssets().open("alerts.json"));
        Gson gson = new GsonBuilder().create();
        Alert[] jsonAlerts = gson.fromJson(jsonReader, Alert[].class);

        return Arrays.asList(jsonAlerts);
    }

    public List<WeatherTypeMascots> getWeatherTypeMascotsList() throws  IOException {
        Reader jsonReader = new InputStreamReader(context.getResources().getAssets().open("weatherTypeMascots.json"));
        Gson gson = new GsonBuilder().create();
        WeatherTypeMascots[] jsonWeatherTypesMascots = gson.fromJson(jsonReader, WeatherTypeMascots[].class);

        return Arrays.asList(jsonWeatherTypesMascots);
    }

    /**
     * Generates a weather alert.
     *
     * @param currentWeather weather at the current moment
     * @param forecast forecast for some point in the future, or null if it is
     *                 unknown. This is usually expected to be a different
     *                 weather than the current one.
     *
     * @return an alert for the provided weather transition
     * @see #generateAlertLevel
     * @see #generateMascot
     */
    public Alert generateAlert(Weather currentWeather, Forecast forecast) {
        try {
            for(Alert a : getAlertsList()) {
                if (forecast != null) {
                    if (a.getFromType().equals(currentWeather.getType()) && a.getToType().equals(forecast.getForecastedWeather().getType())) {
                        a.setDressedMascot(generateMascot(forecast.getForecastedWeather()));
                        return a;
                    }
                } else {
                    if (a.getFromType().equals(currentWeather.getType()) && a.getToType().equals(currentWeather.getType())) {
                        a.setDressedMascot(generateMascot(currentWeather));
                        return a;
                    }
                }
            }
        } catch (IOException e) {
            new IllegalArgumentException("Can't find a alert for " + currentWeather + " - " + forecast.getForecastedWeather(), e);
        }

        throw new IllegalArgumentException("Didn't find an alert for " + currentWeather + " -> " + forecast);
    }

    public String getAlertMessage(Alert alert, Interval interval) {
        return interval == null ? alert.getAlertMessage().toString() : String.format(alert.getAlertMessage().toString(),
                UiUtil.getDebugOnlyPeriodFormatter().print(new Period(interval)));
    }

    public int generateMascot(Weather weather) {

        List<Integer> mascots = new ArrayList<Integer>();
        try {
            for(WeatherTypeMascots wtm : getWeatherTypeMascotsList()) {
                if (wtm.getType().equals(weather.getType())) {
                    for (String s : wtm.getDressedMascots()) {
                        mascots.add(context.getResources().getIdentifier(s, "drawable", context.getPackageName()));
                    }
                }
            }
        } catch (IOException e) {
            new IllegalArgumentException("Can't find a mascot drawable for " + weather, e);
        }

        final int mascotIndex = random.nextInt(mascots.size());
        return mascots.get(mascotIndex);
    }

    /**
     * Generates the alert level for the provided weather transition.
     * <p/>
     * See the tests to understand the different transitions. And keep them updated with any
     * changes.
     * <p/>
     * Visibility raised from private for testing purposes.
     *
     * @return alert level that this weather transition deserves
     */
    AlertLevel generateAlertLevel(Weather current, Weather future) {
        if (current.getType() != WeatherType.RAIN && future.getType() == WeatherType.RAIN) {
            return AlertLevel.INFO;
        } else if (current.getType() == WeatherType.RAIN && future.getType() != WeatherType.RAIN) {
            return AlertLevel.INFO;
        } else {
            return AlertLevel.NEVER_MIND;
        }
    }

    /** Visibility raised from private for testing purposes. */
    String periodToString(Period period, String hours, String minutes, Locale locale) {
        final PeriodFormatter durationFormatter = new PeriodFormatterBuilder()
                .appendHours()
                .appendSeparatorIfFieldsBefore(" " + hours + " and ")
                .appendMinutes()
                .appendSeparatorIfFieldsBefore(" " + minutes)
                .toFormatter()
                .withLocale(locale);

        return durationFormatter.print(period);
    }
}
