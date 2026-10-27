package com.pine.template.base.manager;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.pine.tool.util.AppUtils;

import java.util.HashMap;
import java.util.Locale;

public class ResourceManager {
    private final String TAG = "ResourceManager";

    private static ResourceManager mInstance;

    private ResourceManager() {

    }

    public synchronized static ResourceManager getInstance() {
        if (mInstance == null) {
            mInstance = new ResourceManager();
            mContext = AppUtils.getApplicationContext();
        }
        return mInstance;
    }

    private static Context mContext;
    private Locale mDefaultLocale = Locale.SIMPLIFIED_CHINESE;
    private HashMap<String, Resources> mResourceMap = new HashMap();

    public Locale getCurrentAppLocale(Context ctx) {
        Configuration cfg = ctx.getResources().getConfiguration();
        Locale primary;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList list = cfg.getLocales();
            primary = list.isEmpty() ? Locale.getDefault() : list.get(0);
        } else {
            primary = cfg.locale;
        }
        return primary;
    }

    public boolean isChineseLanguage(Context ctx) {
        Locale locale = getCurrentAppLocale(ctx);
        return locale.getLanguage().equals("zh");
    }

    public boolean isEnglishLanguage(Context ctx) {
        Locale locale = getCurrentAppLocale(ctx);
        return locale.getLanguage().equals("en");
    }

    public Resources getResource() {
        return getResource(getCurrentAppLocale(mContext));
    }

    public Resources getResource(Locale locale) {
        return getLocalizedResources(mContext, locale);
    }

    public String getString(@StringRes int resId) {
        return getString(getCurrentAppLocale(mContext), resId);
    }

    public String getString(Locale locale, @StringRes int resId) {
        return getResource(locale).getString(resId);
    }

    public String getString(@StringRes int resId, Object... formatArgs) {
        return getString(getCurrentAppLocale(mContext), resId, formatArgs);
    }

    public String getString(Locale locale, @StringRes int resId, Object... formatArgs) {
        return getResource(locale).getString(resId, formatArgs);
    }

    @NonNull
    public synchronized Resources getLocalizedResources(Context context, Locale locale) {
        if (locale != null) {
            Resources resources = mResourceMap.get(locale.toString());
            if (resources == null) {
                Configuration conf = context.getResources().getConfiguration();
                conf = new Configuration(conf);
                conf.setLocale(locale);
                Context localizedContext = context.createConfigurationContext(conf);
                resources = localizedContext.getResources();
                mResourceMap.put(locale.toString(), resources);
            }
            return resources;
        }
        return null;
    }
}
