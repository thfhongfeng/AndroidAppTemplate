package com.pine.router.command;

import com.pine.config.ConfigBundleKey;
import com.pine.router.annotation.ARouterRemoteAction;
import com.pine.router.annotation.AtlasRemoteAction;

/**
 * Created by tanghongfeng on 2019/1/25
 */

@ARouterRemoteAction(Key = ConfigBundleKey.DB_SEVER_BUNDLE_KEY, UiRemoteAction = "/db/dataService",
        DataRemoteAction = "/db/dataService", OpRemoteAction = "/db/dataService")
@AtlasRemoteAction(Key = ConfigBundleKey.DB_SEVER_BUNDLE_KEY,
        UiRemoteAction = "atlas.transaction.intent.action.main.DbDataRemoteAction",
        DataRemoteAction = "atlas.transaction.intent.action.main.DbDataRemoteAction",
        OpRemoteAction = "atlas.transaction.intent.action.main.DbDataRemoteAction")
public interface RouterDbServerCommand {
    // Data command begin
    String callDbServerCommand = "callDbServerCommand";
    // Data command end
}
