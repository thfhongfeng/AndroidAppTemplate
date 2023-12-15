package com.pine.template.base.manager.tts;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.util.Locale;

public class TtsManager {
    private final String TAG = "TtsManager";

    private static TtsManager mInstance;
    private ITtsManager mProxy;

    private TtsManager() {
        mProxy = new DefaultTtsManager();
    }

    public static TtsManager getInstance() {
        if (mInstance == null) {
            synchronized (TtsManager.class) {
                if (mInstance == null) {
                    mInstance = new TtsManager();
                }
            }
        }
        return mInstance;
    }

    private Context mContext;
    private Locale mLocale;
    private Resources mResources;

    public void init(Context context) {
        init(context, null, Locale.SIMPLIFIED_CHINESE);
    }

    public void init(Context context, ITtsManager poxy) {
        init(context, poxy, Locale.SIMPLIFIED_CHINESE);
    }

    public void init(Context context, @NonNull final Locale locale) {
        init(context, null, locale);
    }

    public void init(Context context, ITtsManager poxy, @NonNull final Locale locale) {
        mContext = context;
        mLocale = locale;
        mResources = null;
        if (poxy != null) {
            mProxy = poxy;
        } else {
            mProxy = new DefaultTtsManager();
        }
        mProxy.init(context, locale);
    }

    public void stop() {
        mProxy.stop();
    }

    public void shutDown() {
        mProxy.shutDown();
    }

    public boolean play(String msg) {
        return play(msg, false, null);
    }

    public boolean play(String msg, boolean immediately) {
        return play(msg, immediately, null);
    }

    public boolean play(String msg, final ITtsManager.TtsPlayProgress listener) {
        return play(msg, false, listener);
    }

    public boolean play(String msg, boolean immediately, final ITtsManager.TtsPlayProgress listener) {
        return mProxy.play("", msg, immediately, listener);
    }

    public boolean play(String tag, String msg) {
        return play(tag, msg, false, null);
    }

    public boolean play(String tag, String msg, boolean immediately) {
        return play(tag, msg, immediately, null);
    }

    public boolean play(String tag, String msg, final ITtsManager.TtsPlayProgress listener) {
        return play(tag, msg, false, listener);
    }

    public boolean play(String tag, String msg, boolean immediately, final ITtsManager.TtsPlayProgress listener) {
        return mProxy.play(tag, msg, immediately, listener);
    }

    public boolean play(@StringRes int resId) {
        return play(getString(resId), false, null);
    }

    public boolean play(@StringRes int resId, boolean immediately) {
        return play(getString(resId), immediately, null);
    }

    public boolean play(@StringRes int resId, final ITtsManager.TtsPlayProgress listener) {
        return play(getString(resId), false, listener);
    }

    public boolean play(@StringRes int resId, boolean immediately, final ITtsManager.TtsPlayProgress listener) {
        return mProxy.play("", getString(resId), immediately, listener);
    }

    public boolean play(String tag, @StringRes int resId) {
        return play(tag, getString(resId), false, null);
    }

    public boolean play(String tag, @StringRes int resId, boolean immediately) {
        return play(tag, getString(resId), immediately, null);
    }

    public boolean play(String tag, @StringRes int resId, final ITtsManager.TtsPlayProgress listener) {
        return play(tag, getString(resId), false, listener);
    }

    public boolean play(String tag, @StringRes int resId, boolean immediately, final ITtsManager.TtsPlayProgress listener) {
        return mProxy.play(tag, getString(resId), immediately, listener);
    }

    public boolean play(@StringRes int resId, Object... formatArgs) {
        return play(getString(resId, formatArgs), false, null);
    }

    public boolean play(boolean immediately, @StringRes int resId, Object... formatArgs) {
        return play(getString(resId, formatArgs), immediately, null);
    }

    public boolean play(final ITtsManager.TtsPlayProgress listener,
                        @StringRes int resId, Object... formatArgs) {
        return play(getString(resId, formatArgs), false, listener);
    }

    public boolean play(boolean immediately, final ITtsManager.TtsPlayProgress listener,
                        @StringRes int resId, Object... formatArgs) {
        return mProxy.play("", getString(resId, formatArgs), immediately, listener);
    }

    public boolean play(String tag, @StringRes int resId, Object... formatArgs) {
        return play(tag, getString(resId, formatArgs), false, null);
    }

    public boolean play(String tag, boolean immediately,
                        @StringRes int resId, Object... formatArgs) {
        return play(tag, getString(resId, formatArgs), immediately, null);
    }

    public boolean play(String tag, final ITtsManager.TtsPlayProgress listener,
                        @StringRes int resId, Object... formatArgs) {
        return play(tag, getString(resId, formatArgs), false, listener);
    }

    public boolean play(String tag, boolean immediately, final ITtsManager.TtsPlayProgress listener,
                        @StringRes int resId, Object... formatArgs) {
        return mProxy.play(tag, getString(resId, formatArgs), immediately, listener);
    }

    public Resources getResource() {
        return getLocalizedResources(mContext, mLocale);
    }

    public String getString(@StringRes int resId) {
        return getResource().getString(resId);
    }

    public String getString(@StringRes int resId, Object... formatArgs) {
        return getResource().getString(resId, formatArgs);
    }

    @NonNull
    private synchronized Resources getLocalizedResources(Context context, Locale locale) {
        if (locale != null) {
            if (mResources == null) {
                Configuration conf = context.getResources().getConfiguration();
                conf = new Configuration(conf);
                conf.setLocale(locale);
                Context localizedContext = context.createConfigurationContext(conf);
                mResources = localizedContext.getResources();
            }
            return mResources;
        }
        return mResources;
    }
}
