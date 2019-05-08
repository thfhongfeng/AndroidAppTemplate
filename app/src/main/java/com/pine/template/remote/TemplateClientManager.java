package com.pine.template.remote;

import com.pine.base.BaseApplication;
import com.pine.router.IRouterCallback;
import com.pine.router.command.RouterLoginCommand;
import com.pine.router.impl.RouterManager;

public class TemplateClientManager {
    public static void goLoginActivity(IRouterCallback callback) {
        RouterManager.getLoginRouter().callUiCommand(BaseApplication.mCurResumedActivity,
                RouterLoginCommand.goLoginActivity, null, callback);
    }
}
