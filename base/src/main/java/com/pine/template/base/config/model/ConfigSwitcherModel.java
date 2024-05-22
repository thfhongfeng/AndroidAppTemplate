package com.pine.template.base.config.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.template.base.KeyConstants;
import com.pine.template.base.config.bean.ConfigSwitcherInfo;
import com.pine.tool.architecture.mvp.model.IModelAsyncResponse;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.RequestBean;
import com.pine.tool.request.RequestManager;
import com.pine.tool.request.Response;
import com.pine.tool.request.callback.JsonCallback;
import com.pine.tool.util.LogUtils;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class ConfigSwitcherModel {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private static final int REQUEST_REQUEST_QUERY_BUNDLE_SWITCHER = 1;

    public boolean requestBundleSwitcherData(final String configUrl, HashMap<String, String> params,
                                             @NonNull IModelAsyncResponse<ConfigSwitcherInfo> callback) {
        JsonCallback httpStringCallback = handleResponse(callback);
        RequestBean requestBean = new RequestBean(configUrl, REQUEST_REQUEST_QUERY_BUNDLE_SWITCHER, params);
        requestBean.setModuleTag(TAG);
        return RequestManager.setJsonRequest(requestBean, httpStringCallback);
    }

    private <T> JsonCallback handleResponse(final IModelAsyncResponse<T> callback) {
        return new JsonCallback() {
            @Override
            public void onResponse(int what, JSONObject jsonObject, Response response) {
                if (REQUEST_REQUEST_QUERY_BUNDLE_SWITCHER == what) {
                    if (jsonObject.optBoolean(KeyConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(KeyConstants.DATA),
                                new TypeToken<ConfigSwitcherInfo>() {
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
}
