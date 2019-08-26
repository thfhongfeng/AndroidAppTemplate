package com.pine.login.remote.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.pine.config.SPKeyConstants;
import com.pine.login.LoginApplication;
import com.pine.login.LoginConstants;
import com.pine.login.manager.LoginManager;
import com.pine.login.model.ILoginResponse;
import com.pine.login.ui.activity.LoginActivity;
import com.pine.router.IServiceCallback;
import com.pine.router.annotation.RouterCommand;
import com.pine.base.router.command.RouterLoginCommand;
import com.pine.tool.util.SharePreferenceUtils;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class LoginRemoteService {

    @RouterCommand(CommandName = RouterLoginCommand.autoLogin)
    public void autoLogin(@NonNull Context context, Bundle args, @NonNull final IServiceCallback callback) {
        final Bundle responseBundle = new Bundle();
        if (LoginApplication.isLogin()) {
            responseBundle.putBoolean(LoginConstants.SUCCESS, true);
            responseBundle.putString(LoginConstants.MESSAGE, "");
            callback.onResponse(responseBundle);
            return;
        }
        String account = SharePreferenceUtils.readStringFromCache(SPKeyConstants.ACCOUNT_ACCOUNT, "");
        String password = SharePreferenceUtils.readStringFromCache(SPKeyConstants.ACCOUNT_PASSWORD, "");
        if (account.length() == 0 || password.length() == 0) {
            responseBundle.putBoolean(LoginConstants.SUCCESS, false);
            responseBundle.putString(LoginConstants.MESSAGE, "no account on local");
            callback.onResponse(responseBundle);
            return;
        }
        LoginManager.autoLogin(account, password, new ILoginResponse() {
            @Override
            public boolean onLoginResponse(boolean isSuccess, String msg) {
                responseBundle.putBoolean(LoginConstants.SUCCESS, isSuccess);
                responseBundle.putString(LoginConstants.MESSAGE, msg);
                callback.onResponse(responseBundle);
                return true;
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @RouterCommand(CommandName = RouterLoginCommand.logout)
    public void logout(@NonNull Context context, Bundle args, @NonNull final IServiceCallback callback) {
        final Bundle responseBundle = new Bundle();
        LoginManager.logout();
        callback.onResponse(responseBundle);
    }

    @RouterCommand(CommandName = RouterLoginCommand.goLoginActivity)
    public void goLoginActivity(@NonNull Context context, Bundle args, @NonNull final IServiceCallback callback) {
        Bundle responseBundle = new Bundle();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        callback.onResponse(responseBundle);
    }
}
