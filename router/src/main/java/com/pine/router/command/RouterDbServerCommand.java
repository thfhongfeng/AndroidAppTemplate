package com.pine.router.command;

import com.pine.config.ConfigKey;
import com.pine.router.annotation.ARouterRemoteAction;
import com.pine.router.annotation.AtlasRemoteAction;

/**
 * Created by tanghongfeng on 2019/1/25
 */

@ARouterRemoteAction(Key = ConfigKey.BUNDLE_DB_SEVER_KEY, UiRemoteAction = "/db/dataService",
        DataRemoteAction = "/db/dataService", OpRemoteAction = "/db/dataService")
@AtlasRemoteAction(Key = ConfigKey.BUNDLE_DB_SEVER_KEY,
        UiRemoteAction = "atlas.transaction.intent.action.main.DbDataRemoteAction",
        DataRemoteAction = "atlas.transaction.intent.action.main.DbDataRemoteAction",
        OpRemoteAction = "atlas.transaction.intent.action.main.DbDataRemoteAction")
public interface RouterDbServerCommand {
    // Data command begin
    String callDbServerCommand = "callDbServerCommand";
    // Data command end
}
