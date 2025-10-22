package com.pine.template.base.manager.tts;

import android.content.Context;

import java.util.Locale;

public interface ITtsManager {
    void init(Context context, Locale locale);

    void stop();

    void shutDown();

    boolean play(TtsEntity ttsEntity, final TtsPlayProgress listener);

    interface TtsPlayProgress {
        void onStart(String utteranceId);

        void onDone(String utteranceId);

        void onCancel(String utteranceId);

        void onFail(String utteranceId);
    }
}
