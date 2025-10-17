package com.pine.template.base.browser.hdmiin;

import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.pine.template.base.browser.hdmiin.lztek.LztekHdmiInManager;

public class HdmiInManager {
    private final String TAG = this.getClass().getSimpleName();

    private static IHdmiInManager mProxy;
    private static HdmiInManager instance;

    private HdmiInManager() {
        mProxy = new LztekHdmiInManager();
    }

    public synchronized static HdmiInManager getInstance() {
        if (instance == null) {
            instance = new HdmiInManager();
        }
        return instance;
    }

    private synchronized boolean checkProxy() {
        return mProxy != null;
    }

    public boolean initHdmiIn(@NonNull String tag, @NonNull FrameLayout frameView) {
        if (!checkProxy()) {
            return false;
        }
        return mProxy.initHdmiIn(tag, frameView, 0, 0, 0, 0, IHdmiInScaleType.STYLE_FULL);
    }

    public boolean initHdmiIn(@NonNull String tag, @NonNull FrameLayout frameView,
                              int left, int top) {
        if (!checkProxy()) {
            return false;
        }
        return mProxy.initHdmiIn(tag, frameView, left, top, 0, 0, IHdmiInScaleType.STYLE_FULL);
    }

    public boolean initHdmiIn(@NonNull String tag, @NonNull FrameLayout frameView,
                              int left, int top, int width, int height) {
        if (!checkProxy()) {
            return false;
        }
        return mProxy.initHdmiIn(tag, frameView, left, top, width, height, IHdmiInScaleType.STYLE_FULL);
    }

    public boolean initHdmiIn(@NonNull String tag, @NonNull FrameLayout frameView,
                              int left, int top, int width, int height, int scaleType) {
        if (!checkProxy()) {
            return false;
        }
        return mProxy.initHdmiIn(tag, frameView, left, top, width, height, scaleType);
    }

    public void setOnPlayEventListener(@NonNull String tag, IOnPlayEventListener listener) {
        mProxy.setOnPlayEventListener(tag, listener);
    }

    public boolean startHdmiIn(@NonNull String tag) {
        if (!checkProxy()) {
            return false;
        }
        return mProxy.startHdmiIn(tag);
    }

    public boolean stopHdmiIn(@NonNull String tag) {
        if (!checkProxy()) {
            return false;
        }
        return mProxy.stopHdmiIn(tag);
    }

    public boolean releaseHdmiIn(@NonNull String tag, FrameLayout frameView) {
        if (!checkProxy()) {
            return false;
        }
        return mProxy.releaseHdmiIn(tag, frameView);
    }

    public boolean releaseAllHdmiIn(FrameLayout frameView) {
        if (!checkProxy()) {
            return false;
        }
        return mProxy.releaseAllHdmiIn(frameView);
    }
}
