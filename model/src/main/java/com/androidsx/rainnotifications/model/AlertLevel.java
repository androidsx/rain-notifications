package com.androidsx.rainnotifications.model;

/**
 * Level of a weather alert, that helps decide whether it's worth showing the alert to the user or
 * not, or if different UI effects are to be applied (such as for warnings).
 */
public enum AlertLevel {

    /**
     * Never mind, don't show this alert. For instance, it's sunny but it'll get a little cloudy. We
     * may want to show these to test users all for debug purposes, though.
     */
    NEVER_MIND,

    /**
     * The default alert, such as when it's sunny and it's gonna start raining.
     */
    INFO;
}
