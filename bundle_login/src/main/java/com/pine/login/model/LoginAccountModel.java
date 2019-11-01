package com.pine.login.model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.config.BuildConfig;
import com.pine.login.LoginConstants;
import com.pine.login.LoginUrlConstants;
import com.pine.base.bean.AccountBean;
import com.pine.login.model.callback.LoginCallback;
import com.pine.tool.architecture.mvp.model.IModelAsyncResponse;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.RequestBean;
import com.pine.tool.request.RequestManager;
import com.pine.tool.request.Response;
import com.pine.tool.request.callback.JsonCallback;
import com.pine.tool.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginAccountModel {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private static final int REQUEST_REGISTER = 1;

    public boolean requestLogin(final HashMap<String, String> params, int requestCode, ILoginResponse callback) {
        String url = LoginUrlConstants.Login;
        RequestManager.clearCookie();
        RequestBean requestBean = new RequestBean(url, requestCode, params);
        requestBean.setModuleTag(TAG);
        return RequestManager.setJsonRequest(requestBean, new LoginCallback(callback));
    }

    public void requestLogout() {
        String url = LoginUrlConstants.Logout;
        RequestManager.clearCookie();
        RequestBean requestBean = new RequestBean(url, LoginCallback.LOGOUT_CODE, new HashMap<String, String>());
        requestBean.setModuleTag(TAG);
        RequestManager.setJsonRequest(requestBean, new LoginCallback());
    }

    public void requestRegister(final HashMap<String, String> params,
                                @NonNull final IModelAsyncResponse<AccountBean> callback) {
        String url = LoginUrlConstants.Register_Account;
        RequestBean requestBean = new RequestBean(url, REQUEST_REGISTER, params);
        requestBean.setModuleTag(TAG);
        RequestManager.setJsonRequest(requestBean, handleResponse(callback));
    }

    private <T> JsonCallback handleResponse(final IModelAsyncResponse<T> callback) {
        return new JsonCallback() {
            @Override
            public void onResponse(int what, JSONObject jsonObject, Response response) {
                if (what == REQUEST_REGISTER) {
                    // Test code begin
                    if (!"local".equalsIgnoreCase(BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER)) {
                        jsonObject = getRegisterAccountData();
                    }
                    // Test code end
                    if (jsonObject.optBoolean(LoginConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(LoginConstants.DATA), new TypeToken<AccountBean>() {
                        }.getType());
                        if (callback != null) {
                            callback.onResponse(retData);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFail(new MessageException(jsonObject.optString("message")));
                        }
                    }
                }
            }

            @Override
            public boolean onFail(int what, Exception e, Response response) {
                if (callback != null) {
                    return callback.onFail(e);
                }
                return false;
            }

            @Override
            public void onCancel(int what) {
                if (callback != null) {
                    callback.onCancel();
                }
            }
        };
    }

    // Test code begin
    private JSONObject getRegisterAccountData() {
        String res = "{success:true,code:200,message:'',data:" +
                "{id:'" + 100 + "',account:'测试注册用户', password:'111aaa'}}";
        try {
            return new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
    // Test code end
}
