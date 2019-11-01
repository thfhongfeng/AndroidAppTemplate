package com.pine.tool.router;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public interface IRouterManagerFactory {
    IRouterManager makeRouterManager(String bundleKey);

    boolean isBundleEnable(String bundleKey);
}
