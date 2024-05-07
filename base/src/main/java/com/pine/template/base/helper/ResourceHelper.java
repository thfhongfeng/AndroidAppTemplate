package com.pine.template.base.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;

import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

import java.util.Locale;

public class ResourceHelper {
    private static final String TAG = ResourceHelper.class.getSimpleName();

    private static Resources mFirstResources, mSecondResources;

    private static String mFirstLocal = "zh_CN", mSecondLocal = "zh_CN";

    public static void setup(String firstLocal, String secondLocal) {
        mFirstLocal = firstLocal;
        mSecondLocal = secondLocal;
    }

    public static void clear() {
        mFirstResources = null;
        mSecondResources = null;
    }

    public static String getFirstString(int resId) {
        if (resId <= 0) {
            return "";
        }
        return getFirstResources().getString(resId);
    }

    public static String getSecondString(int resId) {
        if (resId <= 0) {
            return "";
        }
        return getSecondResources().getString(resId);
    }

    public static String getFirstString(int resId, Object... args) {
        if (resId <= 0) {
            return "";
        }
        return getFirstResources().getString(resId, args);
    }

    public static String getSecondString(int resId, Object... args) {
        if (resId <= 0) {
            return "";
        }
        return getSecondResources().getString(resId, args);
    }

    private synchronized static Resources getFirstResources() {
        if (mFirstResources == null) {
            Context context = AppUtils.getApplicationContext();
            Locale locale = getLocal(mFirstLocal);
            Configuration conf = context.getResources().getConfiguration();
            conf = new Configuration(conf);
            conf.setLocale(locale);
            Context localizedContext = context.createConfigurationContext(conf);
            mFirstResources = localizedContext.getResources();
            LogUtils.d(TAG, "create first resources locale:" + locale);
        }
        return mFirstResources;
    }

    private synchronized static Resources getSecondResources() {
        if (mSecondResources == null) {
            Context context = AppUtils.getApplicationContext();
            Locale locale = getLocal(mSecondLocal);
            Configuration conf = context.getResources().getConfiguration();
            conf = new Configuration(conf);
            conf.setLocale(locale);
            Context localizedContext = context.createConfigurationContext(conf);
            mSecondResources = localizedContext.getResources();
            LogUtils.d(TAG, "create second resources locale:" + locale);
        }
        return mSecondResources;
    }

    public static Locale getLocal(String localeValueSuffix) {
        if (TextUtils.isEmpty(localeValueSuffix)) {
            return null;
        }
        String[] suffixArr = localeValueSuffix.split("_");
        Locale locale = Locale.getDefault();
        int l = suffixArr.length;
        switch (l) {
            case 1:
                locale = new Locale(suffixArr[0]);
                break;
            case 2:
                locale = new Locale(suffixArr[0], suffixArr[1]);
                break;
            case 3:
                locale = new Locale(suffixArr[0], suffixArr[1], suffixArr[2]);
                break;
            default:
                break;

        }
        return locale;
    }
}
