package com.androidsx.rainnotifications.model;

/**
 * An alert, that has a level and a message.
 */
public class Alert {
    private final AlertLevel alertLevel;
    private final AlertMessage alertMessage;
    private final int dressedMascot;

    public Alert(AlertLevel alertLevel, AlertMessage alertMessage, int dressedMascot) {
        this.alertLevel = alertLevel;
        this.alertMessage = alertMessage;
        this.dressedMascot = dressedMascot;
    }

    public AlertLevel getAlertLevel() {
        return alertLevel;
    }

    public AlertMessage getAlertMessage() {
        return alertMessage;
    }

    /**
     * Mascot that is dressed in an outfit that's appropriate for the weather that is coming. Take
     * into account that, if no changes are expected, the outfit may correspond to the current
     * weather.
     *
     * @return mascot dressed for the weather announced in this alert
     */
    public int getDressedMascot() {
        return dressedMascot;
    }

    @Override
    public String toString() {
        return "Alert " + getAlertLevel() + " with message \"" + getAlertMessage() + "\"";
    }
}
