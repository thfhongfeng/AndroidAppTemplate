package com.pine.template.welcome.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.pine.template.base.architecture.mvvm.ui.activity.BaseMvvmNoActionBarActivity;
import com.pine.template.base.router.command.RouterMainCommand;
import com.pine.tool.router.IRouterCallback;
import com.pine.tool.util.LogUtils;
import com.pine.template.welcome.R;
import com.pine.template.welcome.databinding.WelcomeActivityBinding;
import com.pine.template.welcome.remote.WelcomeRouterClient;
import com.pine.template.welcome.vm.WelcomeVm;

public class WelcomeActivity extends BaseMvvmNoActionBarActivity<WelcomeActivityBinding, WelcomeVm> {
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
        Toast.makeText(this, "这是小米的客制化", Toast.LENGTH_SHORT).show();
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
