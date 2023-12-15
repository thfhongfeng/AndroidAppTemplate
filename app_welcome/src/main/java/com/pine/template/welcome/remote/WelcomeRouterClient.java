package com.pine.template.welcome.remote;

import android.content.Context;
import android.os.Bundle;

import com.pine.template.config.ConfigKey;
import com.pine.template.config.router.command.RouterLoginCommand;
import com.pine.template.config.router.command.RouterMainCommand;
import com.pine.tool.router.IRouterCallback;
import com.pine.tool.router.RouterManager;

public class WelcomeRouterClient {

    public static void autoLogin(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.callOpCommand(context, ConfigKey.BUNDLE_LOGIN_KEY,
                RouterLoginCommand.autoLogin, args, callback);
    }

    public static void goMainHomeActivity(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.callUiCommand(context, ConfigKey.BUNDLE_MAIN_KEY,
                RouterMainCommand.goMainHomeActivity, args, callback);
    }
}
