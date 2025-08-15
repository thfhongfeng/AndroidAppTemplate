package com.pine.template.main.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import com.pine.app.template.bundle_main.BuildConfigKey;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.util.LogUtils;

public abstract class AutoFinishActivity<T extends ViewDataBinding, VM extends ViewModel>
        extends AppBaseActivity<T, VM> {
    private final int REQUEST_AUTO_FINISH = 56789;
    private final int RESULT_AUTO_FINISH = 98765;

    private final String AUTO_FINISH_LISTENER_TAG = "auto_finish_ui";

    private Handler mAutoFinishHandler = new Handler(Looper.getMainLooper());

    private int mAutoFinishTime = ConfigSwitcherServer
            .getConfigInt(BuildConfigKey.CONFIG_AUTO_FINISH_SETTINGS_UI_DELAY);

    @Override
    public void startActivity(Intent intent) {
        startActivityForResult(intent, REQUEST_AUTO_FINISH);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        startActivityForResult(intent, REQUEST_AUTO_FINISH, options);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAutoFinishTime = ConfigSwitcherServer
                .getConfigInt(BuildConfigKey.CONFIG_AUTO_FINISH_SETTINGS_UI_DELAY);
        if (mAutoFinishTime > 0) {
            if (resultCode == RESULT_AUTO_FINISH) {
                mAutoFinishHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setResult(RESULT_AUTO_FINISH);
                        finish();
                    }
                }, 1000);
            }
        }
    }

    private IOnUserNoOperateListener mOnUserNoOperateListener = new IOnUserNoOperateListener() {
        @Override
        public void OnUserNoOperate(long idleTime) {
            LogUtils.d(TAG, "OnUserNoOperate idleTime:" + idleTime);
            unListenUserNoOperate(AUTO_FINISH_LISTENER_TAG);
            setResult(RESULT_AUTO_FINISH);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAutoFinishTime = ConfigSwitcherServer
                .getConfigInt(BuildConfigKey.CONFIG_AUTO_FINISH_SETTINGS_UI_DELAY);
        listenUserNoOperate(AUTO_FINISH_LISTENER_TAG, mOnUserNoOperateListener, mAutoFinishTime);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAutoFinishHandler.removeCallbacksAndMessages(null);
        unListenUserNoOperate(AUTO_FINISH_LISTENER_TAG);
    }

    protected void resetAutoFinishJob() {
        mAutoFinishHandler.removeCallbacksAndMessages(null);
        unListenUserNoOperate(AUTO_FINISH_LISTENER_TAG);
        mAutoFinishTime = ConfigSwitcherServer
                .getConfigInt(BuildConfigKey.CONFIG_AUTO_FINISH_SETTINGS_UI_DELAY);
        listenUserNoOperate(AUTO_FINISH_LISTENER_TAG, mOnUserNoOperateListener, mAutoFinishTime);
    }
}
