package com.pine.tool.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.SharePreferenceUtils;

import java.util.Locale;

public class LanguageHelper {
    private final String TAG = this.getClass().getSimpleName();

    private final String LANGUAGE_KEY = "language_local_key";
    private final String LANGUAGE_EFFECTIVE_KEY = "language_local_change_effective_key";

    private static volatile LanguageHelper instance;

    public synchronized static LanguageHelper getInstance() {
        if (instance == null) {
            instance = new LanguageHelper();
        }
        return instance;
    }

    private LanguageHelper() {
    }

    public Context attachBaseContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context);
        } else {
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public Context updateResources(Context context) {
        Resources resources = context.getResources();
        Locale locale = getAppLocal(context);
        if (locale == null) {
            return context;
        }
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLocales(new LocaleList(locale));
        Context contextFinal = context.createConfigurationContext(configuration);
        LanguageHelper.getInstance().setupAppLanguageLocal(contextFinal);
        return contextFinal;
    }

    public void setupAppLanguageLocal(Context context) {
        setLanguageLocal(context, getAppLanguageLocal(context));
    }

    public Locale getAppLocal(Context context) {
        return getLocal(getAppLanguageLocal(context));
    }

    public String getAppLanguageLocal(Context context) {
        String localeValueSuffix = SharePreferenceUtils.readStringFromConfig(context, LANGUAGE_KEY, "");
        return localeValueSuffix;
    }

    public void setAndSaveLanguageLocal(Context context, String localeValueSuffix) {
        setLanguageLocal(context, localeValueSuffix);
        SharePreferenceUtils.saveToConfig(context, LANGUAGE_KEY, localeValueSuffix);
    }

    public boolean setAndSaveLanguageLocal(Context context, String localeValueSuffix,
                                           boolean effectiveImmediately) {
        setLanguageLocal(context, localeValueSuffix);
        SharePreferenceUtils.saveToConfig(context, LANGUAGE_KEY, localeValueSuffix);
        SharePreferenceUtils.saveToConfig(context, LANGUAGE_EFFECTIVE_KEY, effectiveImmediately);
        return effectiveImmediately;
    }

    public void setEffectiveImmediately(Context context, boolean enable) {
        SharePreferenceUtils.saveToConfig(context, LANGUAGE_EFFECTIVE_KEY, enable);
    }

    public boolean getEffectiveImmediately(Context context) {
        return SharePreferenceUtils.readBooleanFromConfig(context, LANGUAGE_EFFECTIVE_KEY, false);
    }

    public boolean shouldRecreateActivity(Context context, String lastLanguage) {
        String languageValue = LanguageHelper.getInstance().getAppLanguageLocal(context);
        boolean effectiveImme = getEffectiveImmediately(context);
        LogUtils.d("ActivityLifecycle", context + " shouldRecreateActivity languageValue:" + languageValue
                + ", lastLanguage:" + lastLanguage + ", effectiveImmediately:" + effectiveImme);
        return effectiveImme && !TextUtils.equals(lastLanguage, languageValue);
    }

    public boolean equalAppLanguageLocal(Locale locale) {
        Locale appLocale = getAppLocal(AppUtils.getApplicationContext());
        if (appLocale == null) {
            return Locale.getDefault().equals(locale);
        } else {
            return appLocale.equals(locale);
        }
    }

    public Locale getLocal(String localeValueSuffix) {
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

    /**
     * 修改语言
     *
     * @param context           上下文
     * @param localeValueSuffix 结构：language-country-variant（如：zh-CN）
     */
    public void setLanguageLocal(Context context, String localeValueSuffix) {
        if (TextUtils.isEmpty(localeValueSuffix)) {
            return;
        }
        Locale locale = getLocal(localeValueSuffix);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);
        LogUtils.d(TAG, "setLanguageLocal suffix：" + localeValueSuffix + ", local:" + locale);
    }
}
