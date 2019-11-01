package com.pine.base.router.command;

import com.pine.config.ConfigKey;
import com.pine.tool.router.annotation.ARouterRemoteAction;

/**
 * Created by tanghongfeng on 2019/1/25
 */

@ARouterRemoteAction(Key = ConfigKey.BUNDLE_USER_KEY, RemoteAction = "/user/service")
public interface RouterUserCommand {
    String goUserHomeActivity = "goUserHomeActivity";
    String goUserVipActivity = "goUserVipActivity";
}
