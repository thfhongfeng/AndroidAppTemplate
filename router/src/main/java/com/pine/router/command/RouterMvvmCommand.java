package com.pine.router.command;

import com.pine.config.ConfigKey;
import com.pine.router.annotation.ARouterRemoteAction;

/**
 * Created by tanghongfeng on 2019/1/25
 */

@ARouterRemoteAction(Key = ConfigKey.BUNDLE_BUSINESS_MVVM_KEY, UiRemoteAction = "/mvvm/uiService",
        DataRemoteAction = "/mvvm/dataService", OpRemoteAction = "/mvvm/opService")
public interface RouterMvvmCommand {
    // Ui command begin
    String goMvvmHomeActivity = "goMvpHomeActivity";
    // Ui command end

    // Data command begin
    // Data command end

    // Op command begin
    // Op command end
}
