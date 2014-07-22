package com.androidsx.rainnotifications.model;

/**
 * An alert, that has a level and a message.
 */
public class Alert {
    private final AlertLevel alertLevel;
    private final AlertMessage alertMessage;

    public Alert(AlertLevel alertLevel, AlertMessage alertMessage) {
        this.alertLevel = alertLevel;
        this.alertMessage = alertMessage;
    }

    public AlertLevel getAlertLevel() {
        return alertLevel;
    }

    public AlertMessage getAlertMessage() {
        return alertMessage;
    }
}
