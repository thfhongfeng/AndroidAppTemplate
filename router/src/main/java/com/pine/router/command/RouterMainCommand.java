package com.pine.router.command;

import com.pine.config.ConfigKey;
import com.pine.router.annotation.ARouterRemoteAction;

/**
 * Created by tanghongfeng on 2019/1/25
 */

@ARouterRemoteAction(Key = ConfigKey.BUNDLE_MAIN_KEY, UiRemoteAction = "/main/uiService",
        DataRemoteAction = "/main/dataService", OpRemoteAction = "/main/opService")
public interface RouterMainCommand {
    // Ui command begin
    String goMainHomeActivity = "goMainHomeActivity";
    // Ui command end

    // Data command begin
    // Data command end

    // Op command begin
    // Op command end
}
