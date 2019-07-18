package com.pine.login.manager;

import com.pine.config.SPKeyConstants;
import com.pine.login.LoginApplication;
import com.pine.login.LoginConstants;
import com.pine.login.bean.AccountBean;
import com.pine.login.model.ILoginResponse;
import com.pine.login.model.LoginAccountModel;
import com.pine.login.model.callback.LoginCallback;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.SecurityUtils;
import com.pine.tool.util.SharePreferenceUtils;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public class LoginManager {
    private final static String TAG = LogUtils.makeLogTag(LoginManager.class);
    private static LoginAccountModel mAccountModel = new LoginAccountModel();
    private static volatile String mAccount;
    private static volatile String mPassword;

    // 登录
    public static void login(String account, String password, ILoginResponse callback) {
        String securityPwd = SecurityUtils.generateMD5(password);
        HashMap<String, String> params = new HashMap<>();
        params.put(LoginConstants.LOGIN_ACCOUNT, account);
        params.put(LoginConstants.LOGIN_PASSWORD, securityPwd);

        mAccount = account;
        mPassword = securityPwd;

        mAccountModel.requestLogin(params, LoginCallback.LOGIN_CODE, callback);
    }

    // 退出登录
    public static void logout() {
        clearLoginInfo();
        LoginApplication.setLogin(false);
        mAccountModel.requestLogout();
    }

    // 自动登录
    public static void autoLogin(String account, String password, ILoginResponse callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put(LoginConstants.LOGIN_ACCOUNT, account);
        params.put(LoginConstants.LOGIN_PASSWORD, password);

        mAccount = account;
        mPassword = password;

        mAccountModel.requestLogin(params, LoginCallback.AUTO_LOGIN_CODE, callback);
    }

    // 重新登录
    public static boolean reLogin() {
        if (LoginApplication.isLogin()) {
            return false;
        }
        HashMap<String, String> params = new HashMap<>();
        String account = SharePreferenceUtils.readStringFromCache(SPKeyConstants.ACCOUNT_ACCOUNT, "");
        String password = SharePreferenceUtils.readStringFromCache(SPKeyConstants.ACCOUNT_PASSWORD, "");
        if (account.length() == 0 || password.length() == 0) {
            return false;
        }
        params.put(LoginConstants.LOGIN_ACCOUNT, account);
        params.put(LoginConstants.LOGIN_PASSWORD, password);

        mAccount = account;
        mPassword = password;

        return mAccountModel.requestLogin(params, LoginCallback.RE_LOGIN_CODE, null);
    }

    public static void saveLoginInfo(AccountBean accountBean) {
        SharePreferenceUtils.saveToCache(SPKeyConstants.ACCOUNT_ACCOUNT, mAccount);
        SharePreferenceUtils.saveToCache(SPKeyConstants.ACCOUNT_PASSWORD, mPassword);

        if (accountBean == null) {
            return;
        }
        SharePreferenceUtils.saveToAppLivedCache(SPKeyConstants.ACCOUNT_ID, accountBean.getId());
        SharePreferenceUtils.saveToAppLivedCache(SPKeyConstants.ACCOUNT_ACCOUNT, accountBean.getAccount());
        SharePreferenceUtils.saveToAppLivedCache(SPKeyConstants.ACCOUNT_PASSWORD, accountBean.getPassword());
        SharePreferenceUtils.saveToAppLivedCache(SPKeyConstants.ACCOUNT_TYPE, accountBean.getAccountType());
        SharePreferenceUtils.saveToAppLivedCache(SPKeyConstants.ACCOUNT_NAME, accountBean.getName());
        SharePreferenceUtils.saveToAppLivedCache(SPKeyConstants.ACCOUNT_HEAD_IMG_URL, accountBean.getHeadImgUrl());
        SharePreferenceUtils.saveToAppLivedCache(SPKeyConstants.ACCOUNT_STATE, accountBean.getState());
        SharePreferenceUtils.saveToAppLivedCache(SPKeyConstants.ACCOUNT_MOBILE, accountBean.getMobile());
        SharePreferenceUtils.saveToAppLivedCache(SPKeyConstants.ACCOUNT_CREATE_TIME, accountBean.getCreateTime());
        SharePreferenceUtils.saveToAppLivedCache(SPKeyConstants.ACCOUNT_UPDATE_TIME, accountBean.getUpdateTime());
    }

    public static void clearLoginInfo() {
        SharePreferenceUtils.cleanCacheKey(SPKeyConstants.ACCOUNT_ACCOUNT);
        SharePreferenceUtils.cleanCacheKey(SPKeyConstants.ACCOUNT_PASSWORD);

        SharePreferenceUtils.cleanAppLivedCacheKey(SPKeyConstants.ACCOUNT_ID);
        SharePreferenceUtils.cleanAppLivedCacheKey(SPKeyConstants.ACCOUNT_ACCOUNT);
        SharePreferenceUtils.cleanAppLivedCacheKey(SPKeyConstants.ACCOUNT_PASSWORD);
        SharePreferenceUtils.cleanAppLivedCacheKey(SPKeyConstants.ACCOUNT_TYPE);
        SharePreferenceUtils.cleanAppLivedCacheKey(SPKeyConstants.ACCOUNT_NAME);
        SharePreferenceUtils.cleanAppLivedCacheKey(SPKeyConstants.ACCOUNT_HEAD_IMG_URL);
        SharePreferenceUtils.cleanAppLivedCacheKey(SPKeyConstants.ACCOUNT_STATE);
        SharePreferenceUtils.cleanAppLivedCacheKey(SPKeyConstants.ACCOUNT_MOBILE);
        SharePreferenceUtils.cleanAppLivedCacheKey(SPKeyConstants.ACCOUNT_CREATE_TIME);
        SharePreferenceUtils.cleanAppLivedCacheKey(SPKeyConstants.ACCOUNT_UPDATE_TIME);
    }
}
