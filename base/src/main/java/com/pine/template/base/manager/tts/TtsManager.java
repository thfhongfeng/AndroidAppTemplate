package com.pine.template.base.manager.tts;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.pine.template.base.manager.ResourceManager;
import com.pine.tool.util.AppUtils;

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

    private Locale mLocale = Locale.SIMPLIFIED_CHINESE;
    private volatile boolean mInit;

    public void init(ITtsManager poxy) {
        init(poxy, Locale.SIMPLIFIED_CHINESE);
    }

    public void init(@NonNull final Locale locale) {
        init(null, locale);
    }

    public void init(ITtsManager poxy, @NonNull final Locale locale) {
        mLocale = locale;
        if (poxy != null) {
            mProxy = poxy;
        } else {
            mProxy = new DefaultTtsManager();
        }
        mProxy.init(AppUtils.getApplicationContext(), locale);
        mInit = true;
    }

    private boolean isInit() {
        return mInit;
    }

    public void stop() {
        if (!isInit()) {
            return;
        }
        mProxy.stop();
    }

    public void shutDown() {
        if (!isInit()) {
            return;
        }
        mProxy.shutDown();
    }

    public boolean play(String msg) {
        if (!isInit()) {
            return false;
        }
        return play(msg, false, null);
    }

    public boolean play(String msg, boolean immediately) {
        if (!isInit()) {
            return false;
        }
        return play(msg, immediately, null);
    }

    public boolean play(String msg, final ITtsManager.TtsPlayProgress listener) {
        if (!isInit()) {
            return false;
        }
        return play(msg, false, listener);
    }

    public boolean play(String msg, boolean immediately, final ITtsManager.TtsPlayProgress listener) {
        if (!isInit()) {
            return false;
        }
        return mProxy.play(msg, msg, immediately, listener);
    }

    public boolean play(String utteranceId, String msg) {
        if (!isInit()) {
            return false;
        }
        return play(utteranceId, msg, false, null);
    }

    public boolean play(String utteranceId, String msg, boolean immediately) {
        if (!isInit()) {
            return false;
        }
        return play(utteranceId, msg, immediately, null);
    }

    public boolean play(String utteranceId, String msg, final ITtsManager.TtsPlayProgress listener) {
        if (!isInit()) {
            return false;
        }
        return play(utteranceId, msg, false, listener);
    }

    public boolean play(String utteranceId, String msg, boolean immediately, final ITtsManager.TtsPlayProgress listener) {
        if (!isInit()) {
            return false;
        }
        return mProxy.play(utteranceId, msg, immediately, listener);
    }

    public boolean play(@StringRes int resId) {
        if (!isInit()) {
            return false;
        }
        return play(ResourceManager.getInstance().getString(mLocale, resId), false, null);
    }

    public boolean play(@StringRes int resId, boolean immediately) {
        if (!isInit()) {
            return false;
        }
        return play(ResourceManager.getInstance().getString(mLocale, resId), immediately, null);
    }

    public boolean play(@StringRes int resId, final ITtsManager.TtsPlayProgress listener) {
        if (!isInit()) {
            return false;
        }
        return play(ResourceManager.getInstance().getString(mLocale, resId), false, listener);
    }

    public boolean play(@StringRes int resId, boolean immediately, final ITtsManager.TtsPlayProgress listener) {
        if (!isInit()) {
            return false;
        }
        String msg = ResourceManager.getInstance().getString(mLocale, resId);
        return mProxy.play(msg, msg, immediately, listener);
    }

    public boolean play(String utteranceId, @StringRes int resId) {
        if (!isInit()) {
            return false;
        }
        return play(utteranceId, ResourceManager.getInstance().getString(mLocale, resId), false, null);
    }

    public boolean play(String utteranceId, @StringRes int resId, boolean immediately) {
        if (!isInit()) {
            return false;
        }
        return play(utteranceId, ResourceManager.getInstance().getString(mLocale, resId), immediately, null);
    }

    public boolean play(String utteranceId, @StringRes int resId, final ITtsManager.TtsPlayProgress listener) {
        if (!isInit()) {
            return false;
        }
        return play(utteranceId, ResourceManager.getInstance().getString(mLocale, resId), false, listener);
    }

    public boolean play(String utteranceId, @StringRes int resId, boolean immediately, final ITtsManager.TtsPlayProgress listener) {
        if (!isInit()) {
            return false;
        }
        return mProxy.play(utteranceId, ResourceManager.getInstance().getString(mLocale, resId), immediately, listener);
    }

    public boolean play(@StringRes int resId, Object... formatArgs) {
        if (!isInit()) {
            return false;
        }
        return play(ResourceManager.getInstance().getString(mLocale, resId, formatArgs), false, null);
    }

    public boolean play(boolean immediately, @StringRes int resId, Object... formatArgs) {
        return play(ResourceManager.getInstance().getString(mLocale, resId, formatArgs), immediately, null);
    }

    public boolean play(final ITtsManager.TtsPlayProgress listener,
                        @StringRes int resId, Object... formatArgs) {
        if (!isInit()) {
            return false;
        }
        return play(ResourceManager.getInstance().getString(mLocale, resId, formatArgs), false, listener);
    }

    public boolean play(boolean immediately, final ITtsManager.TtsPlayProgress listener,
                        @StringRes int resId, Object... formatArgs) {
        if (!isInit()) {
            return false;
        }
        String msg = ResourceManager.getInstance().getString(mLocale, resId, formatArgs);
        return mProxy.play(msg, msg, immediately, listener);
    }

    public boolean play(String utteranceId, @StringRes int resId, Object... formatArgs) {
        if (!isInit()) {
            return false;
        }
        return play(utteranceId, ResourceManager.getInstance().getString(mLocale, resId, formatArgs), false, null);
    }

    public boolean play(String utteranceId, boolean immediately,
                        @StringRes int resId, Object... formatArgs) {
        if (!isInit()) {
            return false;
        }
        return play(utteranceId, ResourceManager.getInstance().getString(mLocale, resId, formatArgs), immediately, null);
    }

    public boolean play(String utteranceId, final ITtsManager.TtsPlayProgress listener,
                        @StringRes int resId, Object... formatArgs) {
        if (!isInit()) {
            return false;
        }
        return play(utteranceId, ResourceManager.getInstance().getString(mLocale, resId, formatArgs), false, listener);
    }

    public boolean play(String utteranceId, boolean immediately, final ITtsManager.TtsPlayProgress listener,
                        @StringRes int resId, Object... formatArgs) {
        if (!isInit()) {
            return false;
        }
        return mProxy.play(utteranceId, ResourceManager.getInstance().getString(mLocale, resId, formatArgs), immediately, listener);
    }
}
