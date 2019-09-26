package com.pine.tool.request;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.pine.tool.R;
import com.pine.tool.request.IRequestManager.RequestType;
import com.pine.tool.request.callback.DownloadCallback;
import com.pine.tool.request.callback.JsonCallback;
import com.pine.tool.request.callback.UploadCallback;
import com.pine.tool.request.interceptor.IRequestInterceptor;
import com.pine.tool.request.interceptor.IResponseInterceptor;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;
import com.yanzhenjie.nohttp.error.NetworkError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/9/7.
 */

public class RequestManager {
    private final static String TAG = LogUtils.makeLogTag(RequestManager.class);
    private static Context mApplicationContext;
    private static IRequestManager mRequestManagerImpl;

    // 正在进行的请求
    private static HashMap<String, RequestBean> mLoadingRequestMap = null;

    private static List<IRequestInterceptor> mRequestInterceptorList = new ArrayList<>();

    private static List<IResponseInterceptor> mResponseInterceptorList = new ArrayList<>();

    public static void init(Context context, @NonNull IRequestManagerFactory factory) {
        init(context, new HashMap<String, String>(), factory);
    }

    /**
     * 初始化
     *
     * @param context
     * @param head
     */
    public static void init(Context context, HashMap<String, String> head, @NonNull IRequestManagerFactory factory) {
        if (context != null) {
            mApplicationContext = context;
        } else {
            mApplicationContext = AppUtils.getApplication();
        }
        mRequestManagerImpl = factory.makeRequestManager(context, head);
        mLoadingRequestMap = new HashMap<>();
    }

    public static void addGlobalResponseInterceptor(IResponseInterceptor interceptor) {
        if (!mResponseInterceptorList.contains(interceptor)) {
            mResponseInterceptorList.add(interceptor);
            LogUtils.releaseLog(TAG, "Global response interceptor: " + interceptor.getClass() + " was added");
        }
    }

    public static void addGlobalRequestInterceptor(IRequestInterceptor interceptor) {
        if (!mRequestInterceptorList.contains(interceptor)) {
            mRequestInterceptorList.add(interceptor);
            LogUtils.releaseLog(TAG, "Global request interceptor: " + interceptor.getClass() + " was added");
        }
    }

    // json请求
    public static boolean setJsonRequest(@NonNull RequestBean requestBean, JsonCallback callback) {
        //设置模块名
        if (!TextUtils.isEmpty(requestBean.getModuleTag())) {
            callback.setModuleTag(requestBean.getModuleTag());
        }
        callback.setUrl(requestBean.getUrl());
        callback.setWhat(requestBean.getWhat());

        requestBean.setRequestType(RequestType.STRING);
        requestBean.setCallback(callback);
        if (mRequestInterceptorList != null) {
            for (int i = 0; i < mRequestInterceptorList.size(); i++) {
                if (mRequestInterceptorList.get(i).onIntercept(requestBean.getWhat(), requestBean)) {
                    if (callback != null) {
                        callback.onCancel(requestBean.getWhat());
                    }
                    return false;
                }
            }
        }
        mLoadingRequestMap.put(requestBean.getKey(), requestBean);

        LogUtils.d(TAG, "Request in json queue - " + requestBean.getModuleTag() +
                "(what:" + requestBean.getWhat() + ")" + "\r\n- url: " +
                requestBean.getUrl() + "\r\n- params:" + requestBean.getParams() +
                "\r\n- Cookies: " + getCookiesLog());
        mRequestManagerImpl.setJsonRequest(requestBean, getResponseListener(requestBean.getKey(), callback));
        return true;
    }

    // 下载文件
    public static boolean setDownloadRequest(@NonNull DownloadRequestBean requestBean, DownloadCallback callback) {
        //设置模块名
        if (!TextUtils.isEmpty(requestBean.getModuleTag())) {
            callback.setModuleTag(requestBean.getModuleTag());
        }
        callback.setUrl(requestBean.getUrl());
        callback.setWhat(requestBean.getWhat());

        requestBean.setRequestType(RequestType.DOWNLOAD);
        requestBean.setCallback(callback);
        if (mRequestInterceptorList != null) {
            for (int i = 0; i < mRequestInterceptorList.size(); i++) {
                if (mRequestInterceptorList.get(i).onIntercept(requestBean.getWhat(), requestBean)) {
                    if (callback != null) {
                        callback.onCancel(requestBean.getWhat());
                    }
                    return false;
                }
            }
        }
        mLoadingRequestMap.put(requestBean.getKey(), requestBean);

        LogUtils.d(TAG, "Request in download queue - " + requestBean.getModuleTag() +
                "(what:" + requestBean.getWhat() + ")" + "\r\n- url: " +
                requestBean.getUrl() + "\r\n -params: " + requestBean.getParams() +
                "\r\n- Cookies: " + getCookiesLog());
        mRequestManagerImpl.setDownloadRequest(requestBean, getDownloadListener(requestBean.getKey(), callback));
        return true;
    }

    /**
     * 上传文件
     *
     * @param requestBean
     * @param processCallback
     * @param requestCallback
     * @return false表示请求没有被发送出去；true表示请求正常发出
     */
    public static boolean setUploadRequest(@NonNull UploadRequestBean requestBean,
                                           UploadCallback processCallback, JsonCallback requestCallback) {
        if (!TextUtils.isEmpty(requestBean.getModuleTag())) {
            requestCallback.setModuleTag(requestBean.getModuleTag());
            processCallback.setModuleTag(requestBean.getModuleTag());
        }
        requestCallback.setUrl(requestBean.getUrl());
        processCallback.setUrl(requestBean.getUrl());
        requestCallback.setWhat(requestBean.getWhat());
        processCallback.setWhat(requestBean.getWhat());

        requestBean.setRequestType(RequestType.UPLOAD);
        requestBean.setUploadCallback(processCallback);
        requestBean.setCallback(requestCallback);
        if (mRequestInterceptorList != null) {
            for (int i = 0; i < mRequestInterceptorList.size(); i++) {
                if (mRequestInterceptorList.get(i).onIntercept(requestBean.getWhat(), requestBean)) {
                    if (requestCallback != null) {
                        requestCallback.onCancel(requestBean.getWhat());
                    }
                    return false;
                }
            }
        }
        mLoadingRequestMap.put(requestBean.getKey(), requestBean);

        LogUtils.d(TAG, "Request in upload queue - " + requestBean.getModuleTag() +
                "(what:" + requestBean.getWhat() + ")" + "\r\n- url: " +
                requestBean.getUrl() + "\r\n- params: " + requestBean.getParams() +
                "\r\n- Cookies: " + getCookiesLog());
        mRequestManagerImpl.setUploadRequest(requestBean, getUploadListener(processCallback),
                getResponseListener(requestBean.getKey(), requestCallback));
        return true;
    }

    /**
     * 标准回调请求
     *
     * @param requestKey
     * @param callback
     * @return
     */
    public static IResponseListener.OnResponseListener getResponseListener(final String requestKey,
                                                                           final JsonCallback callback) {
        return new IResponseListener.OnResponseListener() {
            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, Response response) {
                LogUtils.d(TAG, "Response onSucceed in json queue - " + callback.getModuleTag() +
                        "(what:" + what + ")" + "\r\n- url: " + callback.getUrl() +
                        "\r\n- Cookies: " + getCookiesLog() +
                        "\r\n- response: " + getResponseLog(response));
                if (mLoadingRequestMap != null && mLoadingRequestMap.containsKey(requestKey)) {
                    RequestBean requestBean = mLoadingRequestMap.remove(requestKey);
                    requestBean.setResponse(response);
                    if (mResponseInterceptorList != null) {
                        for (int i = 0; i < mResponseInterceptorList.size(); i++) {
                            if (mResponseInterceptorList.get(i).onIntercept(what, requestBean, response)) {
                                return;
                            }
                        }
                    }
                }
                if (callback != null) {
                    callback.onResponse(what, response);
                }
            }

            @Override
            public void onFailed(int what, Response response) {
                LogUtils.d(TAG, "Response onFailed in json queue - " + callback.getModuleTag() +
                        "(what:" + what + ")" + "\r\n- url: " + callback.getUrl() +
                        "\r\n- Cookies: " + getCookiesLog() +
                        "\r\n- response: " + getResponseLog(response));
                if (mLoadingRequestMap != null && mLoadingRequestMap.containsKey(requestKey)) {
                    RequestBean requestBean = mLoadingRequestMap.remove(requestKey);
                    requestBean.setResponse(response);
                    if (mResponseInterceptorList != null) {
                        for (int i = 0; i < mResponseInterceptorList.size(); i++) {
                            if (mResponseInterceptorList.get(i).onIntercept(what, requestBean, response)) {
                                return;
                            }
                        }
                    }
                }
                Exception exception = response.getException();
                if (callback != null && !callback.onFail(what, exception)) {
                    defaultDeduceErrorResponse(exception);
                }
            }

            @Override
            public void onFinish(int what) {
                if (mLoadingRequestMap != null && mLoadingRequestMap.containsKey(requestKey)) {
                    mLoadingRequestMap.remove(requestKey);
                }
            }
        };
    }

    // 下载callback
    public static IResponseListener.OnDownloadListener getDownloadListener(final String requestKey,
                                                                           final DownloadCallback callback) {
        return new IResponseListener.OnDownloadListener() {
            @Override
            public void onDownloadError(int what, Exception exception) {
                LogUtils.d(TAG, "Response onDownloadError in download queue - " + callback.getModuleTag() +
                        "(what:" + what + ")" + "\r\n- exception: " + exception.toString());
                if (mLoadingRequestMap != null && mLoadingRequestMap.containsKey(requestKey)) {
                    mLoadingRequestMap.remove(requestKey);
                }
                if (callback != null && !callback.onError(what, exception)) {
                    defaultDeduceErrorResponse(exception);
                }
            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, long allCount) {
                LogUtils.d(TAG, "Response onStart in download queue - " + callback.getModuleTag() +
                        "(what:" + what + ")" + "\r\n- url: " + callback.getUrl() +
                        "\r\n- isResume: " + isResume +
                        "\r\n- rangeSize: " + rangeSize +
                        "\r\n- allCount: " + allCount +
                        "\r\n- Cookies: " + getCookiesLog());
                if (callback != null) {
                    callback.onStart(what, isResume, rangeSize, allCount);
                }
            }

            @Override
            public void onProgress(int what, int progress, long fileCount, long speed) {
                if (callback != null) {
                    callback.onProgress(what, progress, fileCount, speed);
                }
            }

            @Override
            public void onFinish(int what, String filePath) {
                LogUtils.d(TAG, "Response onFinish in download queue - " + callback.getModuleTag() +
                        "(what:" + what + ")" + "\r\n- filePath: " + filePath);
                if (mLoadingRequestMap != null && mLoadingRequestMap.containsKey(requestKey)) {
                    mLoadingRequestMap.remove(requestKey);
                }
                if (callback != null) {
                    callback.onFinish(what, filePath);
                }
            }

            @Override
            public void onCancel(int what) {
                LogUtils.d(TAG, "Response onCancel in download queue - " + callback.getModuleTag() +
                        "(what:" + what + ")");
                if (mLoadingRequestMap != null && mLoadingRequestMap.containsKey(requestKey)) {
                    mLoadingRequestMap.remove(requestKey);
                }
                if (callback != null) {
                    callback.onCancel(what);
                }
            }
        };
    }

    public static IResponseListener.OnUploadListener getUploadListener(final UploadCallback callback) {
        return new IResponseListener.OnUploadListener() {
            @Override
            public void onStart(int what, UploadRequestBean.FileBean fileBean) {
                LogUtils.d(TAG, "Response onStart in upload queue - " + callback.getModuleTag() +
                        "(what:" + what + ")" + "\r\n- url: " + callback.getUrl() +
                        "\r\n- Cookies: " + getCookiesLog());
                if (callback != null) {
                    callback.onStart(what, fileBean);
                }
            }

            @Override
            public void onCancel(int what, UploadRequestBean.FileBean fileBean) {
                if (callback != null) {
                    callback.onCancel(what, fileBean);
                }
            }

            @Override
            public void onProgress(int what, UploadRequestBean.FileBean fileBean, int progress) {
                if (callback != null) {
                    callback.onProgress(what, fileBean, progress);
                }
            }

            @Override
            public void onFinish(int what, UploadRequestBean.FileBean fileBean) {
                LogUtils.d(TAG, "Response onFinish in upload queue - " + callback.getModuleTag() +
                        "(what:" + what + ")");
                if (callback != null) {
                    callback.onFinish(what, fileBean);
                }
            }

            @Override
            public void onError(int what, UploadRequestBean.FileBean fileBean, Exception exception) {
                LogUtils.d(TAG, "Response onError in upload queue - " + callback.getModuleTag() +
                        "(what:" + what + ")" + "\r\n- exception: " + exception.toString());
                if (callback != null && !callback.onError(what, fileBean, exception)) {
                    defaultDeduceErrorResponse(exception);
                }
            }
        };
    }

    private static void defaultDeduceErrorResponse(Exception exception) {
        if (exception instanceof NetworkError) {
            Toast.makeText(mApplicationContext, mApplicationContext.getString(R.string.tool_network_err),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mApplicationContext, mApplicationContext.getString(R.string.tool_server_err) +
                            (TextUtils.isEmpty(exception.toString()) ? "" : ":" + exception.toString()),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // 根据sign标识中断对应网络请求
    public static void cancelBySign(Object sign) {
        mRequestManagerImpl.cancelBySign(sign);
    }

    // 中断所有网络请求
    public static void cancelAll() {
        mRequestManagerImpl.cancelAll();
        mLoadingRequestMap = new HashMap<>();
    }

    //获取正在进行中的网络请求数
    public static int getLoadingRequestCount() {
        return mLoadingRequestMap == null ? 0 : mLoadingRequestMap.size();
    }

    //获取所有正在进行中的网络请求
    public static Map<String, RequestBean> getAllLoadingRequest() {
        return mLoadingRequestMap;
    }

    public static String getSessionId() {
        return mRequestManagerImpl.getSessionId("main");
    }

    public static void setSessionId(String sessionId) {
        mRequestManagerImpl.setSessionId("main", sessionId);
    }

    public static Map<String, String> getLastSessionCookie() {
        return mRequestManagerImpl.getLastSessionCookie();
    }

    public String getSessionId(String sysTag) {
        return mRequestManagerImpl.getSessionId(sysTag);
    }

    public void setSessionId(String sysTag, String sessionId) {
        mRequestManagerImpl.setSessionId(sysTag, sessionId);
    }

    public static void clearCookie() {
        mRequestManagerImpl.clearCookie();
    }

    private static String getResponseLog(Response response) {
        if (response.getData() == null) {
            return "";
        }
        return response.getData().toString();
    }

    private static String getCookiesLog() {
        Map<String, String> cookies = getLastSessionCookie();
        String cookiesStr = "";
        if (cookies != null && cookies.size() > 0) {
            Iterator<Map.Entry<String, String>> iterator = cookies.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                cookiesStr += entry.getKey() + "=" + entry.getValue() + ";";
            }
            cookiesStr = cookiesStr.substring(0, cookiesStr.length() - 1);
        }
        return cookiesStr;
    }
}
