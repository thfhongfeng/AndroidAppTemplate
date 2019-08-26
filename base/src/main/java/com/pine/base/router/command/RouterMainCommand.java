package com.pine.base.router.command;

import com.pine.config.ConfigKey;
import com.pine.router.annotation.ARouterRemoteAction;

/**
 * Created by tanghongfeng on 2019/1/25
 */

@ARouterRemoteAction(Key = ConfigKey.BUNDLE_MAIN_KEY, RemoteAction = "/main/service")
public interface RouterMainCommand {
    String goMainHomeActivity = "goMainHomeActivity";
}
