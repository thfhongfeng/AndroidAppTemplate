package com.pine.tool.router;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public interface IRouterManagerFactory {
    IRouterManager makeRouterManager();

    boolean isBundleEnable(String bundleKey);
}
