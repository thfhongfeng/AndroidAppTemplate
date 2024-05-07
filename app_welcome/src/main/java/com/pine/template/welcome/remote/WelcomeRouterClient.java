package com.pine.template.welcome.remote;

import android.content.Context;
import android.os.Bundle;

import com.pine.app.template.app_welcome.BuildConfigKey;
import com.pine.app.template.app_welcome.router.RouterLoginCommand;
import com.pine.app.template.app_welcome.router.RouterMainCommand;
import com.pine.tool.router.IRouterCallback;
import com.pine.tool.router.RouterManager;

public class WelcomeRouterClient {

    public static void autoLogin(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.callOpCommand(context, BuildConfigKey.BUNDLE_LOGIN,
                RouterLoginCommand.autoLogin, args, callback);
    }

    public static void goMainHomeActivity(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.callUiCommand(context, BuildConfigKey.BUNDLE_MAIN,
                RouterMainCommand.goMainHomeActivity, args, callback);
    }
}
