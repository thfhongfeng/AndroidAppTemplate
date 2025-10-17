package com.pine.template.base.browser.hdmiin;

import android.widget.FrameLayout;

import androidx.annotation.NonNull;

public interface IHdmiInManager {
    boolean initHdmiIn(@NonNull String tag, @NonNull FrameLayout frameView,
                       int left, int top, int width, int height, int scaleType);

    void setOnPlayEventListener(@NonNull String tag, IOnPlayEventListener listener);

    boolean startHdmiIn(@NonNull String tag);

    boolean stopHdmiIn(@NonNull String tag);

    boolean releaseHdmiIn(@NonNull String tag, FrameLayout frameView);

    boolean releaseAllHdmiIn(FrameLayout frameView);
}
