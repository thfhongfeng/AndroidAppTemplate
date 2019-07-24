package com.pine.router.command;

import com.pine.config.ConfigKey;
import com.pine.router.annotation.ARouterRemoteAction;

/**
 * Created by tanghongfeng on 2019/1/25
 */

@ARouterRemoteAction(Key = ConfigKey.BUNDLE_BUSINESS_MVC_KEY, RemoteAction = "/mvc/service")
public interface RouterMvcCommand {
    String goMvcHomeActivity = "goMvcHomeActivity";
}
