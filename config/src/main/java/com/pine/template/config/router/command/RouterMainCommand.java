package com.pine.template.config.router.command;

import com.pine.app.template.config.BuildConfigKey;
import com.pine.tool.router.annotation.ARouterRemoteAction;

/**
 * Created by tanghongfeng on 2019/1/25
 */

@ARouterRemoteAction(Key = BuildConfigKey.BUNDLE_MAIN, RemoteAction = "/main/service")
public interface RouterMainCommand {
    String goMainHomeActivity = "goMainHomeActivity";
}
