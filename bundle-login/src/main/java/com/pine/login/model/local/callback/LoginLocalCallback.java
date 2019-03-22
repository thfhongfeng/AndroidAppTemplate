package com.pine.login.model.local.callback;

import android.content.Intent;
import android.widget.Toast;

import com.pine.base.BaseApplication;
import com.pine.base.database.callback.DbJsonCallback;
import com.pine.login.LoginConstants;
import com.pine.login.manager.LoginManager;
import com.pine.login.model.ILoginResponse;
import com.pine.login.ui.activity.LoginActivity;
import com.pine.tool.util.AppUtils;

import org.json.JSONObject;

public class LoginLocalCallback extends DbJsonCallback {
    public static final int LOGIN_CODE = 4001;
    public static final int LOGOUT_CODE = 4002;
    public static final int AUTO_LOGIN_CODE = 4003;
    public static final int RE_LOGIN_CODE = 4004;
    private ILoginResponse mCallback;

    public LoginLocalCallback() {

    }

    public LoginLocalCallback(ILoginResponse callback) {
        mCallback = callback;
    }

    @Override
    public void onResponse(int what, JSONObject jsonObject) {
        if (LOGOUT_CODE == what) {
            LoginManager.clearLoginInfo();
            BaseApplication.setLogin(false);
            return;
        } else {
            if (jsonObject == null || !jsonObject.optBoolean(LoginConstants.SUCCESS, false)) {
                BaseApplication.setLogin(false);
                if (mCallback != null) {
                    if (!mCallback.onLoginResponse(false, jsonObject == null ?
                            "" : jsonObject.optString(LoginConstants.MESSAGE)) && LOGIN_CODE == what) {
                        Toast.makeText(AppUtils.getApplication(), "登陆失败！", Toast.LENGTH_SHORT).show();
                    }
                }
                if (AUTO_LOGIN_CODE != what) {
                    goLoginActivity();
                }
                return;
            }
            LoginManager.saveLoginInfo(jsonObject);
            BaseApplication.setLogin(true);
            if (mCallback != null) {
                if (mCallback.onLoginResponse(true, "") && LOGIN_CODE == what) {
                    Toast.makeText(AppUtils.getApplication(), "登陆成功！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onFail(int what, Exception e) {
        BaseApplication.setLogin(false);
        boolean consumed = false;
        if (mCallback != null) {
            consumed = mCallback.onLoginResponse(false, "");
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
