package com.pine.login.manager;

import com.pine.base.BaseApplication;
import com.pine.login.LoginConstants;
import com.pine.login.model.ILoginAccountModel;
import com.pine.login.model.ILoginResponse;
import com.pine.login.model.LoginModelFactory;
import com.pine.login.model.net.callback.LoginCallback;
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
    private static ILoginAccountModel mAccountModel = LoginModelFactory.getLoginAccountModel();
    private static volatile String mMobile;
    private static volatile String mPassword;

    // 登录
    public static void login(String mobile, String password, ILoginResponse callback) {
        String securityPwd = SecurityUtils.generateMD5(password);
        HashMap<String, String> params = new HashMap<>();
        params.put(LoginConstants.LOGIN_MOBILE, mobile);
        params.put(LoginConstants.LOGIN_PASSWORD, securityPwd);

        mMobile = mobile;
        mPassword = securityPwd;

        mAccountModel.requestLogin(params, LoginCallback.LOGIN_CODE, callback);
    }

    // 退出登录
    public static void logout() {
        clearLoginInfo();
        BaseApplication.setLogin(false);
        mAccountModel.requestLogout();
    }

    // 自动登录
    public static void autoLogin(String mobile, String password, ILoginResponse callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put(LoginConstants.LOGIN_MOBILE, mobile);
        params.put(LoginConstants.LOGIN_PASSWORD, password);

        mMobile = mobile;
        mPassword = password;

        mAccountModel.requestLogin(params, LoginCallback.AUTO_LOGIN_CODE, callback);
    }

    // 重新登录
    public static boolean reLogin() {
        if (BaseApplication.isLogin()) {
            return false;
        }
        HashMap<String, String> params = new HashMap<>();
        String mobile = SharePreferenceUtils.readStringFromCache(LoginConstants.LOGIN_MOBILE, "");
        String password = SharePreferenceUtils.readStringFromCache(LoginConstants.LOGIN_PASSWORD, "");
        if (mobile.length() == 0 || password.length() == 0) {
            return false;
        }
        params.put(LoginConstants.LOGIN_MOBILE, mobile);
        params.put(LoginConstants.LOGIN_PASSWORD, password);

        mMobile = mobile;
        mPassword = password;

        return mAccountModel.requestLogin(params, LoginCallback.RE_LOGIN_CODE, null);
    }

    public static void saveLoginInfo(JSONObject jsonObject) {
        SharePreferenceUtils.saveToCache(LoginConstants.LOGIN_MOBILE, mMobile);
        SharePreferenceUtils.saveToCache(LoginConstants.LOGIN_PASSWORD, mPassword);
    }

    public static void clearLoginInfo() {
        SharePreferenceUtils.cleanCacheKey(LoginConstants.LOGIN_MOBILE);
        SharePreferenceUtils.cleanCacheKey(LoginConstants.LOGIN_PASSWORD);
    }
}
