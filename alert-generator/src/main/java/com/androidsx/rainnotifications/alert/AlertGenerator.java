package com.androidsx.rainnotifications.alert;

import android.content.res.Resources;
import android.content.res.TypedArray;

import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.AlertLevel;
import com.androidsx.rainnotifications.model.AlertMessage;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.model.util.UiUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    private final Resources resources;

    public AlertGenerator(Resources resources) {
        this.resources = resources;
    }

    public List<Alert> getAlertsList() throws IOException {
        Reader jsonReader = new InputStreamReader(resources.getAssets().open("alerts.json"));
        Gson gson = new GsonBuilder().create();
        Alert[] jsonAlerts = gson.fromJson(jsonReader, Alert[].class);

        return Arrays.asList(jsonAlerts);
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
                    if (a.getFromType().equals(currentWeather.getType()) && a.getToType().equals("*")) {
                        a.setDressedMascot(generateMascot(currentWeather));
                        return a;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int generateMascot(Weather weather) {
        final Map<WeatherType, Integer> owlieVariations = new HashMap<WeatherType, Integer>() {
            {
                put(WeatherType.RAIN, R.array.rainy);
                put(WeatherType.CLEAR, R.array.sunny);
                put(WeatherType.CLOUDY, R.array.cloudy);
                put(WeatherType.PARTLY_CLOUDY, R.array.partlycloudy);
                put(WeatherType.UNKNOWN, R.array.default_weather);
            }
        };

        final int mascotArray = owlieVariations.get(weather.getType());
        final TypedArray mascotTypedArray = resources.obtainTypedArray(mascotArray);
        final int mascotIndex = random.nextInt(mascotTypedArray.length());
        return mascotTypedArray.getResourceId(mascotIndex, -1);
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

    private static <T> T pickRandom(List<T> list, Random random) {
        return new ArrayList<T>(list).get(random.nextInt(list.size()));
    }
}
