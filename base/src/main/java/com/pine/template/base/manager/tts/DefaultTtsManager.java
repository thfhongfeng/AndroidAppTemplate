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
                LogUtils.d(TAG, "onStart utteranceId:" + utteranceId);
            }

            @Override
            public void onDone(final String utteranceId) {
                LogUtils.d(TAG, "onDone utteranceId:" + utteranceId);
                mMainHandler.post(new Runnable() {
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
                });
            }

            @Override
            public void onError(final String utteranceId) {
                LogUtils.d(TAG, "onError utteranceId:" + utteranceId);
                mMainHandler.post(new Runnable() {
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
                });
            }
        });
    }

    private boolean check(TtsEntity ttsEntity, final TtsPlayProgress listener) {
        if (mLocale == null || !mIsSupport || ttsEntity == null || !ttsEntity.isValid()) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.onFail();
                    }
                }
            });
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
        mMainHandler.removeCallbacksAndMessages(null);
        mSpeech.shutdown();
    }

    @Override
    public boolean play(TtsEntity ttsEntity, TtsPlayProgress listener) {
        LogUtils.d(TAG, "play TTS:" + ttsEntity + ",listener:" + listener
                + ",mLocale:" + mLocale + ",mIsSupport:" + mIsSupport);
        if (!check(ttsEntity, listener)) {
            return false;
        }
        if (listener != null) {
            synchronized (mListenerMap) {
                mListenerMap.put(ttsEntity.getUtteranceId(), listener);
            }
        }
        int queueMode = ttsEntity.isImmediately() ? TextToSpeech.QUEUE_FLUSH : TextToSpeech.QUEUE_ADD;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle params = new Bundle();
            params.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_NOTIFICATION);
            mSpeech.speak(ttsEntity.getMsg(), queueMode, params, ttsEntity.getUtteranceId());
        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_NOTIFICATION));
            mSpeech.speak(ttsEntity.getMsg(), queueMode, params);
        }
        return true;
    }
}
