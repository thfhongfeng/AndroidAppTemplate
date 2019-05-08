package com.pine.welcome.remote;

import android.os.Bundle;

import com.pine.router.IRouterCallback;
import com.pine.router.command.RouterLoginCommand;
import com.pine.router.command.RouterMainCommand;
import com.pine.router.impl.RouterManager;
import com.pine.welcome.WelcomeApplication;

public class WelcomeClientManager {

    public static void autoLogin(Bundle args, IRouterCallback callback) {
        RouterManager.getLoginRouter().callOpCommand(WelcomeApplication.mCurResumedActivity,
                RouterLoginCommand.autoLogin, args, callback);
    }

    public static void goMainHomeActivity(Bundle args, IRouterCallback callback) {
        RouterManager.getMainRouter().callUiCommand(WelcomeApplication.mCurResumedActivity,
                RouterMainCommand.goMainHomeActivity, args, callback);
    }
}
