package com.pine.template.welcome.updater;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.template.base.BaseUrlConstants;
import com.pine.template.welcome.WelcomeKeyConstants;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.RequestBean;
import com.pine.tool.request.RequestManager;
import com.pine.tool.request.RequestMethod;
import com.pine.tool.request.Response;
import com.pine.tool.request.callback.JsonCallback;
import com.pine.tool.request.response.IAsyncResponse;
import com.pine.tool.util.LogUtils;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public class VersionModel {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private static final int REQUEST_QUERY_VERSION_INFO = 1;

    private static final int REQUEST_UPDATE_CANCEL = 2;

    private static final int REQUEST_UPDATE_COMPLETE = 3;

    public boolean requestUpdateVersionData(final HashMap<String, String> params,
                                            @NonNull IAsyncResponse<VersionEntity> callback) {
        String url = BaseUrlConstants.APK_UPDATE();
        RequestBean requestBean = new RequestBean(RequestManager.buildGetUrl(url, params),
                REQUEST_QUERY_VERSION_INFO, new HashMap<String, String>());
        requestBean.setRequestMethod(RequestMethod.GET);
        requestBean.setModuleTag(TAG);
        return RequestManager.setJsonRequest(requestBean, handleResponse(callback));
    }

    public boolean requestUpdateCancel() {
        String url = BaseUrlConstants.APK_UPDATE();
        HashMap<String, String> params = new HashMap<>();
        // 1-取消了更新, 2-表示更新完成，空或者其它表示请求更新检查
        params.put("updateState", "1");
        RequestBean requestBean = new RequestBean(RequestManager.buildGetUrl(url, params),
                REQUEST_UPDATE_CANCEL, new HashMap<String, String>());
        requestBean.setRequestMethod(RequestMethod.GET);
        requestBean.setModuleTag(TAG);
        return RequestManager.setJsonRequest(requestBean, null);
    }

    public boolean requestUpdateComplete() {
        String url = BaseUrlConstants.APK_UPDATE();
        HashMap<String, String> params = new HashMap<>();
        // 1-取消了更新, 2-表示更新完成，空或者其它表示请求更新检查
        params.put("updateState", "2");
        RequestBean requestBean = new RequestBean(RequestManager.buildGetUrl(url, params),
                REQUEST_UPDATE_COMPLETE, new HashMap<String, String>());
        requestBean.setRequestMethod(RequestMethod.GET);
        requestBean.setModuleTag(TAG);
        return RequestManager.setJsonRequest(requestBean, null);
    }

    private <T> JsonCallback handleResponse(final IAsyncResponse<T> callback) {
        return new JsonCallback() {
            @Override
            public void onResponse(int what, JSONObject jsonObject, Response response) {
                if (REQUEST_QUERY_VERSION_INFO == what) {
                    if (jsonObject.optBoolean(WelcomeKeyConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(WelcomeKeyConstants.DATA),
                                new TypeToken<VersionEntity>() {
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
