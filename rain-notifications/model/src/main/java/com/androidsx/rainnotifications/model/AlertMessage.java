package com.androidsx.rainnotifications.model;

/**
 * Alert message that is suitable to be shown in a notification to the user. It should be
 * localized.
 */
public class AlertMessage {
    private final String notificationMessage;

    public AlertMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    @Override
    public String toString() {
        return getNotificationMessage();
    }
}
