package com.pine.template.base.remote;

import android.content.Context;
import android.os.Bundle;

import com.pine.template.base.bean.AccountBean;
import com.pine.template.base.router.command.RouterLoginCommand;
import com.pine.template.base.router.command.RouterMainCommand;
import com.pine.template.base.router.command.RouterUserCommand;
import com.pine.template.config.ConfigKey;
import com.pine.tool.router.IRouterCallback;
import com.pine.tool.router.RouterManager;

public class BaseRouterClient {

    public static void autoLogin(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.callOpCommand(context, ConfigKey.BUNDLE_LOGIN_KEY,
                RouterLoginCommand.autoLogin, args, callback);
    }

    public static void goLoginActivity(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.callUiCommand(context, ConfigKey.BUNDLE_LOGIN_KEY,
                RouterLoginCommand.goLoginActivity, args, callback);
    }

    public static AccountBean getLoginAccount(Context context, Bundle args) {
        return RouterManager.callDataCommandDirect(context, ConfigKey.BUNDLE_LOGIN_KEY, RouterLoginCommand.getLoginAccount, args);
    }

    public static void goMainHomeActivity(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.callUiCommand(context, ConfigKey.BUNDLE_MAIN_KEY,
                RouterMainCommand.goMainHomeActivity, args, callback);
    }

    public static void goUserHomeActivity(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.callUiCommand(context, ConfigKey.BUNDLE_USER_KEY,
                RouterUserCommand.goUserHomeActivity, args, callback);
    }

    public static void goUserRechargeActivity(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.callUiCommand(context, ConfigKey.BUNDLE_USER_KEY,
                RouterUserCommand.goUserRechargeActivity, args, callback);
    }
}
