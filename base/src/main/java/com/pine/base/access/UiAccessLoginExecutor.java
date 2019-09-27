package com.pine.base.access;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.pine.base.BaseApplication;
import com.pine.base.remote.BaseRouterClient;
import com.pine.router.IRouterCallback;
import com.pine.tool.access.IUiAccessExecutor;
import com.pine.tool.access.UiAccessTimeInterval;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public class UiAccessLoginExecutor implements IUiAccessExecutor {
    public UiAccessLoginExecutor() {
    }

    @Override
    public boolean onExecute(final Activity activity, HashMap<String, String> argsMap, UiAccessTimeInterval accessTimeInterval) {
        boolean canAccess = BaseApplication.isLogin();
        if (!canAccess) {
            if (!doNotGoLoginActivity(argsMap, accessTimeInterval)) {
                BaseRouterClient.goLoginActivity(activity, null, new IRouterCallback() {
                    @Override
                    public void onSuccess(Bundle responseBundle) {

                    }

                    @Override
                    public boolean onFail(int failCode, String errorInfo) {
                        if (activity != null && !activity.isFinishing()) {
                            activity.finish();
                        }
                        return true;
                    }
                });
            }
            if (!doNotFinishActivity(argsMap, accessTimeInterval)) {
                activity.finish();
            }
        }
        return canAccess;
    }

    @Override
    public boolean onExecute(Fragment fragment, HashMap<String, String> argsMap, UiAccessTimeInterval accessTimeInterval) {
        return true;
    }

    private boolean doNotFinishActivity(HashMap<String, String> argsMap, UiAccessTimeInterval accessTimeInterval) {
        return accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_CREATE &&
                argsMap.containsKey(UiAccessArgs.LOGIN_ACCESS_FALSE_ON_CREATE_NOT_FINISH_UI) ||
                accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_NEW_INTENT &&
                        argsMap.containsKey(UiAccessArgs.LOGIN_ACCESS_FALSE_ON_NEW_INTENT_NOT_FINISH_UI) ||
                accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_RESUME &&
                        argsMap.containsKey(UiAccessArgs.LOGIN_ACCESS_FALSE_ON_RESUME_NOT_FINISH_UI);
    }

    private boolean doNotGoLoginActivity(HashMap<String, String> argsMap, UiAccessTimeInterval accessTimeInterval) {
        return accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_CREATE &&
                argsMap.containsKey(UiAccessArgs.LOGIN_ACCESS_FALSE_ON_CREATE_NOT_GO_LOGIN) ||
                accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_NEW_INTENT &&
                        argsMap.containsKey(UiAccessArgs.LOGIN_ACCESS_FALSE_ON_NEW_INTENT_NOT_GO_LOGIN) ||
                accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_RESUME &&
                        argsMap.containsKey(UiAccessArgs.LOGIN_ACCESS_FALSE_ON_RESUME_NOT_GO_LOGIN);
    }
}
