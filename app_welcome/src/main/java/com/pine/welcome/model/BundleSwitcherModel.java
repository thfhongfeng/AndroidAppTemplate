package com.pine.welcome.model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.base.architecture.mvp.model.IModelAsyncResponse;
import com.pine.base.request.RequestManager;
import com.pine.base.request.callback.JsonCallback;
import com.pine.config.BuildConfig;
import com.pine.tool.util.LogUtils;
import com.pine.welcome.WelcomeConstants;
import com.pine.welcome.WelcomeUrlConstants;
import com.pine.welcome.bean.BundleSwitcherEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class BundleSwitcherModel {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private static final int HTTP_REQUEST_QUERY_BUNDLE_SWITCHER = 1;

    public boolean requestBundleSwitcherData(@NonNull IModelAsyncResponse<ArrayList<BundleSwitcherEntity>> callback) {
        String url = WelcomeUrlConstants.Query_BundleSwitcher_Data;
        JsonCallback httpStringCallback = handleHttpResponse(callback);
        return RequestManager.setJsonRequest(url, new HashMap<String, String>(),
                TAG, HTTP_REQUEST_QUERY_BUNDLE_SWITCHER, httpStringCallback);
    }

    private <T> JsonCallback handleHttpResponse(final IModelAsyncResponse<T> callback) {
        return new JsonCallback() {
            @Override
            public void onResponse(int what, JSONObject jsonObject) {
                if (HTTP_REQUEST_QUERY_BUNDLE_SWITCHER == what) {
                    // Test code begin
                    if (!"local".equalsIgnoreCase(BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER)) {
                        jsonObject = getBundleSwitcherData();
                    }
                    // Test code end
                    if (jsonObject.optBoolean(WelcomeConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(WelcomeConstants.DATA),
                                new TypeToken<ArrayList<BundleSwitcherEntity>>() {
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

    // Test code begin
    private JSONObject getBundleSwitcherData() {
        String res = "{success:true,code:200,message:'',data:" +
                "[{configKey:'login_bundle', open:true},{configKey:'main_bundle', open:true}," +
                "{configKey:'user_bundle', open:true},{configKey:'business_mvc_bundle', open:true}," +
                "{configKey:'business_mvp_bundle', open:true},{configKey:'business_mvvm_bundle', open:true}," +
                "{configKey:'business_demo_bundle', open:true}]}";
        try {
            return new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
    // Test code end
}
