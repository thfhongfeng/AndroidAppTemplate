package com.pine.base.router.command;

import com.pine.config.ConfigKey;
import com.pine.router.annotation.ARouterRemoteAction;

/**
 * Created by tanghongfeng on 2019/1/25
 */

@ARouterRemoteAction(Key = ConfigKey.BUNDLE_LOGIN_KEY, RemoteAction = "/login/service")
public interface RouterLoginCommand {
    String goLoginActivity = "goLoginActivity";
    String autoLogin = "autoLogin";
    String logout = "logout";
}
