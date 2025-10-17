package com.pine.template.base.browser.hdmiin.lztek;

import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.pine.template.base.browser.hdmiin.IHdmiInManager;
import com.pine.template.base.browser.hdmiin.IOnPlayEventListener;
import com.pine.template.bundle_base.R;

import java.util.HashMap;
import java.util.Set;

public class LztekHdmiInManager implements IHdmiInManager {
    private final String TAG = this.getClass().getSimpleName();

    private HashMap<String, HdmiPlayerView> hdmiPlayerViewMap = new HashMap<>();
    private HashMap<String, LinearLayout> containerViewMap = new HashMap<>();
    private HashMap<String, IOnPlayEventListener> mOnPlayEventListenerMap = new HashMap<>();

    @Override
    public boolean initHdmiIn(@NonNull String tag, @NonNull FrameLayout frameView,
                              int left, int top, int width, int height, int scaleType) {
        HdmiPlayerView hdmiPlayerView = new HdmiPlayerView(frameView.getContext());
        hdmiPlayerView.init(scaleType);

        LinearLayout containerView = new LinearLayout(frameView.getContext());
        containerView.setOrientation(LinearLayout.VERTICAL);
        containerView.setBackgroundResource(R.color.black);
        LinearLayout.LayoutParams containerLayoutParams = new LinearLayout.LayoutParams(width, height);
        containerLayoutParams.gravity = Gravity.CENTER;
        containerView.setGravity(Gravity.CENTER);
        containerView.addView(hdmiPlayerView, containerLayoutParams);

        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(width, height);
        frameLayoutParams.leftMargin = left;
        frameLayoutParams.topMargin = top;
        frameView.addView(containerView, frameLayoutParams);

        hdmiPlayerViewMap.put(tag, hdmiPlayerView);
        containerViewMap.put(tag, containerView);
        return true;
    }

    @Override
    public void setOnPlayEventListener(@NonNull String tag, IOnPlayEventListener listener) {
        if (listener == null) {
            return;
        }
        HdmiPlayerView hdmiPlayerView = hdmiPlayerViewMap.get(tag);
        if (hdmiPlayerView != null) {
            hdmiPlayerView.setOnPlayEventListener(new HdmiPlayerView.OnPlayEventListener() {
                @Override
                public void onSurfacePrepared(HdmiPlayerView playerView) {
                    listener.onSurfacePrepared();
                }

                @Override
                public void onHdmiStart(HdmiPlayerView playerView) {
                    listener.onHdmiInStart();
                }

                @Override
                public void onHdmiError(HdmiPlayerView playerView) {
                    listener.onHdmiInError();
                }
            });
            mOnPlayEventListenerMap.put(tag, listener);
        }
    }

    @Override
    public boolean startHdmiIn(@NonNull String tag) {
        HdmiPlayerView hdmiPlayerView = hdmiPlayerViewMap.get(tag);
        if (hdmiPlayerView != null) {
            hdmiPlayerView.startPlay();
            return true;
        }
        return false;
    }

    @Override
    public boolean stopHdmiIn(@NonNull String tag) {
        HdmiPlayerView hdmiPlayerView = hdmiPlayerViewMap.get(tag);
        if (hdmiPlayerView != null) {
            hdmiPlayerView.stopPlay();
            return true;
        }
        return false;
    }

    @Override
    public boolean releaseHdmiIn(@NonNull String tag, FrameLayout frameView) {
        HdmiPlayerView hdmiPlayerView = hdmiPlayerViewMap.get(tag);
        if (hdmiPlayerView != null) {
            hdmiPlayerView.release();
            hdmiPlayerViewMap.remove(hdmiPlayerView);
            ViewGroup containerView = containerViewMap.get(tag);
            if (containerView != null) {
                frameView.removeView(containerView);
                containerViewMap.remove(containerView);
            }
        }
        return true;
    }

    @Override
    public boolean releaseAllHdmiIn(FrameLayout frameView) {
        Set<String> keySet = hdmiPlayerViewMap.keySet();
        for (String key : keySet) {
            releaseHdmiIn(key, frameView);
        }
        return true;
    }
}
