package com.pine.login.remote;

import android.content.Context;
import android.os.Bundle;

import com.pine.router.IRouterCallback;
import com.pine.router.RouterManager;

public class LoginRouterClient {
    public static void callCommand(Context context, String bundleKey,
                                   String command, Bundle args, IRouterCallback callback) {
        RouterManager.getInstance(bundleKey).callUiCommand(context,
                command, args, callback);
    }
}
