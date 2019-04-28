package com.pine.router.impl.arouter;

import com.pine.config.ConfigBundleKey;
import com.pine.router.impl.IRouterManager;
import com.pine.router.impl.arouter.manager.ARouterBusinessDemoManager;
import com.pine.router.impl.arouter.manager.ARouterBusinessMvcManager;
import com.pine.router.impl.arouter.manager.ARouterBusinessMvpManager;
import com.pine.router.impl.arouter.manager.ARouterBusinessMvvmManager;
import com.pine.router.impl.arouter.manager.ARouterDbServerManager;
import com.pine.router.impl.arouter.manager.ARouterLoginManager;
import com.pine.router.impl.arouter.manager.ARouterMainManager;
import com.pine.router.impl.arouter.manager.ARouterUserManager;

/**
 * Created by tanghongfeng on 2019/1/14
 */

public class ARouterManagerFactory {

    public static IRouterManager getManager(String bundleKey) {
        switch (bundleKey) {
            case ConfigBundleKey.LOGIN_BUNDLE_KEY:
                return getLoginRouter();
            case ConfigBundleKey.MAIN_BUNDLE_KEY:
                return getMainRouter();
            case ConfigBundleKey.USER_BUNDLE_KEY:
                return getUserRouter();
            case ConfigBundleKey.BUSINESS_MVC_BUNDLE_KEY:
                return getBusinessMvcRouter();
            case ConfigBundleKey.BUSINESS_MVP_BUNDLE_KEY:
                return getBusinessMvpRouter();
            case ConfigBundleKey.BUSINESS_MVVM_BUNDLE_KEY:
                return getBusinessMvvmRouter();
            case ConfigBundleKey.BUSINESS_DEMO_BUNDLE_KEY:
                return getBusinessDemoRouter();
            case ConfigBundleKey.DB_SEVER_BUNDLE_KEY:
                return getDbServerRouter();
            default:
                return null;
        }
    }

    public static IRouterManager getLoginRouter() {
        return ARouterLoginManager.getInstance();
    }

    public static IRouterManager getMainRouter() {
        return ARouterMainManager.getInstance();
    }

    public static IRouterManager getUserRouter() {
        return ARouterUserManager.getInstance();
    }

    public static IRouterManager getBusinessMvcRouter() {
        return ARouterBusinessMvcManager.getInstance();
    }

    public static IRouterManager getBusinessMvpRouter() {
        return ARouterBusinessMvpManager.getInstance();
    }

    public static IRouterManager getBusinessMvvmRouter() {
        return ARouterBusinessMvvmManager.getInstance();
    }

    public static IRouterManager getBusinessDemoRouter() {
        return ARouterBusinessDemoManager.getInstance();
    }

    public static IRouterManager getDbServerRouter() {
        return ARouterDbServerManager.getInstance();
    }
}
