package com.pine.tool.request;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.pine.tool.R;
import com.pine.tool.request.IRequestManager.ActionType;
import com.pine.tool.request.IRequestManager.RequestType;
import com.pine.tool.request.callback.DownloadCallback;
import com.pine.tool.request.callback.JsonCallback;
import com.pine.tool.request.callback.UploadCallback;
import com.pine.tool.request.interceptor.IRequestInterceptor;
import com.pine.tool.request.interceptor.IResponseInterceptor;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;
import com.yanzhenjie.nohttp.error.NetworkError;

import java.io.File;
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
    private static IRequestManager mRequestManager;

    // 正在进行的请求
    private static Map<String, RequestBean> mLoadingRequestMap = null;
    // 错误返回的请求
    private static Map<String, RequestBean> mErrorRequestMap = null;

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
        mRequestManager = factory.makeRequestManager(context, head);
        mLoadingRequestMap = new HashMap<>();
        mErrorRequestMap = new HashMap<>();
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
    public static boolean setJsonRequest(String url, Map<String, String> params, String moduleTag, JsonCallback callback) {
        return setJsonRequest(url, params, moduleTag, -1, RequestType.STRING, callback);
    }

    // json请求
    public static boolean setJsonRequest(String url, Map<String, String> params, String moduleTag, int what, JsonCallback callback) {
        return setJsonRequest(url, params, moduleTag, what, IRequestManager.RequestType.STRING, callback);
    }

    // json请求
    public static boolean setJsonRequest(String url, Map<String, String> params, String moduleTag, int what, boolean needLogin, JsonCallback callback) {
        return setJsonRequest(url, params, moduleTag, what, RequestType.STRING, needLogin, callback);
    }

    // json请求
    public static boolean setJsonRequest(String url, Map<String, String> params, String moduleTag, int what, Object sign, JsonCallback callback) {
        return setJsonRequest(url, RequestMethod.POST, params, moduleTag, what, sign, false, RequestType.STRING, callback);
    }

    // json请求
    public static boolean setJsonRequest(String url, Map<String, String> params, String moduleTag, int what, Object sign, boolean needLogin, JsonCallback callback) {
        return setJsonRequest(url, RequestMethod.POST, params, moduleTag, what, sign, needLogin, RequestType.STRING, callback);
    }

    // json请求
    public static boolean setJsonRequest(String url, RequestMethod method, Map<String, String> params, String moduleTag,
                                         int what, JsonCallback callback) {
        return setJsonRequest(url, method, params, moduleTag, what, null, false, RequestType.STRING, callback);
    }

    // json请求
    public static boolean setJsonRequest(RequestBean requestBean, ActionType actionType) {
        requestBean.setActionType(actionType);
        return setJsonRequest(requestBean.getUrl(), requestBean.getRequestMethod(), requestBean.getParams(),
                requestBean.getModuleTag(), requestBean.getWhat(), requestBean.getSign(), requestBean.isNeedLogin(),
                requestBean.getRequestType(), (JsonCallback) requestBean.getCallback());
    }

    // json请求
    public static boolean setJsonRequest(RequestBean requestBean) {
        return setJsonRequest(requestBean.getUrl(), requestBean.getRequestMethod(), requestBean.getParams(),
                requestBean.getModuleTag(), requestBean.getWhat(), requestBean.getSign(), requestBean.isNeedLogin(),
                requestBean.getRequestType(), (JsonCallback) requestBean.getCallback());
    }

    /**
     * json请求
     *
     * @param url         地址
     * @param method      请求方式：GET、POST等
     * @param params      参数
     * @param moduleTag   模块标识
     * @param what        请求标识code
     * @param sign        cancel标识
     * @param needLogin   是否需要登陆
     * @param requestType 请求分类，目前只区分通用和登录
     * @param callback    回调
     * @return false表示请求没有被发送出去；true表示请求正常发出
     */
    public static boolean setJsonRequest(String url, RequestMethod method, Map<String, String> params, String moduleTag,
                                         int what, Object sign, boolean needLogin, RequestType requestType, JsonCallback callback) {
        //设置模块名
        if (!TextUtils.isEmpty(moduleTag)) {
            callback.setModuleTag(moduleTag);
        }
        callback.setUrl(url);
        callback.setWhat(what);

        RequestBean requestBean = new RequestBean(what, callback);
        requestBean.setUrl(url);
        requestBean.setRequestMethod(method);
        requestBean.setParams(params);
        requestBean.setModuleTag(moduleTag);
        requestBean.setWhat(what);
        requestBean.setSign(sign);
        if (!TextUtils.isEmpty(moduleTag)) {
            requestBean.setModuleTag(moduleTag);
        }
        requestBean.setNeedLogin(needLogin);
        requestBean.setRequestType(requestType);
        requestBean.setCallback(callback);
        if (mRequestInterceptorList != null) {
            for (int i = 0; i < mRequestInterceptorList.size(); i++) {
                if (mRequestInterceptorList.get(i).onIntercept(what, requestBean)) {
                    callback.onCancel(what);
                    return false;
                }
            }
        }
        mLoadingRequestMap.put(requestBean.getKey(), requestBean);

        LogUtils.d(TAG, "Request in json queue - " + requestBean.getModuleTag() +
                "(what:" + requestBean.getWhat() + ")" + "\r\n- url: " +
                requestBean.getUrl() + "\r\n- params:" + requestBean.getParams() +
                "\r\n- Cookies: " + getCookiesLog());
        mRequestManager.setJsonRequest(requestBean, getResponseListener(requestBean.getKey(), callback));
        return true;
    }

    // 下载文件
    public static boolean setDownloadRequest(String url, String fileFolder, String fileName,
                                             int what, Object sign, DownloadCallback callback) {
        return setDownloadRequest(url, fileFolder, fileName, RequestMethod.GET, new HashMap<String, String>(),
                null, false, true, what, sign, false, callback);
    }

    // 下载文件
    public static boolean setDownloadRequest(String url, String fileFolder, String fileName, String moduleTag,
                                             int what, DownloadCallback callback) {
        return setDownloadRequest(url, fileFolder, fileName, RequestMethod.GET, new HashMap<String, String>(),
                moduleTag, false, true, what, null, false, callback);
    }

    // 下载文件
    public static boolean setDownloadRequest(String url, String fileFolder, String fileName, String moduleTag,
                                             int what, boolean needLogin, DownloadCallback callback) {
        return setDownloadRequest(url, fileFolder, fileName, RequestMethod.GET, new HashMap<String, String>(),
                moduleTag, false, true, what, null, needLogin, callback);
    }

    // 下载文件
    public static boolean setDownloadRequest(String url, String fileFolder, String fileName,
                                             String moduleTag, boolean isContinue, boolean isDeleteOld,
                                             int what, boolean needLogin, DownloadCallback callback) {
        return setDownloadRequest(url, fileFolder, fileName, RequestMethod.GET, new HashMap<String, String>(),
                moduleTag, isContinue, isDeleteOld, what, null, needLogin, callback);
    }

    // 下载文件
    public static boolean setDownloadRequest(String url, String fileFolder, String fileName, RequestMethod method,
                                             HashMap<String, String> params, String moduleTag, boolean isContinue, boolean isDeleteOld,
                                             int what, boolean needLogin, DownloadCallback callback) {
        return setDownloadRequest(url, fileFolder, fileName, method, params, moduleTag, isContinue,
                isDeleteOld, what, null, needLogin, callback);
    }

    /**
     * 下载文件
     *
     * @param url         地址
     * @param fileFolder  下载文件保存目录
     * @param fileName    下载文件保存文件名
     * @param method      请求方式：GET、POST等
     * @param params      参数
     * @param moduleTag   模块标识
     * @param isContinue  是否继续之前的下载
     * @param isDeleteOld 是否删除之前的下载
     * @param what        请求标识code
     * @param sign        cancel标识
     * @param needLogin   是否需要登陆
     * @param callback    回调
     * @return false表示请求没有被发送出去；true表示请求正常发出
     */
    public static boolean setDownloadRequest(String url, String fileFolder, String fileName,
                                             RequestMethod method, HashMap<String, String> params,
                                             String moduleTag, boolean isContinue, boolean isDeleteOld,
                                             int what, Object sign, boolean needLogin, DownloadCallback callback) {
        //设置模块名
        if (!TextUtils.isEmpty(moduleTag)) {
            callback.setModuleTag(moduleTag);
        }
        callback.setUrl(url);
        callback.setWhat(what);

        RequestBean requestBean = new RequestBean(what, callback);
        requestBean.setUrl(url);
        requestBean.setSaveFolder(fileFolder);
        requestBean.setSaveFileName(fileName);
        requestBean.setRequestMethod(method);
        requestBean.setParams(params);
        requestBean.setModuleTag(moduleTag);
        requestBean.setContinue(isContinue);
        requestBean.setDeleteOld(isDeleteOld);
        requestBean.setWhat(what);
        requestBean.setSign(sign);
        if (!TextUtils.isEmpty(moduleTag)) {
            requestBean.setModuleTag(moduleTag);
        }
        requestBean.setRequestType(RequestType.DOWNLOAD);
        requestBean.setNeedLogin(needLogin);
        requestBean.setCallback(callback);

        if (mRequestInterceptorList != null) {
            for (int i = 0; i < mRequestInterceptorList.size(); i++) {
                if (mRequestInterceptorList.get(i).onIntercept(what, requestBean)) {
                    callback.onCancel(what);
                    return false;
                }
            }
        }
        mLoadingRequestMap.put(requestBean.getKey(), requestBean);

        LogUtils.d(TAG, "Request in download queue - " + requestBean.getModuleTag() +
                "(what:" + requestBean.getWhat() + ")" + "\r\n- url: " +
                requestBean.getUrl() + "\r\n -params: " + requestBean.getParams() +
                "\r\n- Cookies: " + getCookiesLog());
        mRequestManager.setDownloadRequest(requestBean, getDownloadListener(requestBean.getKey(), callback));
        return true;
    }

    /**
     * 上传单个文件
     */
    public static boolean setUploadRequest(String url, Map<String, String> params,
                                           String fileKey, String fileName, File file,
                                           int what, Object sign,
                                           UploadCallback processCallback, JsonCallback requestCallback) {
        return setUploadRequest(url, params, null, fileKey, fileName, file, what, sign,
                false, processCallback, requestCallback);
    }

    /**
     * 上传单个文件
     */
    public static boolean setUploadRequest(String url, Map<String, String> params, String moduleTag,
                                           String fileKey, String fileName, File file,
                                           int what, Object sign, boolean needLogin,
                                           UploadCallback processCallback,
                                           JsonCallback requestCallback) {
        ArrayList<RequestBean.FileBean> fileList = new ArrayList<>();
        RequestBean.FileBean fileBean = new RequestBean.FileBean(fileKey,
                fileName, file, 0);
        fileList.add(fileBean);
        ArrayList<String> fileNameList = new ArrayList<>();
        fileNameList.add(fileName);
        return setUploadRequest(url, params, moduleTag, null, fileList, what,
                sign, needLogin, processCallback, requestCallback);
    }

    /**
     * 上传多个文件
     */
    public static boolean setUploadRequest(String url, Map<String, String> params,
                                           List<RequestBean.FileBean> httpFileList,
                                           int what, Object sign,
                                           UploadCallback processCallback,
                                           JsonCallback requestCallback) {
        return setUploadRequest(url, params, null, null, httpFileList,
                what, sign, false, processCallback, requestCallback);
    }

    /**
     * 上传多个文件
     */
    public static boolean setUploadRequest(String url, Map<String, String> params, String fileKey,
                                           List<RequestBean.FileBean> httpFileList,
                                           int what, Object sign,
                                           UploadCallback processCallback,
                                           JsonCallback requestCallback) {
        return setUploadRequest(url, params, null, fileKey, httpFileList,
                what, sign, false, processCallback, requestCallback);
    }

    /**
     * 上传多个文件
     *
     * @param url             地址
     * @param params          普通参数
     * @param moduleTag       模块标识
     * @param fileKey         文件的key
     * @param httpFileList    上传文件集合
     * @param what            请求标识code
     * @param sign            用于取消的sign
     * @param needLogin       是否需要登陆
     * @param processCallback
     * @param requestCallback
     * @return false表示请求没有被发送出去；true表示请求正常发出
     */
    public static boolean setUploadRequest(String url, Map<String, String> params, String moduleTag,
                                           String fileKey, List<RequestBean.FileBean> httpFileList,
                                           int what, Object sign, boolean needLogin,
                                           UploadCallback processCallback, JsonCallback requestCallback) {
        if (!TextUtils.isEmpty(moduleTag)) {
            requestCallback.setModuleTag(moduleTag);
            processCallback.setModuleTag(moduleTag);
        }
        requestCallback.setUrl(url);
        processCallback.setUrl(url);
        requestCallback.setWhat(what);
        processCallback.setWhat(what);

        RequestBean requestBean = new RequestBean(what, requestCallback);
        requestBean.setUrl(url);
        requestBean.setUploadFileList(httpFileList);
        requestBean.setUpLoadFileKey(fileKey);
        requestBean.setRequestMethod(RequestMethod.POST);
        requestBean.setParams(params);
        requestBean.setWhat(what);
        requestBean.setSign(sign);
        if (!TextUtils.isEmpty(moduleTag)) {
            requestBean.setModuleTag(moduleTag);
        }
        requestBean.setRequestType(RequestType.UPLOAD);
        requestBean.setNeedLogin(needLogin);
        requestBean.setCallback(requestCallback);
        if (mRequestInterceptorList != null) {
            for (int i = 0; i < mRequestInterceptorList.size(); i++) {
                if (mRequestInterceptorList.get(i).onIntercept(what, requestBean)) {
                    requestCallback.onCancel(what);
                    return false;
                }
            }
        }
        mLoadingRequestMap.put(requestBean.getKey(), requestBean);

        LogUtils.d(TAG, "Request in upload queue - " + requestBean.getModuleTag() +
                "(what:" + requestBean.getWhat() + ")" + "\r\n- url: " +
                requestBean.getUrl() + "\r\n- params: " + requestBean.getParams() +
                "\r\n- Cookies: " + getCookiesLog());
        mRequestManager.setUploadRequest(requestBean, getUploadListener(processCallback),
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
                        "\r\n- response: " + response.getData() +
                        "\r\n- Cookies: " + getCookiesLog());
                RequestBean requestBean = null;
                if (mLoadingRequestMap != null && mLoadingRequestMap.containsKey(requestKey)) {
                    requestBean = mLoadingRequestMap.remove(requestKey);
                    requestBean.setResponse(response);
                }
                if (mResponseInterceptorList != null) {
                    for (int i = 0; i < mResponseInterceptorList.size(); i++) {
                        if (mResponseInterceptorList.get(i).onIntercept(what, requestBean, response)) {
                            return;
                        }
                    }
                }
                callback.onResponse(what, response);
            }

            @Override
            public void onFailed(int what, Response response) {
                LogUtils.d(TAG, "Response onFailed in json queue - " + callback.getModuleTag() +
                        "(what:" + what + ")" + "\r\n- url: " + callback.getUrl() +
                        "\r\n- response: " + response.getData() +
                        "\r\n- Cookies: " + getCookiesLog());
                RequestBean requestBean = null;
                if (mLoadingRequestMap != null && mLoadingRequestMap.containsKey(requestKey)) {
                    if (mErrorRequestMap == null) {
                        mErrorRequestMap = new HashMap<>();
                    }
                    if (mErrorRequestMap.containsKey(requestKey)) {
                        mErrorRequestMap.remove(requestKey);
                    }
                    mErrorRequestMap.put(requestKey, mLoadingRequestMap.get(requestKey));
                    requestBean = mLoadingRequestMap.remove(requestKey);
                    requestBean.setResponse(response);
                }
                if (mResponseInterceptorList != null) {
                    for (int i = 0; i < mResponseInterceptorList.size(); i++) {
                        if (mResponseInterceptorList.get(i).onIntercept(what, requestBean, response)) {
                            return;
                        }
                    }
                }
                Exception exception = response.getException();
                if (!callback.onFail(what, exception)) {
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
                    if (mErrorRequestMap == null) {
                        mErrorRequestMap = new HashMap<>();
                    }
                    if (mErrorRequestMap.containsKey(requestKey)) {
                        mErrorRequestMap.remove(requestKey);
                    }
                    mErrorRequestMap.put(requestKey, mLoadingRequestMap.get(requestKey));
                    mLoadingRequestMap.remove(requestKey);
                }
                if (!callback.onError(what, exception)) {
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
                callback.onStart(what, isResume, rangeSize, allCount);
            }

            @Override
            public void onProgress(int what, int progress, long fileCount, long speed) {
                callback.onProgress(what, progress, fileCount, speed);
            }

            @Override
            public void onFinish(int what, String filePath) {
                LogUtils.d(TAG, "Response onFinish in download queue - " + callback.getModuleTag() +
                        "(what:" + what + ")" + "\r\n- filePath: " + filePath);
                if (mLoadingRequestMap != null && mLoadingRequestMap.containsKey(requestKey)) {
                    mLoadingRequestMap.remove(requestKey);
                }
                callback.onFinish(what, filePath);
            }

            @Override
            public void onCancel(int what) {
                LogUtils.d(TAG, "Response onCancel in download queue - " + callback.getModuleTag() +
                        "(what:" + what + ")");
                if (mLoadingRequestMap != null && mLoadingRequestMap.containsKey(requestKey)) {
                    mLoadingRequestMap.remove(requestKey);
                }
                callback.onCancel(what);
            }
        };
    }

    public static IResponseListener.OnUploadListener getUploadListener(final UploadCallback callback) {
        return new IResponseListener.OnUploadListener() {
            @Override
            public void onStart(int what, RequestBean.FileBean fileBean) {
                LogUtils.d(TAG, "Response onStart in upload queue - " + callback.getModuleTag() +
                        "(what:" + what + ")" + "\r\n- url: " + callback.getUrl() +
                        "\r\n- Cookies: " + getCookiesLog());
                callback.onStart(what, fileBean);
            }

            @Override
            public void onCancel(int what, RequestBean.FileBean fileBean) {
                callback.onCancel(what, fileBean);
            }

            @Override
            public void onProgress(int what, RequestBean.FileBean fileBean, int progress) {
                callback.onProgress(what, fileBean, progress);
            }

            @Override
            public void onFinish(int what, RequestBean.FileBean fileBean) {
                LogUtils.d(TAG, "Response onFinish in upload queue - " + callback.getModuleTag() +
                        "(what:" + what + ")");
                callback.onFinish(what, fileBean);
            }

            @Override
            public void onError(int what, RequestBean.FileBean fileBean, Exception exception) {
                LogUtils.d(TAG, "Response onError in upload queue - " + callback.getModuleTag() +
                        "(what:" + what + ")" + "\r\n- exception: " + exception.toString());
                if (!callback.onError(what, fileBean, exception)) {
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

    //重新发起一次已失败的网络请求
    public static void reloadErrorRequest(String key) {
        if (mErrorRequestMap == null) {
            return;
        }
        RequestBean bean = mErrorRequestMap.get(key);
        if (bean == null) {
            return;
        }
        bean.setActionType(ActionType.RETRY_WHEN_ERROR);
        if (bean.getRequestType() == RequestType.UPLOAD) {
            mRequestManager.setUploadRequest(bean, getUploadListener((UploadCallback) bean.getCallback()),
                    getResponseListener(bean.getKey(), (JsonCallback) bean.getCallback()));
        } else if (bean.getRequestType() == RequestType.DOWNLOAD) {
            mRequestManager.setDownloadRequest(bean, getDownloadListener(bean.getKey(), (DownloadCallback) bean.getCallback()));
        } else {
            mRequestManager.setJsonRequest(bean, getResponseListener(bean.getKey(), (JsonCallback) bean.getCallback()));
        }
    }

    //重新发起所有失败的网络请求
    public static void reloadAllErrorRequest() {
        if (mErrorRequestMap == null) {
            return;
        }
        Iterator<String> iterator = mErrorRequestMap.keySet().iterator();
        while (iterator.hasNext()) {
            reloadErrorRequest(iterator.next());
        }
    }

    // 根据sign标识中断对应网络请求
    public static void cancelBySign(Object sign) {
        mRequestManager.cancelBySign(sign);
    }

    // 中断所有网络请求
    public static void cancelAll() {
        mRequestManager.cancelAll();
        mLoadingRequestMap = new HashMap<>();
    }

    //获取正在进行中的网络请求数
    public static int getLoadingRequestCount() {
        return mLoadingRequestMap == null ? 0 : mLoadingRequestMap.size();
    }

    // 获取请求失败的网络请求数
    public static int getErrorRequestCount() {
        return mErrorRequestMap == null ? 0 : mErrorRequestMap.size();
    }

    //获取所有正在进行中的网络请求
    public static Map<String, RequestBean> getAllLoadingRequest() {
        return mLoadingRequestMap;
    }

    //获取所有请求失败的网络请求
    public static Map<String, RequestBean> getAllErrorRequest() {
        return mErrorRequestMap;
    }

    public static String getSessionId() {
        return mRequestManager.getSessionId();
    }

    public static void setSessionId(String sessionId) {
        mRequestManager.setSessionId(sessionId);
    }

    public static Map<String, String> getSessionCookie() {
        return mRequestManager.getSessionCookie();
    }

    public static void clearCookie() {
        mRequestManager.clearCookie();
    }

    private static String getCookiesLog() {
        Map<String, String> cookies = getSessionCookie();
        String cookiesStr = "";
        if (cookies != null && cookies.size() > 0) {
            Iterator<Map.Entry<String, String>> iterator = cookies.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                cookiesStr += entry.getKey() + ":" + entry.getValue() + ",";
            }
            cookiesStr = cookiesStr.substring(0, cookiesStr.length() - 1);
        }
        return cookiesStr;
    }
}
