package com.pine.router.command;

import com.pine.config.ConfigKey;
import com.pine.router.annotation.ARouterRemoteAction;

/**
 * Created by tanghongfeng on 2019/1/25
 */

@ARouterRemoteAction(Key = ConfigKey.BUNDLE_DB_SEVER_KEY, UiRemoteAction = "/db/dataService",
        DataRemoteAction = "/db/dataService", OpRemoteAction = "/db/dataService")
public interface RouterDbServerCommand {
    // Data command begin
    String callDbServerCommand = "callDbServerCommand";
    // Data command end
}
