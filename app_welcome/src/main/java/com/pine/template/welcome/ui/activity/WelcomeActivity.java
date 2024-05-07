package com.pine.template.welcome.ui.activity;

import android.os.Bundle;
import android.os.Handler;

import com.pine.app.template.app_welcome.router.RouterMainCommand;
import com.pine.template.base.architecture.mvvm.ui.activity.BaseMvvmFullScreenActivity;
import com.pine.template.welcome.R;
import com.pine.template.welcome.databinding.WelcomeActivityBinding;
import com.pine.template.welcome.remote.WelcomeRouterClient;
import com.pine.template.welcome.vm.WelcomeVm;
import com.pine.tool.router.IRouterCallback;
import com.pine.tool.util.LogUtils;

public class WelcomeActivity extends BaseMvvmFullScreenActivity<WelcomeActivityBinding, WelcomeVm> {
    private final static int WELCOME_STAY_MIN_TIME = 1000;
    private long mStartTimeMillis;

    @Override
    public void observeInitLiveData(Bundle savedInstanceState) {

    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.wel_activity_welcome;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mStartTimeMillis = System.currentTimeMillis();
        goMainHomeActivity();
    }

    private void goMainHomeActivity() {
        long delay = WELCOME_STAY_MIN_TIME - (System.currentTimeMillis() - mStartTimeMillis);
        delay = delay > 0 ? delay : 0;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                WelcomeRouterClient.goMainHomeActivity(WelcomeActivity.this, null, new IRouterCallback() {
                    @Override
                    public void onSuccess(Bundle responseBundle) {
                        LogUtils.d(TAG, "onSuccess " + RouterMainCommand.goMainHomeActivity);
                        finish();
                        return;
                    }

                    @Override
                    public boolean onFail(int failCode, String errorInfo) {
                        return false;
                    }
                });
            }
        }, delay);
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }
}
