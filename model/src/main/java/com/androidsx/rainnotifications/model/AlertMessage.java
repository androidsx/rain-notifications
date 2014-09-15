package com.androidsx.rainnotifications.model;

import com.androidsx.rainnotifications.model.util.UiUtil;

import org.joda.time.Interval;
import org.joda.time.Period;

/**
 * Alert message that is suitable to be shown in a notification to the user. It should be
 * localized.
 */
public class AlertMessage {
    private final String notificationMessage;

    public AlertMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    /**
     * Returns the notification message ready to be used in the UI.
     *
     * @param interval time interval to the next transition, required to generate many of the
     * templated alert messages
     */
    public String getNotificationMessage(Interval interval) {
        return String.format(notificationMessage,
                UiUtil.getDebugOnlyPeriodFormatter().print(new Period(interval)));
    }

    @Override
    public String toString() {
        return notificationMessage;
    }
}
