package com.pine.main.remote;

import android.content.Context;
import android.os.Bundle;

import com.pine.main.MainApplication;
import com.pine.router.IRouterCallback;
import com.pine.router.command.RouterMainCommand;
import com.pine.router.command.RouterUserCommand;
import com.pine.router.impl.RouterManager;

public class MainClientManager {
    public static void callCommand(String bundleKey, String command, Bundle args, IRouterCallback callback) {
        RouterManager.getInstance(bundleKey).callUiCommand(MainApplication.mCurResumedActivity,
                command, args, callback);
    }

    public static void goMainHomeActivity(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.getMainRouter().callUiCommand(context,
                RouterMainCommand.goMainHomeActivity, null, null);
    }

    public static void goUserHomeActivity(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.getUserRouter().callUiCommand(context,
                RouterUserCommand.goUserHomeActivity, null, null);
    }
}
