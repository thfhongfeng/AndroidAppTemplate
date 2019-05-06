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
import com.pine.welcome.bean.VersionEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public class VersionModel {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private static final int HTTP_QUERY_VERSION_INFO = 1;

    public boolean requestUpdateVersionData(@NonNull IModelAsyncResponse<VersionEntity> callback) {
        String url = WelcomeUrlConstants.Query_Version_Data;
        JsonCallback httpStringCallback = handleHttpResponse(callback);
        return RequestManager.setJsonRequest(url, new HashMap<String, String>(),
                TAG, HTTP_QUERY_VERSION_INFO, httpStringCallback);
    }

    private <T> JsonCallback handleHttpResponse(final IModelAsyncResponse<T> callback) {
        return new JsonCallback() {
            @Override
            public void onResponse(int what, JSONObject jsonObject) {
                if (HTTP_QUERY_VERSION_INFO == what) {
                    // Test code begin
                    if (!"local".equalsIgnoreCase(BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER)) {
                        jsonObject = getUpdateVersionData();
                    }
                    // Test code end
                    if (jsonObject.optBoolean(WelcomeConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(WelcomeConstants.DATA), new TypeToken<VersionEntity>() {
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
    private JSONObject getUpdateVersionData() {
        String res = "{success:true,code:200,message:'',data:" +
                "{packageName:'com.pine.template', 'versionCode':2," +
                "versionName:'1.0.2',minSupportedVersion:1," +
                "force:false, fileName:'pine_app_template-V1.0.2-release.apk', " +
                "path:'http://yanyangtian.purang.com/download/bsd_purang.apk'}}";
        try {
            return new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
    // Test code end
}
