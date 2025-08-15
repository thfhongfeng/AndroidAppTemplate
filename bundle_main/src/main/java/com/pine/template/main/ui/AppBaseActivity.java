package com.pine.template.main.ui;

import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.databinding.ViewDataBinding;

import com.pine.template.base.architecture.mvvm.ui.activity.BaseMvvmFullScreenActivity;
import com.pine.template.base.helper.WakeLockHelper;
import com.pine.template.base.manager.tts.TtsManager;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.util.LogUtils;

public abstract class AppBaseActivity<T extends ViewDataBinding, VM extends ViewModel>
        extends BaseMvvmFullScreenActivity<T, VM> {

    public abstract String makeUiName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private volatile boolean mEnableScreenSleep;

    protected void canScreenSleep() {
        mEnableScreenSleep = true;
    }

    private volatile boolean mIsUiActive = false;

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        synchronized (this) {
            mIsUiActive = true;
        }
        LogUtils.d(TAG, "onStart");
        if (mEnableScreenSleep) {
            WakeLockHelper.getInstance().releaseLock();
        } else {
            WakeLockHelper.getInstance().acquireWakeLock();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d(TAG, "onStop");
        synchronized (this) {
            mIsUiActive = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "onDestroy");
    }

    public synchronized boolean isUiActive() {
        return mIsUiActive;
    }

    public long getLastSleepTime() {
        return WakeLockHelper.getInstance().getLastSleepTime();
    }

    public long getLastWakeupTime() {
        return WakeLockHelper.getInstance().getLastWakeupTime();
    }

    protected int getLoadingUiResId() {
        return super.getLoadingUiResId();
    }

    public void tts(String msg, boolean immediately) {
        TtsManager.getInstance().play(msg, immediately);
    }

    public void tts(@StringRes int resId, boolean immediately) {
        TtsManager.getInstance().play(resId, immediately);
    }

    public void tts(@StringRes int resId, boolean immediately, Integer... formatArgs) {
        Object[] args = new Object[formatArgs.length];
        for (int i = 0; i < formatArgs.length; i++) {
            Object idObj = formatArgs[i];
            args[i] = getString((int) idObj);
        }
        tts(resId, immediately, args);
    }

    public void tts(@StringRes int resId, boolean immediately, Object... formatArgs) {
        TtsManager.getInstance().play(immediately, resId, formatArgs);
    }
}
