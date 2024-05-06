package com.pine.template.main.remote;

import android.content.Context;
import android.os.Bundle;

import com.pine.app.template.bundle_main.BuildConfigKey;
import com.pine.template.config.router.command.RouterWelcomeCommand;
import com.pine.tool.router.IRouterCallback;
import com.pine.tool.router.RouterManager;

public class MainRouterClient {
    public static void callCommand(Context context, String bundleKey, String commandType,
                                   String command, Bundle args, IRouterCallback callback) {
        RouterManager.callCommand(context, bundleKey, commandType, command, args, callback);
    }

    public static void checkAndUpdateApk(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.callUiCommand(context, BuildConfigKey.BUNDLE_WELCOME,
                RouterWelcomeCommand.checkApkUpdate, args, callback);
    }
}
