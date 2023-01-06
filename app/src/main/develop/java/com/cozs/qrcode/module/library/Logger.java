package com.cozs.qrcode.module.library;

import android.util.Log;

import com.cozs.qrcode.BuildConfig;

public class Logger {
    public static final boolean DEBUG = BuildConfig.LOG_ENABLE;

    public static void d(String tag, String message) {
        if (DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void d(String message) {
        if (DEBUG) {
            Log.d("C508", message);
        }
    }

    public static void e(String message) {
        if (DEBUG) {
            Log.e("C508", message);
        }
    }
}
