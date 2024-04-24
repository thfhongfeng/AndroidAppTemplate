package com.pine.template.base.manager;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

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
    private Locale mLocale = Locale.SIMPLIFIED_CHINESE;
    private HashMap<String, Resources> mResourceMap = new HashMap();

    public Resources getResource() {
        return getResource(mLocale);
    }

    public Resources getResource(Locale locale) {
        return getLocalizedResources(mContext, locale);
    }

    public String getString(@StringRes int resId) {
        return getString(mLocale, resId);
    }

    public String getString(Locale locale, @StringRes int resId) {
        return getResource(locale).getString(resId);
    }

    public String getString(@StringRes int resId, Object... formatArgs) {
        return getString(mLocale, resId, formatArgs);
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
