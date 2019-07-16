package com.pine.router.command;

import com.pine.config.ConfigBundleKey;
import com.pine.router.annotation.ARouterRemoteAction;
import com.pine.router.annotation.AtlasRemoteAction;

/**
 * Created by tanghongfeng on 2019/1/25
 */

@ARouterRemoteAction(Key = ConfigBundleKey.BUSINESS_MVC_BUNDLE_KEY, UiRemoteAction = "/mvc/uiService",
        DataRemoteAction = "/mvc/dataService", OpRemoteAction = "/mvc/opService")
@AtlasRemoteAction(Key = ConfigBundleKey.BUSINESS_MVC_BUNDLE_KEY,
        UiRemoteAction = "atlas.transaction.intent.action.main.MvcUiRemoteAction",
        DataRemoteAction = "atlas.transaction.intent.action.main.MvcDataRemoteAction",
        OpRemoteAction = "atlas.transaction.intent.action.main.MvcOpRemoteAction")
public interface RouterMvcCommand {
    // Ui command begin
    String goMvcHomeActivity = "goMvcHomeActivity";
    // Ui command end

    // Data command begin
    // Data command end

    // Op command begin
    // Op command end
}
