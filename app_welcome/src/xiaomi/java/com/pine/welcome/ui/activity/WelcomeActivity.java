package com.pine.welcome.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.pine.base.architecture.mvvm.ui.activity.BaseMvvmNoActionBarActivity;
import com.pine.base.router.command.RouterMainCommand;
import com.pine.router.IRouterCallback;
import com.pine.tool.util.LogUtils;
import com.pine.welcome.R;
import com.pine.welcome.databinding.WelcomeActivityBinding;
import com.pine.welcome.remote.WelcomeRouterClient;
import com.pine.welcome.vm.WelcomeVm;

public class WelcomeActivity extends BaseMvvmNoActionBarActivity<WelcomeActivityBinding, WelcomeVm> {

    @Override
    public void observeInitLiveData() {

    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.wel_activity_welcome;
    }

    @Override
    protected void init() {
        Toast.makeText(this, "这是小米的客制化", Toast.LENGTH_SHORT).show();
        goMainHomeActivity();
    }

    private void goMainHomeActivity() {
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
        }, 2000);
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }
}
