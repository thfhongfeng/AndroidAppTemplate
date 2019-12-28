package com.pine.base.access;

import android.app.Activity;
import android.os.Bundle;

import com.pine.base.bean.AccountBean;
import com.pine.base.remote.BaseRouterClient;
import com.pine.tool.access.IUiAccessExecutor;
import com.pine.tool.access.UiAccessTimeInterval;
import com.pine.tool.router.IRouterCallback;

import java.util.HashMap;

import androidx.fragment.app.Fragment;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public class UiAccessVipLevelExecutor implements IUiAccessExecutor {
    public UiAccessVipLevelExecutor() {

    }

    /**
     * @param activity
     * @param arg                整型字符串
     * @param actionsMap
     * @param accessTimeInterval
     * @return
     */
    @Override
    public boolean onExecute(final Activity activity, String arg, HashMap<String, String> actionsMap,
                             UiAccessTimeInterval accessTimeInterval) {
        try {
            AccountBean accountBean = BaseRouterClient.getLoginAccount(activity, null);
            int accountType = accountBean.getAccountType();
            int vipLevelNeed = Integer.parseInt(arg);
            boolean canAccess = accountType - 9000 >= vipLevelNeed * 10;
            if (!canAccess) {
                if (!doNotGoVipActivity(actionsMap, accessTimeInterval)) {
                    BaseRouterClient.goUserRechargeActivity(activity, null, new IRouterCallback() {
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
        } catch (NumberFormatException nfe) {
            return true;
        }
    }

    /**
     * @param fragment
     * @param arg                整型字符串
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
                actionsMap.containsKey(UiAccessAction.VIP_ACCESS_FALSE_ON_CREATE_NOT_FINISH_UI) ||
                accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_NEW_INTENT &&
                        actionsMap.containsKey(UiAccessAction.VIP_ACCESS_FALSE_ON_NEW_INTENT_NOT_FINISH_UI) ||
                accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_RESUME &&
                        actionsMap.containsKey(UiAccessAction.VIP_ACCESS_FALSE_ON_RESUME_NOT_FINISH_UI);
    }

    private boolean doNotGoVipActivity(HashMap<String, String> actionsMap, UiAccessTimeInterval accessTimeInterval) {
        return accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_CREATE &&
                actionsMap.containsKey(UiAccessAction.VIP_ACCESS_FALSE_ON_CREATE_NOT_GO_VIP_UI) ||
                accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_NEW_INTENT &&
                        actionsMap.containsKey(UiAccessAction.VIP_ACCESS_FALSE_ON_NEW_INTENT_NOT_GO_VIP_UI) ||
                accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_RESUME &&
                        actionsMap.containsKey(UiAccessAction.VIP_ACCESS_FALSE_ON_RESUME_NOT_GO_VIP_UI);
    }
}
