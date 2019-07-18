package com.pine.router.command;

import com.pine.config.ConfigKey;
import com.pine.router.annotation.ARouterRemoteAction;
import com.pine.router.annotation.AtlasRemoteAction;

/**
 * Created by tanghongfeng on 2019/1/25
 */

@ARouterRemoteAction(Key = ConfigKey.BUNDLE_BUSINESS_MVP_KEY, UiRemoteAction = "/mvp/uiService",
        DataRemoteAction = "/mvp/dataService", OpRemoteAction = "/mvp/opService")
@AtlasRemoteAction(Key = ConfigKey.BUNDLE_BUSINESS_MVP_KEY,
        UiRemoteAction = "atlas.transaction.intent.action.main.MvpUiRemoteAction",
        DataRemoteAction = "atlas.transaction.intent.action.main.MvpDataRemoteAction",
        OpRemoteAction = "atlas.transaction.intent.action.main.MvpOpRemoteAction")
public interface RouterMvpCommand {
    // Ui command begin
    String goMvpHomeActivity = "goMvpHomeActivity";
    // Ui command end

    // Data command begin
    // Data command end

    // Op command begin
    // Op command end
}
