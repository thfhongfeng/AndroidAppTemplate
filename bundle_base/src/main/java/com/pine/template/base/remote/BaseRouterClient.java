package com.pine.template.base.remote;

import android.content.Context;
import android.os.Bundle;

import com.pine.app.template.bundle_base.BuildConfigKey;
import com.pine.app.template.bundle_base.router.RouterLoginCommand;
import com.pine.app.template.bundle_base.router.RouterMainCommand;
import com.pine.app.template.bundle_base.router.RouterUserCommand;
import com.pine.template.base.business.bean.AccountBean;
import com.pine.tool.router.IRouterCallback;
import com.pine.tool.router.RouterException;
import com.pine.tool.router.RouterManager;

public class BaseRouterClient {

    public static void autoLogin(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.callOpCommand(context, BuildConfigKey.BUNDLE_LOGIN,
                RouterLoginCommand.autoLogin, args, callback);
    }

    public static void goLoginActivity(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.callUiCommand(context, BuildConfigKey.BUNDLE_LOGIN,
                RouterLoginCommand.goLoginActivity, args, callback);
    }

    public static AccountBean getLoginAccount(Context context, Bundle args) {
        try {
            return RouterManager.callDataCommandDirect(context, BuildConfigKey.BUNDLE_LOGIN,
                    RouterLoginCommand.getLoginAccount, args);
        } catch (RouterException e) {
            return null;
        }
    }

    public static void goMainHomeActivity(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.callUiCommand(context, BuildConfigKey.BUNDLE_MAIN,
                RouterMainCommand.goMainHomeActivity, args, callback);
    }

    public static void goUserHomeActivity(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.callUiCommand(context, BuildConfigKey.BUNDLE_USER,
                RouterUserCommand.goUserHomeActivity, args, callback);
    }

    public static void goUserRechargeActivity(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.callUiCommand(context, BuildConfigKey.BUNDLE_USER,
                RouterUserCommand.goUserRechargeActivity, args, callback);
    }
}
