package com.pine.login.model.local;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.base.architecture.mvp.model.IModelAsyncResponse;
import com.pine.base.database.DbRequestManager;
import com.pine.base.database.IDbCommand;
import com.pine.base.database.callback.DbJsonCallback;
import com.pine.login.LoginConstants;
import com.pine.login.bean.AccountBean;
import com.pine.login.model.ILoginAccountModel;
import com.pine.login.model.ILoginResponse;
import com.pine.login.model.local.callback.LoginLocalCallback;
import com.pine.tool.util.LogUtils;

import org.json.JSONObject;

import java.util.HashMap;

public class LoginAccountLocalModel implements ILoginAccountModel {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private static final int DB_REGISTER = 1;

    @Override
    public boolean requestLogin(HashMap<String, String> params, int requestCode, ILoginResponse callback) {
        return DbRequestManager.setJsonRequest(IDbCommand.REQUEST_LOGIN, params, TAG,
                requestCode, new LoginLocalCallback(callback));
    }

    @Override
    public void requestLogout() {
        DbRequestManager.setJsonRequest(IDbCommand.REQUEST_LOGOUT, new HashMap<String, String>(), TAG,
                LoginLocalCallback.LOGOUT_CODE, true, new LoginLocalCallback());
    }

    @Override
    public void requestRegister(HashMap<String, String> params, @NonNull IModelAsyncResponse<AccountBean> callback) {
        DbRequestManager.setJsonRequest(IDbCommand.REQUEST_REGISTER, params, TAG, DB_REGISTER,
                handleDbResponse(callback));
    }

    private <T> DbJsonCallback handleDbResponse(final IModelAsyncResponse<T> callback) {
        return new DbJsonCallback() {
            @Override
            public void onResponse(int what, JSONObject jsonObject) {
                if (what == DB_REGISTER) {
                    if (jsonObject.optBoolean(LoginConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(LoginConstants.DATA), new TypeToken<AccountBean>() {
                        }.getType());
                        callback.onResponse(retData);
                    } else {
                        callback.onFail(new Exception(jsonObject.optString("message")));
                    }
                }
            }

            @Override
            public boolean onFail(int what, Exception e) {
                return callback.onFail(e);
            }

            @Override
            public void onCancel(int what) {
                callback.onCancel();
            }
        };
    }
}
