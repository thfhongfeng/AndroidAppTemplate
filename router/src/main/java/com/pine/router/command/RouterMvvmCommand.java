package com.pine.router.command;

import com.pine.config.ConfigKey;
import com.pine.router.annotation.ARouterRemoteAction;

/**
 * Created by tanghongfeng on 2019/1/25
 */

@ARouterRemoteAction(Key = ConfigKey.BUNDLE_BUSINESS_MVVM_KEY, RemoteAction = "/mvvm/service")
public interface RouterMvvmCommand {
    String goMvvmHomeActivity = "goMvpHomeActivity";
}
