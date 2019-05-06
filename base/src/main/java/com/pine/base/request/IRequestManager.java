package com.pine.base.request;

import android.content.Context;

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

    IRequestManager init(Context context, HashMap<String, String> head);

    void setJsonRequest(RequestBean requestBean, IResponseListener.OnResponseListener listener);

    void setDownloadRequest(RequestBean requestBean, IResponseListener.OnDownloadListener listener);

    void setUploadRequest(RequestBean requestBean, IResponseListener.OnUploadListener processListener,
                          IResponseListener.OnResponseListener responseListener);

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
