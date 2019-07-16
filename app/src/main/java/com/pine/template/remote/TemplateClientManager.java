package com.pine.template.remote;

import android.content.Context;
import android.os.Bundle;

import com.pine.config.ConfigBundleKey;
import com.pine.router.IRouterCallback;
import com.pine.router.command.RouterLoginCommand;
import com.pine.router.impl.RouterManager;

public class TemplateClientManager {
    public static void callCommand(Context context, String bundleKey,
                                   String command, Bundle args, IRouterCallback callback) {
        RouterManager.getInstance(bundleKey).callUiCommand(context,
                command, args, callback);
    }

    public static void goLoginActivity(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.getInstance(ConfigBundleKey.LOGIN_BUNDLE_KEY).callUiCommand(context,
                RouterLoginCommand.goLoginActivity, args, callback);
    }
}
