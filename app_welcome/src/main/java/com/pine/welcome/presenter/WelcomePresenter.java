package com.pine.welcome.presenter;

import android.os.Bundle;
import android.os.Handler;

import com.pine.router.IRouterCallback;
import com.pine.router.command.RouterMainCommand;
import com.pine.tool.architecture.mvp.presenter.Presenter;
import com.pine.tool.util.LogUtils;
import com.pine.welcome.contract.IWelcomeContract;
import com.pine.welcome.remote.WelcomeRouterClient;

/**
 * Created by tanghongfeng on 2018/9/12
 */

public class WelcomePresenter extends Presenter<IWelcomeContract.Ui> implements IWelcomeContract.Presenter {

    public WelcomePresenter() {

    }

    @Override
    public void goMainHomeActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                WelcomeRouterClient.goMainHomeActivity(getContext(), null, new IRouterCallback() {
                    @Override
                    public void onSuccess(Bundle responseBundle) {
                        LogUtils.d(TAG, "onSuccess " + RouterMainCommand.goMainHomeActivity);
                        finishUi();
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
}
