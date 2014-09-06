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

import org.joda.time.Period;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
     * @see #generateAlertMessage
     * @see #generateMascot
     */
    public Alert generateAlert(Weather currentWeather, Forecast forecast) {
        if (forecast == null) {
            return new Alert(AlertLevel.NEVER_MIND,
                    generateAlertMessage(currentWeather, null),
                    generateMascot(currentWeather));
        } else {
            return new Alert(
                    generateAlertLevel(currentWeather, forecast.getForecastedWeather()),
                    generateAlertMessage(currentWeather, forecast),
                    generateMascot(forecast.getForecastedWeather())
            );
        }
    }

    public int generateMascot(Weather weather) {
        final Map<WeatherType, Integer> owlieVariations = new HashMap<WeatherType, Integer>() {
            {
                put(WeatherType.RAIN, R.array.rainy);
                put(WeatherType.SUNNY, R.array.sunny);
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

    /**
     * Generates a message for the provided weather transition, to be shown to the user as a
     * notification.
     * <p/>
     * Note that we don't know here whether the message will end up being shown to the user. That's
     * not for us here to decide.
     *
     * @return message for the user
     */
    private AlertMessage generateAlertMessage(Weather currentWeather, Forecast forecast) {
        if (forecast == null || forecast.getForecastedWeather().equals(currentWeather)) {
            if (currentWeather.getType().equals(WeatherType.SUNNY)) {
                return new AlertMessage(resourceToToRandomAlertMessage(R.array.stays_sunny));
            } else if (currentWeather.getType().equals(WeatherType.RAIN)) {
                return new AlertMessage(resourceToToRandomAlertMessage(R.array.stays_rainy));
            } else {
                return new AlertMessage("(Fallback) No changes expected for a while." //TODO: message that refers to no forecast expected in a few hours
                        + " At the moment, it is " + currentWeather);
            }
        } else {
            final Period periodFromNow = forecast.getTimeFromNow().toPeriod();

            if (currentWeather.getType().equals(WeatherType.SUNNY)
                    && forecast.getForecastedWeather().getType().equals(WeatherType.RAIN)) {
                return new AlertMessage(resourceToToRandomAlertMessage(R.array.sun_to_rain, periodFromNow.getMinutes()));
            } else if (currentWeather.getType().equals(WeatherType.RAIN)
                    && forecast.getForecastedWeather().getType().equals(WeatherType.SUNNY)) {
                return new AlertMessage(resourceToToRandomAlertMessage(R.array.rain_to_sun, periodFromNow.getMinutes()));
            } else if (currentWeather.getType().equals(WeatherType.UNKNOWN)
                    && forecast.getForecastedWeather().getType().equals(WeatherType.RAIN)) {
                return new AlertMessage(resourceToToRandomAlertMessage(R.array.unknown_to_rain, periodFromNow.getMinutes()));
            } else if (currentWeather.getType().equals(WeatherType.UNKNOWN)
                    && forecast.getForecastedWeather().getType().equals(WeatherType.SUNNY)) {
                return new AlertMessage(resourceToToRandomAlertMessage(R.array.unknown_to_sun, periodFromNow.getMinutes()));
            } else {
                return new AlertMessage("(Fallback) It's gonna be " + forecast.getForecastedWeather()
                        + " in " + UiUtil.getDebugOnlyPeriodFormatter().print(periodFromNow) + " from now"
                        + " (with a precision of +/- 1 " + forecast.getGranularity() + ")."
                        + " At the moment, it is " + currentWeather);
            }
        }
    }

    private String resourceToToRandomAlertMessage(int arrayResource) {
        return pickRandom(Arrays.asList(resources.getStringArray(arrayResource)), random);
    }

    private String resourceToToRandomAlertMessage(int arrayResource, int minutes) {
        return String.format(resourceToToRandomAlertMessage(arrayResource), minutes);
    }

    private static <T> T pickRandom(List<T> list, Random random) {
        return new ArrayList<T>(list).get(random.nextInt(list.size()));
    }
}
