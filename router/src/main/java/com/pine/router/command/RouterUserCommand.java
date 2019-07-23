package com.pine.router.command;

import com.pine.config.ConfigKey;
import com.pine.router.annotation.ARouterRemoteAction;

/**
 * Created by tanghongfeng on 2019/1/25
 */

@ARouterRemoteAction(Key = ConfigKey.BUNDLE_USER_KEY, UiRemoteAction = "/user/uiService",
        DataRemoteAction = "/user/dataService", OpRemoteAction = "/user/opService")
public interface RouterUserCommand {
    // Ui command begin
    String goUserHomeActivity = "goUserHomeActivity";
    // Ui command end

    // Data command begin
    // Data command end

    // Op command begin
    // Op command end
}
