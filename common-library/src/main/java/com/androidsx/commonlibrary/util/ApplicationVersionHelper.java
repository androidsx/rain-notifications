package com.androidsx.commonlibrary.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;


public class ApplicationVersionHelper {
    private static final String TAG = ApplicationVersionHelper.class.getSimpleName();

    private static final String VERSION_CODE_PREFS_NAME = "versionCode";
    private static final String NUM_USES_PREFS_NAME = "numUses";

    /**
     * Is the user opening the application for the first time after an update?
     * <p>
     * Make sure you execute {@link #saveCurrentVersionCode} after the initialization phase is over.
     *
     * @return true if and only if the user just updated the application and opens it for the first time
     */
    public static boolean isUserOpeningAppAfterUpdate(Context context) {
        int versionCode = getApplicationVersionCode(context);
        int savedVersionCode = SharedPrefsHelper.getIntValue(context, VERSION_CODE_PREFS_NAME);
        return savedVersionCode != 0 && savedVersionCode != versionCode;
    }

    public static int getSavedVersionCode(Context context) {
        return SharedPrefsHelper.getIntValue(context, VERSION_CODE_PREFS_NAME);
    }

    /**
     * Saves the current version code on the Shared Preferences.
     * <p>
     * Keep in mind that the execution of this method invalidates the output of {@link #//isUserOpeningAppForFirstTime}
     * and {@link #isUserOpeningAppAfterUpdate}.
     */
    public static void saveCurrentVersionCode(Context context) {
        int versionCode = getApplicationVersionCode(context);
        SharedPrefsHelper.saveIntValue(context, VERSION_CODE_PREFS_NAME, versionCode);
    }

    /**
     * @return number of times that the application has been opened
     */
    public static int getNumUses(Context context) {
        return SharedPrefsHelper.getIntValue(context, NUM_USES_PREFS_NAME);
    }

    /** Should be executed once and only once in the application. */
    public static int saveNewUse(Context context) {
        int numUses = getNumUses(context) + 1;
        SharedPrefsHelper.saveIntValue(context, NUM_USES_PREFS_NAME, numUses);

        Log.v(TAG, "Saving a new usage of the app: " + numUses);
        return numUses;
    }

    /**
     * Get the version of this application from the android manifest
     *
     * @param context the context to use for the version retrieval.
     * @return the version of the application or an empty string if the version could not be retrieved.
     */
    public static String getApplicationVersion(Context context) {
        String version = "";
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            ;
        }
        return version;
    }

    public static int getApplicationVersionCode(Context context) {
        int versionCode = 1;
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
            Log.v(TAG, "Version code for the application: " + versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            ;
        }
        return versionCode;
    }
}
