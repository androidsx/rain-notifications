package com.androidsx.rainnotifications.alert;

import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.AlertLevel;
import com.androidsx.rainnotifications.model.AlertMessage;
import com.androidsx.rainnotifications.model.Forecast;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.WeatherType;
import com.androidsx.rainnotifications.model.util.UiUtil;

import org.joda.time.LocalTime;
import org.joda.time.Period;

/**
 * Generates weather alerts that are relevant to the user.
 * <p/>
 * Note that messages are not coupled with the corresponding alert levels. Hopefully we manage to
 * keep them separate, so that we can create messages for weather transitions and evolve them
 * independently or what alert level they have.
 */
public class AlertGenerator {

    /**
     * Generates a weather alert.
     *
     * @param currentWeather weather at the current moment
     * @param forecast forecast for some point in the future. This is usually expected to be a
     *                 different weather than the current one
     *
     * @return an alert for the provided weather transition
     * @see #generateAlertLevel
     * @see #generateAlertMessage
     */
    public Alert generateAlert(Weather currentWeather, Forecast forecast) {
        return new Alert(
                generateAlertLevel(currentWeather, forecast.getForecastedWeather()),
                generateAlertMessage(currentWeather, forecast)
        );
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
        /*if (current.getType() != WeatherType.RAIN && future.getType() == WeatherType.RAIN) {
            return AlertLevel.INFO;
        } else if (current.getType() == WeatherType.RAIN && future.getType() != WeatherType.RAIN) {
            return AlertLevel.INFO;
        } else {
            return AlertLevel.NEVER_MIND;
        }*/
        if(current.getType() != WeatherType.UNKNOWN && future.getType() != WeatherType.UNKNOWN) {
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
        final Period periodFromNow = forecast.getTimeFromNow().toPeriod();

        /*return new AlertMessage("It's gonna be \"" + forecast.getForecastedWeather() + "\""
                + " in " + UiUtil.getDebugOnlyPeriodFormatter().print(periodFromNow) + " from now"
                + " (with a precision of +/- 1 " + forecast.getGranularity() + ", though)."
                + " At the moment, it is " + currentWeather);*/
        if(currentWeather.getType() != forecast.getForecastedWeather().getType()) {
            return new AlertMessage("At " + new LocalTime(System.currentTimeMillis() + forecast.getTimeFromNow().getEndMillis()) + " - " + forecast.getForecastedWeather().toString());
        } else {
            return new AlertMessage("No changes in next hours.");
        }
    }
}
