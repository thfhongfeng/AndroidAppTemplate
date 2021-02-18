package com.pine.template.base.router.command;

import com.pine.template.config.ConfigKey;
import com.pine.tool.router.annotation.ARouterRemoteAction;

/**
 * Created by tanghongfeng on 2019/1/25
 */

@ARouterRemoteAction(Key = ConfigKey.BUNDLE_BUSINESS_MVVM_KEY, RemoteAction = "/mvvm/service")
public interface RouterMvvmCommand {
    String goMvvmHomeActivity = "goMvpHomeActivity";
}
