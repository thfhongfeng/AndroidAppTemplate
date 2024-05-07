package com.pine.template.user.remote;

import android.content.Context;
import android.os.Bundle;

import com.pine.app.template.bundle_user.BuildConfigKey;
import com.pine.app.template.bundle_user.router.RouterLoginCommand;
import com.pine.template.base.business.bean.AccountBean;
import com.pine.tool.router.IRouterCallback;
import com.pine.tool.router.RouterException;
import com.pine.tool.router.RouterManager;

public class UserRouterClient {

    public static void logout(Context context, Bundle args, IRouterCallback callback) {
        RouterManager.callOpCommand(context, BuildConfigKey.BUNDLE_LOGIN, RouterLoginCommand.logout, args, callback);
    }

    public static AccountBean getLoginAccount(Context context, Bundle args) {
        try {
            return RouterManager.callDataCommandDirect(context, BuildConfigKey.BUNDLE_LOGIN,
                    RouterLoginCommand.getLoginAccount, args);
        } catch (RouterException e) {
            return null;
        }
    }
}
