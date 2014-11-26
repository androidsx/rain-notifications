package com.androidsx.commonlibrary;

public class CommonConstants {

    /**
     * Current application environment. It is essential to switch this to {@link Env#LIVE} for the Live APKs.
     */
    public static final Env ENV = Env.DEV;

    /**
     * Definition of the different runtime environments.
     */
    public enum Env {
        /** Live build for the Play store. */
        LIVE,

        /** For beta testers. */
        BETA,

        /** While we're developing and debugging. */
        DEV;
    }

    public static final String WEAR_PATH = "/forecast";
    public static final String DISMISS_PATH = "/dismiss";

    public static class Extras {
        public static final String EXTRA_TEXT = "extra_text";
        public static final String EXTRA_MASCOT_ICON = "extra_mascot_icon";
    }

    public static class Keys {
        public static final String KEY_TEXT = "key_text";
        public static final String KEY_MASCOT_ICON = "key_mascot_icon";
        public static final String KEY_TIMESTAMP = "key_timestamps";
    }
}
