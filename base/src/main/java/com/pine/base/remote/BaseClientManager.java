package com.pine.base.remote;

import android.content.Context;
import android.os.Bundle;

import com.pine.config.ConfigKey;
import com.pine.router.IRouterCallback;
import com.pine.router.command.RouterLoginCommand;
import com.pine.router.command.RouterMainCommand;
import com.pine.router.command.RouterUserCommand;
import com.pine.router.impl.RouterManager;

public class BaseClientManager {
    public static void callCommand(Context context, String bundleKey,
                                   String command, Bundle args, IRouterCallback callback) {
        RouterManager.getInstance(bundleKey).callUiCommand(context,
                command, args, callback);
    }

    public static void autoLogin(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.getInstance(ConfigKey.BUNDLE_LOGIN_KEY).callOpCommand(context,
                RouterLoginCommand.autoLogin, args, callback);
    }

    public static void goLoginActivity(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.getInstance(ConfigKey.BUNDLE_LOGIN_KEY).callUiCommand(context,
                RouterLoginCommand.goLoginActivity, args, callback);
    }

    public static void goMainHomeActivity(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.getInstance(ConfigKey.BUNDLE_MAIN_KEY).callUiCommand(context,
                RouterMainCommand.goMainHomeActivity, args, callback);
    }

    public static void goUserHomeActivity(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.getInstance(ConfigKey.BUNDLE_USER_KEY).callUiCommand(context,
                RouterUserCommand.goUserHomeActivity, args, callback);
    }
}
