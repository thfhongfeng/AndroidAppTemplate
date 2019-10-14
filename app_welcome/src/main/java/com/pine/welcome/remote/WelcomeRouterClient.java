package com.pine.welcome.remote;

import android.content.Context;
import android.os.Bundle;

import com.pine.config.ConfigKey;
import com.pine.router.IRouterCallback;
import com.pine.base.router.command.RouterLoginCommand;
import com.pine.base.router.command.RouterMainCommand;
import com.pine.router.RouterManager;

public class WelcomeRouterClient {
    public static void callCommand(Context context, String bundleKey,
                                   String command, Bundle args, IRouterCallback callback) {
        RouterManager.getInstance(bundleKey).callUiCommand(context,
                command, args, callback);
    }

    public static void autoLogin(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.getInstance(ConfigKey.BUNDLE_LOGIN_KEY).callOpCommand(context,
                RouterLoginCommand.autoLogin, args, callback);
    }

    public static void goMainHomeActivity(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.getInstance(ConfigKey.BUNDLE_MAIN_KEY).callUiCommand(context,
                RouterMainCommand.goMainHomeActivity, args, callback);
    }
}
