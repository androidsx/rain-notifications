package com.androidsx.rainnotifications.alert;

import android.content.res.Resources;

import com.androidsx.rainnotifications.alert.util.ResourcesHelper;
import com.androidsx.rainnotifications.model.Alert;
import com.androidsx.rainnotifications.model.AlertLevel;
import com.androidsx.rainnotifications.model.AlertMessage;
import com.androidsx.rainnotifications.model.ForecastTable;
import com.androidsx.rainnotifications.model.Weather;
import com.androidsx.rainnotifications.model.WeatherType;

import java.util.Random;

public class DayMessageGenerator {
    private final Random random = new Random();
    private final Resources resources;

    public DayMessageGenerator(Resources resources) {
        this.resources = resources;
    }

    /**
     * Generates a weather alert.
     *
     * @param currentWeather weather at the current moment
     * @param forecastTable forecasts for some points in the future, or empty if there is
     *                 no changes.
     *
     * @return an alert for the provided weather transition
     * @see #generateDayMessage
     */
    public Alert generateAlert(Weather currentWeather, ForecastTable forecastTable) {
        if (forecastTable.getForecasts().isEmpty()) {
            return new Alert(AlertLevel.NEVER_MIND,
                    generateDayMessage(currentWeather, null),
                    ResourcesHelper.generateMascot(currentWeather, resources, random));
        } else {
            return new Alert(
                    generateAlertLevel(currentWeather, forecastTable),
                    generateDayMessage(currentWeather, forecastTable),
                    ResourcesHelper.generateMascot(forecastTable.getBaselineWeather(), resources, random)
            );
        }
    }

    /**
     * Generates the alert level for the provided weather transitions.
     * <p/>
     * See the tests to understand the different transitions. And keep them updated with any
     * changes.
     * <p/>
     * Visibility raised from private for testing purposes.
     *
     * @return alert level that this weather transition deserves
     */
    AlertLevel generateAlertLevel(Weather current, ForecastTable future) {
        //TODO: change that for analyze all forecasts for determine the proper AlertLevel
        if (current.getType() != WeatherType.RAIN) {
            return AlertLevel.INFO;
        } else {
            return AlertLevel.NEVER_MIND;
        }
    }

    /**
     * Generates a message for the provided weather transitions, to be shown to the user as a
     * day message.
     * <p/>
     * Note that we don't know here whether the message will end up being shown to the user. That's
     * not for us here to decide.
     *
     * @return message for the user
     */
    public AlertMessage generateDayMessage(Weather currentWeather, ForecastTable forecastTable) {
        //TODO: change that for analyze all forecasts for determine the proper day message
        if (forecastTable.getForecasts().isEmpty()) {
            return new AlertMessage("(Fallback) No changes expected for a while." //TODO: message that refers to no forecast expected in a few hours
                    + " At the moment, it is " + currentWeather);
        } else {
            return new AlertMessage(ResourcesHelper.resourceToToRandomAlertMessage(resources, R.array.stays_sunny, random));
        }
    }
}
