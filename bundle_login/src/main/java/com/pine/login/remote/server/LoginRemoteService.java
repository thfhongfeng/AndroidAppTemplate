package com.pine.login.remote.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.pine.base.bean.AccountBean;
import com.pine.base.router.command.RouterLoginCommand;
import com.pine.login.LoginApplication;
import com.pine.login.LoginConstants;
import com.pine.login.LoginSPKeyConstants;
import com.pine.login.manager.LoginManager;
import com.pine.login.model.ILoginResponse;
import com.pine.login.ui.activity.LoginActivity;
import com.pine.tool.router.IServiceCallback;
import com.pine.tool.router.annotation.RouterCommand;
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
        String account = SharePreferenceUtils.readStringFromCache(LoginSPKeyConstants.ACCOUNT_ACCOUNT, "");
        String password = SharePreferenceUtils.readStringFromCache(LoginSPKeyConstants.ACCOUNT_PASSWORD, "");
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

    @RouterCommand(CommandName = RouterLoginCommand.getLoginAccount)
    public AccountBean getLoginAccount(@NonNull Context context, Bundle args) {
        AccountBean accountBean = new AccountBean();
        accountBean.setId(SharePreferenceUtils.readStringFromAppLivedCache(LoginSPKeyConstants.ACCOUNT_ID, ""));
        accountBean.setAccount(SharePreferenceUtils.readStringFromAppLivedCache(LoginSPKeyConstants.ACCOUNT_ACCOUNT, ""));
        accountBean.setAccountType(SharePreferenceUtils.readIntFromAppLivedCache(LoginSPKeyConstants.ACCOUNT_TYPE, 0));
        accountBean.setName(SharePreferenceUtils.readStringFromAppLivedCache(LoginSPKeyConstants.ACCOUNT_NAME, ""));
        accountBean.setHeadImgUrl(SharePreferenceUtils.readStringFromAppLivedCache(LoginSPKeyConstants.ACCOUNT_HEAD_IMG_URL, ""));
        accountBean.setMobile(SharePreferenceUtils.readStringFromAppLivedCache(LoginSPKeyConstants.ACCOUNT_MOBILE, ""));
        accountBean.setCurLoginTimeStamp(SharePreferenceUtils.readLongFromAppLivedCache(LoginSPKeyConstants.ACCOUNT_CUR_LOGIN_TIME_STAMP, -1));
        accountBean.setCreateTime(SharePreferenceUtils.readStringFromAppLivedCache(LoginSPKeyConstants.ACCOUNT_CREATE_TIME, ""));
        accountBean.setUpdateTime(SharePreferenceUtils.readStringFromAppLivedCache(LoginSPKeyConstants.ACCOUNT_UPDATE_TIME, ""));
        return accountBean;
    }
}
