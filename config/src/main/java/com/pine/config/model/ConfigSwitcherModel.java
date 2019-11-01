package com.pine.config.model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.config.BuildConfig;
import com.pine.config.ConfigApplication;
import com.pine.config.ConfigKey;
import com.pine.config.Constants;
import com.pine.config.UrlConstants;
import com.pine.config.bean.ConfigSwitcherEntity;
import com.pine.tool.architecture.mvp.model.IModelAsyncResponse;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.RequestBean;
import com.pine.tool.request.RequestManager;
import com.pine.tool.request.Response;
import com.pine.tool.request.callback.JsonCallback;
import com.pine.tool.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class ConfigSwitcherModel {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private static final int REQUEST_REQUEST_QUERY_BUNDLE_SWITCHER = 1;

    public boolean requestBundleSwitcherData(HashMap<String, String> params,
                                             @NonNull IModelAsyncResponse<ArrayList<ConfigSwitcherEntity>> callback) {
        String url = UrlConstants.Query_BundleSwitcher_Data;
        JsonCallback httpStringCallback = handleResponse(callback);
        RequestBean requestBean = new RequestBean(url, REQUEST_REQUEST_QUERY_BUNDLE_SWITCHER, params);
        requestBean.setModuleTag(TAG);
        return RequestManager.setJsonRequest(requestBean, httpStringCallback);
    }

    private <T> JsonCallback handleResponse(final IModelAsyncResponse<T> callback) {
        return new JsonCallback() {
            @Override
            public void onResponse(int what, JSONObject jsonObject, Response response) {
                if (REQUEST_REQUEST_QUERY_BUNDLE_SWITCHER == what) {
                    // Test code begin
                    if (!"local".equalsIgnoreCase(BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER)) {
                        jsonObject = getBundleSwitcherData();
                    }
                    // Test code end
                    if (jsonObject.optBoolean(Constants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(Constants.DATA),
                                new TypeToken<ArrayList<ConfigSwitcherEntity>>() {
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
    private JSONObject getBundleSwitcherData() {
        String content = "[{configKey:'" + ConfigKey.BUNDLE_WELCOME_KEY + "', state:1},{configKey:'" + ConfigKey.BUNDLE_LOGIN_KEY + "', state:1}," +
                "{configKey:'" + ConfigKey.BUNDLE_MAIN_KEY + "', state:1},{configKey:'" + ConfigKey.BUNDLE_USER_KEY + "', state:1}," +
                "{configKey:'" + ConfigKey.BUNDLE_BUSINESS_MVP_KEY + "', state:1}," +
                "{configKey:'" + ConfigKey.BUNDLE_BUSINESS_MVVM_KEY + "', state:1}," +
                "{configKey:'" + ConfigKey.FUN_ADD_SHOP_KEY + "', state:1},{configKey:'" + ConfigKey.FUN_ADD_PRODUCT_KEY + "', state:1}," +
                "{configKey:'" + ConfigKey.FUN_ADD_TRAVEL_NOTE_KEY + "', state:1}";
        if (ConfigApplication.isLogin()) {
            content += ",{configKey:'" + ConfigKey.BUNDLE_BUSINESS_MVC_KEY + "', state:1}";
        }
        content += "]";
        String res = "{success:true,code:200,message:'',data:" + content + "}";
        try {
            return new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
    // Test code end
}
