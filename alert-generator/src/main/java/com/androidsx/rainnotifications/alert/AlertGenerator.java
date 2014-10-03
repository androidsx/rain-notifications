package com.androidsx.rainnotifications.alert;

import android.content.Context;

import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.model.WeatherTypeMascots;
import com.google.gson.GsonBuilder;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import timber.log.Timber;

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

    private List<Alert> alerts;
    private List<WeatherTypeMascots> mascots;

    /**
     * You must call the {@link #init} method after the constructor.
     */
    public AlertGenerator(Context context) {
        this.context = context;
    }

    public void init() {
        final InputStream alertsJsonInputInputStream;
        try {
            alertsJsonInputInputStream = context.getResources().getAssets().open("alerts.json");
        } catch (IOException e) {
            throw new IllegalStateException("Can't parse the alerts JSON file", e);
        }
        final InputStream mascotJsonInputInputStream;
        try {
            mascotJsonInputInputStream = context.getResources().getAssets().open("weatherTypeMascots.json");
        } catch (IOException e) {
            throw new IllegalStateException("Can't parse the mascots JSON file", e);
        }

        init(alertsJsonInputInputStream, mascotJsonInputInputStream);
    }

    /** For testing purposes only. */
    void init(InputStream alertsJsonInputInputStream, InputStream mascotJsonInputInputStream) {
        final Reader alertJsonReader = new InputStreamReader(alertsJsonInputInputStream);
        final Alert[] jsonAlerts = new GsonBuilder().create().fromJson(alertJsonReader, Alert[].class);
        this.alerts = Arrays.asList(jsonAlerts);

        final Reader mascotJsonReader = new InputStreamReader(mascotJsonInputInputStream);
        final WeatherTypeMascots[] jsonWeatherTypesMascots = new GsonBuilder().create().fromJson(mascotJsonReader, WeatherTypeMascots[].class);
        this.mascots = Arrays.asList(jsonWeatherTypesMascots);
    }

    /** For testing purposes only. */
    void init(List<Alert> alerts, List<WeatherTypeMascots> mascots) {
        this.alerts = alerts;
        this.mascots = mascots;
    }

    /**
     * Generates a weather alert.
     *
     * @param currentWeather weather at the current moment
     * @param forecastedWeather forecast for some point in the future, or null if it is
     *                 unknown. This is usually expected to be a different
     *                 weather than the current one.
     *
     * @return an alert for the provided weather transition
     * @see #generateMascot
     */
    public Alert generateAlert(WeatherType currentWeather, WeatherType forecastedWeather) {
        if (alerts == null || mascots == null) {
            throw new IllegalStateException("Did you forget to call the init() method?");
        }

        for(Alert a : alerts) {
            if (forecastedWeather != null) {
                if (a.getFromType().equals(currentWeather) && a.getToType().equals(forecastedWeather)) {
                    a.setDressedMascot(generateMascot(forecastedWeather));
                    return a;
                }
            } else {
                if (a.getFromType().equals(currentWeather) && a.getToType().equals(currentWeather)) {
                    a.setDressedMascot(generateMascot(currentWeather));
                    return a;
                }
            }
        }

        throw new IllegalArgumentException("Didn't find an alert for " + currentWeather + " -> " + forecastedWeather);
    }

    private int generateMascot(WeatherType weather) {
        final List<Integer> mascotsForThisWeather = new ArrayList<Integer>();
        for (WeatherTypeMascots wtm : mascots) {
            Timber.d(""+ weather);
            Timber.v("" + wtm);
            if (wtm.getType().equals(weather)) {
                for (String s : wtm.getDressedMascots()) {
                    mascotsForThisWeather.add(context.getResources().getIdentifier(s, "drawable", context.getPackageName()));
                }
            }
        }

        return mascotsForThisWeather.get(random.nextInt(mascotsForThisWeather.size()));
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
