package com.pine.base.access;

import android.app.Activity;
import android.widget.Toast;

import com.pine.base.R;
import com.pine.config.switcher.ConfigSwitcherServer;
import com.pine.tool.access.IUiAccessExecutor;
import com.pine.tool.access.UiAccessTimeInterval;

import java.util.HashMap;

import androidx.fragment.app.Fragment;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public class UiAccessConfigSwitcherExecutor implements IUiAccessExecutor {
    public UiAccessConfigSwitcherExecutor() {

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
            String config_key = arg;
            boolean canAccess = ConfigSwitcherServer.getInstance().isEnable(config_key);
            if (!canAccess) {
                if (doShowToast(actionsMap, accessTimeInterval)) {
                    Toast.makeText(activity, R.string.base_fun_not_open, Toast.LENGTH_SHORT).show();
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
                actionsMap.containsKey(UiAccessAction.CONFIG_SWITCHER_ACCESS_FALSE_ON_CREATE_NOT_FINISH_UI) ||
                accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_NEW_INTENT &&
                        actionsMap.containsKey(UiAccessAction.CONFIG_SWITCHER_ACCESS_FALSE_ON_NEW_INTENT_NOT_FINISH_UI) ||
                accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_RESUME &&
                        actionsMap.containsKey(UiAccessAction.CONFIG_SWITCHER_ACCESS_FALSE_ON_RESUME_NOT_FINISH_UI);
    }

    private boolean doShowToast(HashMap<String, String> actionsMap, UiAccessTimeInterval accessTimeInterval) {
        return accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_CREATE &&
                actionsMap.containsKey(UiAccessAction.CONFIG_SWITCHER_ACCESS_FALSE_ON_CREATE_SHOW_TOAST) ||
                accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_NEW_INTENT &&
                        actionsMap.containsKey(UiAccessAction.CONFIG_SWITCHER_ACCESS_FALSE_ON_NEW_INTENT_SHOW_TOAST) ||
                accessTimeInterval == UiAccessTimeInterval.UI_ACCESS_ON_RESUME &&
                        actionsMap.containsKey(UiAccessAction.CONFIG_SWITCHER_ACCESS_FALSE_ON_RESUME_SHOW_TOAST);
    }
}
