package com.pine.template.config.router.command;

import com.pine.app.template.config.BuildConfigKey;
import com.pine.tool.router.annotation.ARouterRemoteAction;

/**
 * Created by tanghongfeng on 2019/1/25
 */

@ARouterRemoteAction(Key = BuildConfigKey.BUNDLE_LOGIN, RemoteAction = "/login/service")
public interface RouterLoginCommand {
    String goLoginActivity = "goLoginActivity";
    String autoLogin = "autoLogin";
    String logout = "logout";

    String getLoginAccount = "getLoginAccount";
}
