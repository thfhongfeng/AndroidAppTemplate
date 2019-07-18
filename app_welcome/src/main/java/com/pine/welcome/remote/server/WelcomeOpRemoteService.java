package com.pine.welcome.remote.server;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.pine.router.IServiceCallback;
import com.pine.router.annotation.RouterCommand;
import com.pine.router.command.RouterWelcomeCommand;
import com.pine.welcome.WelcomeConstants;
import com.pine.welcome.manager.ConfigSwitcherManager;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class WelcomeOpRemoteService {
    @RouterCommand(CommandName = RouterWelcomeCommand.setupConfigSwitcher)
    public void setupConfigSwitcher(@NonNull Context context, Bundle args, @NonNull final IServiceCallback callback) {
        final Bundle responseBundle = new Bundle();
        ConfigSwitcherManager.getInstance().setupConfigSwitcher(new ConfigSwitcherManager.IConfigSwitcherCallback() {
            @Override
            public void onSetupComplete() {
                responseBundle.putBoolean(WelcomeConstants.SUCCESS, true);
                responseBundle.putString(WelcomeConstants.MESSAGE, "");
                callback.onResponse(responseBundle);
            }

            @Override
            public void onSetupFail() {
                responseBundle.putBoolean(WelcomeConstants.SUCCESS, false);
                responseBundle.putString(WelcomeConstants.MESSAGE, "");
                callback.onResponse(responseBundle);
            }
        });
    }
}
