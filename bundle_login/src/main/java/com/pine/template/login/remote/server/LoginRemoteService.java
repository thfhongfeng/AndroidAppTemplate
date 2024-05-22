package com.pine.template.login.remote.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.pine.app.template.bundle_login.router.RouterLoginCommand;
import com.pine.template.base.business.bean.AccountBean;
import com.pine.template.login.LoginApplication;
import com.pine.template.login.LoginKeyConstants;
import com.pine.template.login.manager.LoginManager;
import com.pine.template.login.model.ILoginResponse;
import com.pine.template.login.ui.activity.LoginActivity;
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
            responseBundle.putBoolean(LoginKeyConstants.SUCCESS, true);
            responseBundle.putString(LoginKeyConstants.MESSAGE, "");
            callback.onResponse(responseBundle);
            return;
        }
        String account = SharePreferenceUtils.readStringFromCache(LoginKeyConstants.ACCOUNT_ACCOUNT, "");
        String password = SharePreferenceUtils.readStringFromCache(LoginKeyConstants.ACCOUNT_PASSWORD, "");
        if (account.length() == 0 || password.length() == 0) {
            responseBundle.putBoolean(LoginKeyConstants.SUCCESS, false);
            responseBundle.putString(LoginKeyConstants.MESSAGE, "no account on local");
            callback.onResponse(responseBundle);
            return;
        }
        LoginManager.autoLogin(account, password, new ILoginResponse() {
            @Override
            public boolean onLoginResponse(boolean isSuccess, String msg) {
                responseBundle.putBoolean(LoginKeyConstants.SUCCESS, isSuccess);
                responseBundle.putString(LoginKeyConstants.MESSAGE, msg);
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
        accountBean.setId(SharePreferenceUtils.readStringFromAppLivedCache(LoginKeyConstants.ACCOUNT_ID, ""));
        accountBean.setAccount(SharePreferenceUtils.readStringFromAppLivedCache(LoginKeyConstants.ACCOUNT_ACCOUNT, ""));
        accountBean.setAccountType(SharePreferenceUtils.readIntFromAppLivedCache(LoginKeyConstants.ACCOUNT_TYPE, 0));
        accountBean.setName(SharePreferenceUtils.readStringFromAppLivedCache(LoginKeyConstants.ACCOUNT_NAME, ""));
        accountBean.setHeadImgUrl(SharePreferenceUtils.readStringFromAppLivedCache(LoginKeyConstants.ACCOUNT_HEAD_IMG_URL, ""));
        accountBean.setMobile(SharePreferenceUtils.readStringFromAppLivedCache(LoginKeyConstants.ACCOUNT_MOBILE, ""));
        accountBean.setCurLoginTimeStamp(SharePreferenceUtils.readLongFromAppLivedCache(LoginKeyConstants.ACCOUNT_CUR_LOGIN_TIME_STAMP, -1));
        accountBean.setCreateTime(SharePreferenceUtils.readStringFromAppLivedCache(LoginKeyConstants.ACCOUNT_CREATE_TIME, ""));
        accountBean.setUpdateTime(SharePreferenceUtils.readStringFromAppLivedCache(LoginKeyConstants.ACCOUNT_UPDATE_TIME, ""));
        return accountBean;
    }
}
