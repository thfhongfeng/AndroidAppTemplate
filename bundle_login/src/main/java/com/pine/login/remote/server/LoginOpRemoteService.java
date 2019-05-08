package com.pine.login.remote.server;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.pine.login.LoginApplication;
import com.pine.login.LoginConstants;
import com.pine.login.manager.LoginManager;
import com.pine.login.model.ILoginResponse;
import com.pine.router.IServiceCallback;
import com.pine.router.annotation.RouterAnnotation;
import com.pine.router.command.RouterLoginCommand;
import com.pine.tool.util.SharePreferenceUtils;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class LoginOpRemoteService {

    @RouterAnnotation(CommandName = RouterLoginCommand.autoLogin)
    public void autoLogin(@NonNull Context context, Bundle args, @NonNull final IServiceCallback callback) {
        final Bundle responseBundle = new Bundle();
        if (LoginApplication.isLogin()) {
            responseBundle.putBoolean("success", true);
            responseBundle.putString("msg", "");
            callback.onResponse(responseBundle);
            return;
        }
        String account = SharePreferenceUtils.readStringFromCache(LoginConstants.LOGIN_ACCOUNT, "");
        String password = SharePreferenceUtils.readStringFromCache(LoginConstants.LOGIN_PASSWORD, "");
        if (account.length() == 0 || password.length() == 0) {
            responseBundle.putBoolean("success", false);
            responseBundle.putString("msg", "no account on local");
            callback.onResponse(responseBundle);
            return;
        }
        LoginManager.autoLogin(account, password, new ILoginResponse() {
            @Override
            public boolean onLoginResponse(boolean isSuccess, String msg) {
                responseBundle.putBoolean("success", isSuccess);
                responseBundle.putString("msg", msg);
                callback.onResponse(responseBundle);
                return true;
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @RouterAnnotation(CommandName = RouterLoginCommand.logout)
    public void logout(@NonNull Context context, Bundle args, @NonNull final IServiceCallback callback) {
        final Bundle responseBundle = new Bundle();
        LoginManager.logout();
        callback.onResponse(responseBundle);
    }
}
