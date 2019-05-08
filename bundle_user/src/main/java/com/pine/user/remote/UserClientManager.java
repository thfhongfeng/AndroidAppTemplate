package com.pine.user.remote;

import android.content.Context;
import android.os.Bundle;

import com.pine.router.IRouterCallback;
import com.pine.router.command.RouterLoginCommand;
import com.pine.router.command.RouterMainCommand;
import com.pine.router.command.RouterUserCommand;
import com.pine.router.impl.RouterManager;

public class UserClientManager {
    public static void logout(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.getLoginRouter().callOpCommand(context, RouterLoginCommand.logout, args, callback);
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
