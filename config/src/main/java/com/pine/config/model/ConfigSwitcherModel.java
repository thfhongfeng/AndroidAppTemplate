package com.pine.config.model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.config.BuildConfig;
import com.pine.config.Constants;
import com.pine.config.UrlConstants;
import com.pine.config.bean.ConfigSwitcherEntity;
import com.pine.tool.architecture.mvp.model.IModelAsyncResponse;
import com.pine.tool.exception.BusinessException;
import com.pine.tool.request.RequestManager;
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

    public boolean requestBundleSwitcherData(@NonNull IModelAsyncResponse<ArrayList<ConfigSwitcherEntity>> callback) {
        String url = UrlConstants.Query_BundleSwitcher_Data;
        JsonCallback httpStringCallback = handleResponse(callback);
        return RequestManager.setJsonRequest(url, new HashMap<String, String>(),
                TAG, REQUEST_REQUEST_QUERY_BUNDLE_SWITCHER, httpStringCallback);
    }

    private <T> JsonCallback handleResponse(final IModelAsyncResponse<T> callback) {
        return new JsonCallback() {
            @Override
            public void onResponse(int what, JSONObject jsonObject) {
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
                            callback.onFail(new BusinessException(jsonObject.optString("message")));
                        }
                    }
                }
            }

            @Override
            public boolean onFail(int what, Exception e) {
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
        String res = "{success:true,code:200,message:'',data:" +
                "[{configKey:'bundle_welcome', state:1},{configKey:'bundle_login', state:1}," +
                "{configKey:'bundle_main', state:1},{configKey:'bundle_user', state:1}," +
                "{configKey:'bundle_business_mvc', state:1},{configKey:'bundle_business_mvp', state:1}," +
                "{configKey:'bundle_business_mvvm', state:1},{configKey:'business_demo_bundle', state:1}," +
                "{configKey:'fun_add_shop', state:1},{configKey:'fun_add_product', state:1}," +
                "{configKey:'fun_add_travel_note', state:1}]}";
        try {
            return new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
    // Test code end
}
