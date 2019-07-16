package com.pine.router.impl;

import com.pine.config.BuildConfig;
import com.pine.router.impl.arouter.manager.ARouterManager;

/**
 * Created by tanghongfeng on 2018/9/12
 */

public class RouterManager {
    public static IRouterManager getInstance(String bundleKey) {
        switch (BuildConfig.APP_THIRD_ROUTER_PROVIDER) {
            case "arouter":
                return ARouterManager.getInstance(bundleKey);
            default:
                return ARouterManager.getInstance(bundleKey);
        }
    }
}
