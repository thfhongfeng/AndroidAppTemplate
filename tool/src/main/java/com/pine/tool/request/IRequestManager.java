package com.pine.tool.request;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public interface IRequestManager {
    String SESSION_ID = "JSESSIONID";
    String COOKIE_KEY = "Cookie";
    String MOBILE_MODEL_KEY = "mobileModel";

    /**
     * 初始化
     *
     * @param context
     * @param header
     */
    void init(Context context, HashMap<String, String> header);

    /**
     * Byte[]请求
     *
     * @param requestBean 请求实体
     * @param listener    请求响应监听
     */
    void setBytesRequest(@NonNull RequestBean requestBean,
                         @NonNull IResponseListener.OnResponseListener listener);

    /**
     * String请求
     *
     * @param requestBean 请求实体
     * @param listener    请求响应监听
     */
    void setStringRequest(@NonNull RequestBean requestBean,
                          @NonNull IResponseListener.OnResponseListener listener);

    /**
     * Bitmap请求
     *
     * @param requestBean 请求实体
     * @param listener    请求响应监听
     */
    void setBitmapRequest(@NonNull RequestBean requestBean,
                          @NonNull IResponseListener.OnResponseListener listener);

    /**
     * 下载请求
     *
     * @param requestBean 请求实体
     * @param listener    请求响应监听
     */
    void setDownloadRequest(@NonNull DownloadRequestBean requestBean, @NonNull IResponseListener.OnDownloadListener listener);

    /**
     * 上传请求
     *
     * @param requestBean      请求实体
     * @param processListener  文件上传响应监听
     * @param responseListener 上传请求响应监听
     */
    void setUploadRequest(@NonNull UploadRequestBean requestBean, @NonNull IResponseListener.OnUploadListener processListener,
                          @NonNull IResponseListener.OnResponseListener responseListener);

    /**
     * 取消sign标记的正在进行的请求
     *
     * @param sign 取消标识
     */
    void cancelBySign(Object sign);

    /**
     * 取消所有正在进行的请求
     */
    void cancelAll();

    /**
     * 获取最近一次请求的SessionCookies
     *
     * @return
     */
    Map<String, String> getLastSessionCookie();

    /**
     * 获取sysTag标识的sessionId
     *
     * @param sysTag 系统标识。默认为“main”，代表应用本体后台服务。用于多系统的请求的session管理
     * @return
     */
    String getSessionId(String sysTag);

    /**
     * 设置sessionId
     *
     * @param sysTag    sysTag 系统标识。默认为“main”，代表应用本体后台服务。用于多系统的请求的session管理
     * @param sessionId
     */
    void setSessionId(String sysTag, String sessionId);

    /**
     * 清理cookies
     */
    void clearCookie();

    enum RequestType {
        BYTES, // bytesRequest
        JSON, // jsonRequest
        STRING, // stringRequest
        BITMAP,   // bitmapRequest
        DOWNLOAD, // downloadRequest
        UPLOAD; //  uploadRequest

        @Override
        public String toString() {
            return super.toString();
        }
    }

    enum ActionType {
        COMMON, // common
        RETRY_AFTER_RE_LOGIN, //  retry after re-login
        RETRY_WHEN_ERROR     // retry when error
    }
}
