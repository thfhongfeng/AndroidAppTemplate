package com.pine.user.remote;

import android.content.Context;
import android.os.Bundle;

import com.pine.router.IRouterCallback;
import com.pine.router.command.RouterLoginCommand;
import com.pine.router.impl.RouterManager;

public class UserClientManager {
    public static void callCommand(Context context, String bundleKey,
                                   String command, Bundle args, IRouterCallback callback) {
        RouterManager.getInstance(bundleKey).callUiCommand(context,
                command, args, callback);
    }

    public static void logout(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.getLoginRouter().callOpCommand(context, RouterLoginCommand.logout, args, callback);
    }
}
