package com.pine.template.login.model.callback;

import static com.pine.tool.request.IRequestManager.SESSION_ID;

import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pine.template.base.business.bean.AccountBean;
import com.pine.template.base.business.utils.AccountUtils;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.template.login.LoginApplication;
import com.pine.template.login.LoginKeyConstants;
import com.pine.template.login.LoginUrlConstants;
import com.pine.template.login.manager.LoginManager;
import com.pine.template.login.model.ILoginResponse;
import com.pine.template.login.ui.activity.LoginActivity;
import com.pine.tool.request.RequestManager;
import com.pine.tool.request.Response;
import com.pine.tool.request.callback.JsonCallback;
import com.pine.tool.util.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/9/10.
 */

public class LoginCallback extends JsonCallback {
    public static final int LOGIN_CODE = 4001;
    public static final int LOGOUT_CODE = 4002;
    public static final int AUTO_LOGIN_CODE = 4003;
    public static final int RE_LOGIN_CODE = 4004;
    private ILoginResponse mCallback;

    public LoginCallback() {

    }

    public LoginCallback(ILoginResponse callback) {
        mCallback = callback;
    }

    @Override
    public void onResponse(int what, Response response) {
        HashMap<String, String> cookies = response.getCookies();
        Iterator<Map.Entry<String, String>> iterator = cookies.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (SESSION_ID.equalsIgnoreCase(entry.getKey())) {
                RequestManager.setSessionId(entry.getValue());
                break;
            }
        }
        super.onResponse(what, response);
    }

    @Override
    public void onResponse(final int what, final JSONObject jsonObject, Response response) {
        if (LOGOUT_CODE == what) {
            LoginManager.clearLoginInfo();
            LoginApplication.setLogin(false);
            return;
        } else {
            if (jsonObject == null || !jsonObject.optBoolean(LoginKeyConstants.SUCCESS, false)) {
                loginFail(what, jsonObject == null ?
                        "" : jsonObject.optString(LoginKeyConstants.MESSAGE), "登陆失败！");
                if (AUTO_LOGIN_CODE != what) {
                    goLoginActivity();
                }
                return;
            }
            AccountBean responseAccount = new Gson().fromJson(jsonObject.optString(LoginKeyConstants.DATA), AccountBean.class);
            final AccountBean accountBean = responseAccount;
            LoginManager.saveLoginInfo(accountBean);
            LoginApplication.setLogin(true);
            ConfigSwitcherServer.setupConfigSwitcher(LoginUrlConstants.CONFIG(),
                    AccountUtils.getAccountInfoAndIpParams(LoginApplication.mApplication),
                    new ConfigSwitcherServer.IConfigSwitcherCallback() {
                        @Override
                        public void onSetupComplete(boolean change) {
                            loginSuccess(what, accountBean, "登陆成功！");
                        }

                        @Override
                        public boolean onSetupFail() {
                            LoginManager.logout();
                            loginFail(what, jsonObject == null ?
                                    "" : jsonObject.optString(LoginKeyConstants.MESSAGE), "登陆失败，服务器异常，请重试！");
                            return true;
                        }
                    });
        }
    }

    private void loginSuccess(int what, AccountBean accountBean, String defaultMsg) {
        if (mCallback != null) {
            if (mCallback.onLoginResponse(true, "") && LOGIN_CODE == what) {
                Toast.makeText(AppUtils.getApplication(), defaultMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loginFail(int what, String serverMsg, String defaultMsg) {
        LoginApplication.setLogin(false);
        if (mCallback != null) {
            if (!mCallback.onLoginResponse(false, serverMsg) && LOGIN_CODE == what) {
                Toast.makeText(AppUtils.getApplication(), defaultMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onFail(int what, Exception e, Response response) {
        LoginApplication.setLogin(false);
        boolean consumed = false;
        if (mCallback != null) {
            consumed = mCallback.onLoginResponse(false, e.getMessage());
        }
        if (AUTO_LOGIN_CODE != what) {
            goLoginActivity();
        }
        return consumed;
    }

    @Override
    public void onCancel(int what) {
        if (mCallback != null) {
            mCallback.onCancel();
        }
    }

    private void goLoginActivity() {
        Intent intent = new Intent(AppUtils.getApplication(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppUtils.getApplication().startActivity(intent);
    }
}
