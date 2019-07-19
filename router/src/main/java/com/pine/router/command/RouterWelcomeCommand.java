package com.pine.router.command;

import com.pine.config.ConfigKey;
import com.pine.router.annotation.ARouterRemoteAction;
import com.pine.router.annotation.AtlasRemoteAction;

/**
 * Created by tanghongfeng on 2019/1/25
 */

@ARouterRemoteAction(Key = ConfigKey.BUNDLE_WELCOME_KEY, UiRemoteAction = "/wel/opService",
        DataRemoteAction = "/wel/opService", OpRemoteAction = "/wel/opService")
@AtlasRemoteAction(Key = ConfigKey.BUNDLE_WELCOME_KEY,
        UiRemoteAction = "atlas.transaction.intent.action.main.WelcomeOpRemoteAction",
        DataRemoteAction = "atlas.transaction.intent.action.main.WelcomeOpRemoteAction",
        OpRemoteAction = "atlas.transaction.intent.action.main.WelcomeOpRemoteAction")
public interface RouterWelcomeCommand {
    // Ui command begin
    // Ui command end

    // Data command begin
    // Data command end

    // Op command begin
    // Op command end
}
