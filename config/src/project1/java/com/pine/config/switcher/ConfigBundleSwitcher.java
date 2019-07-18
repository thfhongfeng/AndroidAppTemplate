package com.pine.config.switcher;

import com.pine.config.ConfigBundleKey;
import com.pine.tool.util.LogUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class ConfigBundleSwitcher {
    private static final String TAG = LogUtils.makeLogTag(ConfigBundleSwitcher.class);

    private static Map<String, Boolean> mBundleStateMap = new HashMap();

    static {
        setBundleState(ConfigKey.BUNDLE_WELCOME_KEY, true);
        setBundleState(ConfigBundleKey.BUNDLE_LOGIN_KEY, true);
        setBundleState(ConfigBundleKey.BUNDLE_MAIN_KEY, true);
        setBundleState(ConfigBundleKey.BUNDLE_USER_KEY, true);
    }

    public static void setBundleState(String key, boolean isOpen) {
        mBundleStateMap.put(key, isOpen);
        LogUtils.releaseLog(TAG, "Set " + key + " bundle " + (isOpen ? "open" : "close"));
    }

    public static boolean isBundleOpen(String key) {
        return mBundleStateMap.containsKey(key) && mBundleStateMap.get(key);
    }
}
