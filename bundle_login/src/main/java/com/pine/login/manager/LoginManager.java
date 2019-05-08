package com.pine.login.manager;

import com.pine.login.LoginApplication;
import com.pine.login.LoginConstants;
import com.pine.login.model.ILoginResponse;
import com.pine.login.model.LoginAccountModel;
import com.pine.login.model.callback.LoginCallback;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.SecurityUtils;
import com.pine.tool.util.SharePreferenceUtils;

import org.json.JSONObject;

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
        String account = SharePreferenceUtils.readStringFromCache(LoginConstants.LOGIN_ACCOUNT, "");
        String password = SharePreferenceUtils.readStringFromCache(LoginConstants.LOGIN_PASSWORD, "");
        if (account.length() == 0 || password.length() == 0) {
            return false;
        }
        params.put(LoginConstants.LOGIN_ACCOUNT, account);
        params.put(LoginConstants.LOGIN_PASSWORD, password);

        mAccount = account;
        mPassword = password;

        return mAccountModel.requestLogin(params, LoginCallback.RE_LOGIN_CODE, null);
    }

    public static void saveLoginInfo(JSONObject jsonObject) {
        SharePreferenceUtils.saveToCache(LoginConstants.LOGIN_ACCOUNT, mAccount);
        SharePreferenceUtils.saveToCache(LoginConstants.LOGIN_PASSWORD, mPassword);
    }

    public static void clearLoginInfo() {
        SharePreferenceUtils.cleanCacheKey(LoginConstants.LOGIN_ACCOUNT);
        SharePreferenceUtils.cleanCacheKey(LoginConstants.LOGIN_PASSWORD);
    }
}
