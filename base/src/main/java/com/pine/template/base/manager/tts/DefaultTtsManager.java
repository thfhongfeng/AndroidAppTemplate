package com.pine.template.base.manager.tts;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.pine.tool.util.LogUtils;

import java.util.HashMap;
import java.util.Locale;

public class DefaultTtsManager implements ITtsManager {
    private final String TAG = this.getClass().getSimpleName();

    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private TextToSpeech mSpeech;
    private boolean mIsSupport;
    private Locale mLocale;

    private HashMap<String, TtsPlayProgress> mListenerMap = new HashMap<>();

    @Override
    public void init(Context context, final Locale locale) {
        LogUtils.d(TAG, "init called");
        mSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                LogUtils.d(TAG, "onInit status:" + status);
                if (status == TextToSpeech.SUCCESS) {
                    int result = mSpeech.setLanguage(locale);
                    mSpeech.setPitch(1.0f);
                    mSpeech.setSpeechRate(1.0f);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        LogUtils.d(TAG, "init language not support");
                        mIsSupport = false;
                    } else {
                        LogUtils.d(TAG, "init success");
                        mIsSupport = true;
                        mLocale = locale;
                    }
                }
            }
        });
        mSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(final String utteranceId) {
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TtsPlayProgress listener = null;
                        synchronized (mListenerMap) {
                            if (mListenerMap.containsKey(utteranceId)) {
                                listener = mListenerMap.remove(utteranceId);
                            }
                        }
                        if (listener != null) {
                            listener.onDone();
                        }
                    }
                }, 1000);
            }

            @Override
            public void onError(final String utteranceId) {
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TtsPlayProgress listener = null;
                        synchronized (mListenerMap) {
                            if (mListenerMap.containsKey(utteranceId)) {
                                listener = mListenerMap.remove(utteranceId);
                            }
                        }
                        if (listener != null) {
                            listener.onFail();
                        }
                    }
                }, 500);
            }
        });
    }

    private boolean check(final TtsPlayProgress listener) {
        if (mLocale == null || !mIsSupport) {
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.onFail();
                    }
                }
            }, 500);
            return false;
        }
        return true;
    }

    @Override
    public void stop() {
        mSpeech.stop();
    }

    @Override
    public void shutDown() {
        mSpeech.shutdown();
    }

    @Override
    public boolean play(String utteranceId, String msg, boolean immediately, final TtsPlayProgress listener) {
        LogUtils.d(TAG, "play TTS utteranceId:" + utteranceId + ",msg:" + msg
                + ",immediately:" + immediately + ",listener:" + listener
                + ",mLocale:" + mLocale + ",mIsSupport:" + mIsSupport);
        if (!check(listener)) {
            return false;
        }
        if (listener != null) {
            synchronized (mListenerMap) {
                mListenerMap.put(utteranceId, listener);
            }
        }
        int queueMode = immediately ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle params = new Bundle();
            params.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_NOTIFICATION);
            mSpeech.speak(msg, queueMode, params, utteranceId);

        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_NOTIFICATION));
            mSpeech.speak(msg, queueMode, params);
        }
        return true;
    }
}
