package org.GoGoVillage;

import android.util.Log;

/**
 * Created by Tim Chen on 2018/1/25.
 * <p>
 * 管理Log的地方
 */

public class GoGoLogcat {

    private static final String HEAD_STRING = "GoGoLogcat log";

    // region log base with debug switch

    public static void logD(String customTag, String message) {
        if (BuildConfig.LOG_SWITCH) {
            Log.d(HEAD_STRING, customTag + ", " + message);
        }
    }

    public static void logI(String customTag, String message) {
        if (BuildConfig.LOG_SWITCH) {
            Log.i(HEAD_STRING, customTag + ", " + message);
        }
    }

    public static void logE(String customTag, String message) {
        if (BuildConfig.LOG_SWITCH) {
            Log.e(HEAD_STRING, customTag + ", " + message);
        }
    }

    public static void logW(String customTag, String message) {
        if (BuildConfig.LOG_SWITCH) {
            Log.w(HEAD_STRING, customTag + ", " + message);
        }
    }

    public static void logV(String customTag, String message) {
        if (BuildConfig.LOG_SWITCH) {
            Log.v(HEAD_STRING, customTag + ", " + message);
        }
    }

    // endregion
}
