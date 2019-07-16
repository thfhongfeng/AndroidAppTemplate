package com.pine.welcome.remote;

import android.content.Context;
import android.os.Bundle;

import com.pine.config.ConfigBundleKey;
import com.pine.router.IRouterCallback;
import com.pine.router.command.RouterLoginCommand;
import com.pine.router.command.RouterMainCommand;
import com.pine.router.impl.RouterManager;

public class WelcomeClientManager {
    public static void callCommand(Context context, String bundleKey,
                                   String command, Bundle args, IRouterCallback callback) {
        RouterManager.getInstance(bundleKey).callUiCommand(context,
                command, args, callback);
    }

    public static void autoLogin(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.getInstance(ConfigBundleKey.LOGIN_BUNDLE_KEY).callOpCommand(context,
                RouterLoginCommand.autoLogin, args, callback);
    }

    public static void goMainHomeActivity(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.getInstance(ConfigBundleKey.MAIN_BUNDLE_KEY).callUiCommand(context,
                RouterMainCommand.goMainHomeActivity, args, callback);
    }
}
