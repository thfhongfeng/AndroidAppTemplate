package com.pine.template.config.router.command;

import com.pine.app.template.config.BuildConfigKey;
import com.pine.tool.router.annotation.ARouterRemoteAction;

/**
 * Created by tanghongfeng on 2019/1/25
 */

@ARouterRemoteAction(Key = BuildConfigKey.BIZ_BUNDLE_MVP, RemoteAction = "/mvp/service")
public interface RouterMvpCommand {
    String goMvpHomeActivity = "goMvpHomeActivity";
}