package com.pine.tool.request;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public interface IRequestManager {
    String SESSION_ID = "JSESSIONID";
    String COOKIE_KEY = "Cookie";
    String MOBILE_MODEL_KEY = "mobileModel";

    void setJsonRequest(@NonNull RequestBean requestBean,
                        @NonNull IResponseListener.OnResponseListener listener);

    void setDownloadRequest(@NonNull RequestBean requestBean, @NonNull IResponseListener.OnDownloadListener listener);

    void setUploadRequest(@NonNull RequestBean requestBean, @NonNull IResponseListener.OnUploadListener processListener,
                          @NonNull IResponseListener.OnResponseListener responseListener);

    void cancelBySign(Object sign);

    void cancelAll();

    void addGlobalSessionCookie(HashMap<String, String> headerMap);

    void removeGlobalSessionCookie(List<String> keyList);

    Map<String, String> getSessionCookie();

    String getSessionId();

    void setSessionId(String sessionId);

    void clearCookie();

    enum RequestType {
        STRING, // stringRequest
        UPLOAD, //  uploadRequest
        DOWNLOAD, // downloadRequest
        BITMAP   // bitmapRequest
    }

    enum ActionType {
        COMMON, // common
        RETRY_AFTER_RE_LOGIN, //  retry after re-login
        RETRY_WHEN_ERROR     // retry when error
    }
}
