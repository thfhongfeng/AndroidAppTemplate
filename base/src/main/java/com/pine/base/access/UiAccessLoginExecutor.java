package com.pine.base.access;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.pine.base.BaseApplication;
import com.pine.base.remote.BaseRouterClient;
import com.pine.tool.access.IUiAccessExecutor;
import com.pine.tool.access.UiAccessTimeInterval;
import com.pine.tool.router.IRouterCallback;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public class UiAccessLoginExecutor implements IUiAccessExecutor {
    public UiAccessLoginExecutor() {
    }

    /**
     * @param activity
     * @param arg                传空即可
     * @param actionsMap
     * @param accessTimeInterval
     * @return
     */
    @Override
    public boolean onExecute(final Activity activity, String arg, HashMap<String, String> actionsMap,
                             UiAccessTimeInterval accessTimeInterval) {
        boolean canAccess = BaseApplication.isLogin();
        if (!canAccess) {
            if (!doNotGoLoginActivity(actionsMap, accessTimeInterval)) {
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
            if (!doNotFinishActivity(actionsMap, accessTimeInterval)) {
                activity.finish();
            }
        }
        return canAccess;
    }

    /**
     * @param fragment
     * @param arg                传空即可
     * @param actionsMap
     * @param accessTimeInterval
     * @return
     */
    @Override
    public boolean onExecute(Fragment fragment, String arg, HashMap<String, String> actionsMap,
                             UiAccessTimeInterval accessTimeInterval) {
        return true;
    }

    private boolean doNotFinishActivity(HashMap<String, String> actionsMap, UiAccessTimeInterval accessTimeInterval) {
        return accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_CREATE &&
                actionsMap.containsKey(UiAccessAction.LOGIN_ACCESS_FALSE_ON_CREATE_NOT_FINISH_UI) ||
                accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_NEW_INTENT &&
                        actionsMap.containsKey(UiAccessAction.LOGIN_ACCESS_FALSE_ON_NEW_INTENT_NOT_FINISH_UI) ||
                accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_RESUME &&
                        actionsMap.containsKey(UiAccessAction.LOGIN_ACCESS_FALSE_ON_RESUME_NOT_FINISH_UI);
    }

    private boolean doNotGoLoginActivity(HashMap<String, String> actionsMap, UiAccessTimeInterval accessTimeInterval) {
        return accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_CREATE &&
                actionsMap.containsKey(UiAccessAction.LOGIN_ACCESS_FALSE_ON_CREATE_NOT_GO_LOGIN) ||
                accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_NEW_INTENT &&
                        actionsMap.containsKey(UiAccessAction.LOGIN_ACCESS_FALSE_ON_NEW_INTENT_NOT_GO_LOGIN) ||
                accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_RESUME &&
                        actionsMap.containsKey(UiAccessAction.LOGIN_ACCESS_FALSE_ON_RESUME_NOT_GO_LOGIN);
    }
}
