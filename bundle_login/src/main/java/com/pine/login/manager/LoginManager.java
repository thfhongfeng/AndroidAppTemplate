package com.pine.login.manager;

import com.pine.login.LoginApplication;
import com.pine.login.LoginConstants;
import com.pine.login.LoginSPKeyConstants;
import com.pine.base.bean.AccountBean;
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
        String account = SharePreferenceUtils.readStringFromCache(LoginSPKeyConstants.ACCOUNT_ACCOUNT, "");
        String password = SharePreferenceUtils.readStringFromCache(LoginSPKeyConstants.ACCOUNT_PASSWORD, "");
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
        SharePreferenceUtils.saveToCache(LoginSPKeyConstants.ACCOUNT_ACCOUNT, mAccount);
        SharePreferenceUtils.saveToCache(LoginSPKeyConstants.ACCOUNT_PASSWORD, mPassword);

        if (accountBean == null) {
            return;
        }
        SharePreferenceUtils.saveToAppLivedCache(LoginSPKeyConstants.ACCOUNT_ID, accountBean.getId());
        SharePreferenceUtils.saveToAppLivedCache(LoginSPKeyConstants.ACCOUNT_ACCOUNT, accountBean.getAccount());
        SharePreferenceUtils.saveToAppLivedCache(LoginSPKeyConstants.ACCOUNT_PASSWORD, accountBean.getPassword());
        SharePreferenceUtils.saveToAppLivedCache(LoginSPKeyConstants.ACCOUNT_TYPE, accountBean.getAccountType());
        SharePreferenceUtils.saveToAppLivedCache(LoginSPKeyConstants.ACCOUNT_NAME, accountBean.getName());
        SharePreferenceUtils.saveToAppLivedCache(LoginSPKeyConstants.ACCOUNT_HEAD_IMG_URL, accountBean.getHeadImgUrl());
        SharePreferenceUtils.saveToAppLivedCache(LoginSPKeyConstants.ACCOUNT_STATE, accountBean.getState());
        SharePreferenceUtils.saveToAppLivedCache(LoginSPKeyConstants.ACCOUNT_MOBILE, accountBean.getMobile());
        SharePreferenceUtils.saveToAppLivedCache(LoginSPKeyConstants.ACCOUNT_CREATE_TIME, accountBean.getCreateTime());
        SharePreferenceUtils.saveToAppLivedCache(LoginSPKeyConstants.ACCOUNT_UPDATE_TIME, accountBean.getUpdateTime());
    }

    public static void clearLoginInfo() {
        SharePreferenceUtils.cleanCacheKey(LoginSPKeyConstants.ACCOUNT_ACCOUNT);
        SharePreferenceUtils.cleanCacheKey(LoginSPKeyConstants.ACCOUNT_PASSWORD);

        SharePreferenceUtils.cleanAppLivedCacheKey(LoginSPKeyConstants.ACCOUNT_ID);
        SharePreferenceUtils.cleanAppLivedCacheKey(LoginSPKeyConstants.ACCOUNT_ACCOUNT);
        SharePreferenceUtils.cleanAppLivedCacheKey(LoginSPKeyConstants.ACCOUNT_PASSWORD);
        SharePreferenceUtils.cleanAppLivedCacheKey(LoginSPKeyConstants.ACCOUNT_TYPE);
        SharePreferenceUtils.cleanAppLivedCacheKey(LoginSPKeyConstants.ACCOUNT_NAME);
        SharePreferenceUtils.cleanAppLivedCacheKey(LoginSPKeyConstants.ACCOUNT_HEAD_IMG_URL);
        SharePreferenceUtils.cleanAppLivedCacheKey(LoginSPKeyConstants.ACCOUNT_STATE);
        SharePreferenceUtils.cleanAppLivedCacheKey(LoginSPKeyConstants.ACCOUNT_MOBILE);
        SharePreferenceUtils.cleanAppLivedCacheKey(LoginSPKeyConstants.ACCOUNT_CREATE_TIME);
        SharePreferenceUtils.cleanAppLivedCacheKey(LoginSPKeyConstants.ACCOUNT_UPDATE_TIME);
    }
}
