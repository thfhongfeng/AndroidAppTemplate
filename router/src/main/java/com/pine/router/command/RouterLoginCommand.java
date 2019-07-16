package com.pine.router.command;

import com.pine.config.ConfigBundleKey;
import com.pine.router.annotation.ARouterRemoteAction;
import com.pine.router.annotation.AtlasRemoteAction;

/**
 * Created by tanghongfeng on 2019/1/25
 */

@ARouterRemoteAction(Key = ConfigBundleKey.LOGIN_BUNDLE_KEY, UiRemoteAction = "/login/uiService",
        DataRemoteAction = "/login/dataService", OpRemoteAction = "/login/opService")
@AtlasRemoteAction(Key = ConfigBundleKey.LOGIN_BUNDLE_KEY,
        UiRemoteAction = "atlas.transaction.intent.action.main.LoginUiRemoteAction",
        DataRemoteAction = "atlas.transaction.intent.action.main.LoginDataRemoteAction",
        OpRemoteAction = "atlas.transaction.intent.action.main.LoginOpRemoteAction")
public interface RouterLoginCommand {
    // Ui command begin
    String goLoginActivity = "goLoginActivity";
    // Ui command end

    // Data command begin
    // Data command end

    // Op command begin
    String autoLogin = "autoLogin";
    String logout = "logout";
    // Op command end
}
