package com.pine.tool.util;

import android.util.Log;

import com.pine.tool.BuildConfig;

/**
 * Created by tanghongfeng on 2018/9/5.
 */

public class LogUtils {
    private static final String LOG_PREFIX = "Pine_";
    private static final int MAX_LOG_TAG_LENGTH = 23 - LOG_PREFIX.length();

    //各个Log级别定义的值，级别越高值越大
    /*
        public static final int VERBOSE = 2;
        public static final int DEBUG = 3;
        public static final int INFO = 4;
        public static final int WARN = 5;
        public static final int ERROR = 6;
        public static final int ASSERT = 7;
    */
    private static int LOG_LEVEL = Log.DEBUG;
    private static boolean DEBUG = BuildConfig.DEBUG;

    private LogUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void setDebuggable(boolean isDebuggable) {
        Log.d("LogUtils", "setDebuggable isDebuggable:" + isDebuggable);
        DEBUG = isDebuggable;
    }

    public static void setDebugLevel(int level) {
        Log.d("LogUtils", "setDebugLevel level:" + level);
        LOG_LEVEL = level;
    }

    public static String makeLogTag(Class clz) {
        String str = clz.getSimpleName();
        if (str.length() > MAX_LOG_TAG_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - 1);
        }
        return LOG_PREFIX + str;
    }

    /**
     * 用于生产版本的必要log
     *
     * @param tag
     * @param msg
     */
    public static void releaseLog(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isLogEnable(Log.VERBOSE)) {
            Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable cause) {
        if (isLogEnable(Log.VERBOSE)) {
            Log.v(tag, msg, cause);
        }
    }

    public static void d(String tag, String msg) {
        if (isLogEnable(Log.DEBUG)) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable cause) {
        if (isLogEnable(Log.DEBUG)) {
            Log.d(tag, msg, cause);
        }
    }

    public static void i(String tag, String msg) {
        if (isLogEnable(Log.INFO)) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable cause) {
        if (isLogEnable(Log.INFO)) {
            Log.i(tag, msg, cause);
        }
    }

    public static void w(String tag, String msg) {
        if (isLogEnable(Log.WARN)) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable cause) {
        if (isLogEnable(Log.WARN)) {
            Log.w(tag, msg, cause);
        }
    }

    public static void e(String tag, String msg) {
        if (isLogEnable(Log.ERROR)) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable cause) {
        if (isLogEnable(Log.ERROR)) {
            Log.e(tag, msg, cause);
        }
    }

    private static boolean isLogEnable(int level) {
        return DEBUG && LOG_LEVEL <= level;
    }
}
