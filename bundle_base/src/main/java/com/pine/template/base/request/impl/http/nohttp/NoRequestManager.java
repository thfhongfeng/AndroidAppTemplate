package com.pine.template.base.request.impl.http.nohttp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.tool.request.DownloadRequestBean;
import com.pine.tool.request.IRequestManager;
import com.pine.tool.request.IResponseListener;
import com.pine.tool.request.RequestBean;
import com.pine.tool.request.RequestMethod;
import com.pine.tool.request.Response;
import com.pine.tool.request.UploadRequestBean;
import com.pine.tool.util.LogUtils;
import com.yanzhenjie.nohttp.BasicBinary;
import com.yanzhenjie.nohttp.BasicRequest;
import com.yanzhenjie.nohttp.Binary;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.InputStreamBinary;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OnUploadListener;
import com.yanzhenjie.nohttp.URLConnectionNetworkExecutor;
import com.yanzhenjie.nohttp.cache.DBCacheStore;
import com.yanzhenjie.nohttp.cookie.DBCookieStore;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.error.NetworkError;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.tools.HeaderUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.ConnectException;
import java.net.HttpCookie;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public class NoRequestManager implements IRequestManager {
    private final static String TAG = LogUtils.makeLogTag(NoRequestManager.class);
    private static volatile NoRequestManager mInstance;
    private static String mMobileModel = "android";
    private static HashMap<String, String> mHeaderParams = new HashMap<>();
    private RequestQueue mRequestQueue;
    private DownloadQueue mDownloadQueue;
    private HashMap<String, String> mSessionIdMap = new HashMap<>();

    private Handler mCheckProgressHandler;

    private NoRequestManager() {
        Logger.setDebug(true);// 开启NoHttp的调试模式, 配置后可看到请求过程、日志和错误信息。
        Logger.setTag("NoHttp");// 打印Log的tag
        mCheckProgressHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                LogUtils.d(TAG, "request progress timeout: " + msg.obj + ", try cancel it");
                if (msg.obj != null && msg.obj instanceof RequestBean) {
                    RequestBean requestBean = (RequestBean) msg.obj;
                    if (requestBean != null) {
                        requestBean.setRequestState(RequestBean.REQUEST_STATE_TIMEOUT);
                        cancelBySign(requestBean.getSign());
                    }
                }
            }
        };
    }

    public static NoRequestManager getInstance() {
        if (mInstance == null) {
            synchronized (NoRequestManager.class) {
                if (mInstance == null) {
                    LogUtils.releaseLog(TAG, "use request: nohttp");
                    mInstance = new NoRequestManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 标准回调请求
     */
    private OnResponseListener getResponseListener(final IResponseListener.OnResponseListener listener,
                                                   final RequestBean requestBean) {
        return new OnResponseListener() {
            @Override
            public void onStart(int what) {
                if (requestBean.getRequestType() != RequestType.DOWNLOAD
                        || requestBean.getRequestType() != RequestType.UPLOAD) {
                    requestBean.setRequestState(RequestBean.REQUEST_STATE_START);
                }
                listener.onStart(what);
            }

            @Override
            public void onSucceed(int what, com.yanzhenjie.nohttp.rest.Response response) {
                Response httpResponse = new Response();
                httpResponse.setSucceed(response.isSucceed());
                httpResponse.setResponseCode(response.responseCode());
                httpResponse.setTag(response.getTag());
                httpResponse.setData(response.get());
                httpResponse.setException(response.getException());
                httpResponse.setHeaders(response.getHeaders().toResponseHeaders());
                List<HttpCookie> list = response.getHeaders().getCookies();
                HashMap<String, String> cookies = new HashMap<>();
                for (int i = 0; i < list.size(); i++) {
                    HttpCookie cookie = list.get(i);
                    cookies.put(list.get(i).getName(), list.get(i).getValue());
                    if (SESSION_ID.equals(cookie.getName().toUpperCase())) {
                        setSessionId(requestBean.getSysTag(), cookie.getValue());
                    }
                }
                httpResponse.setCookies(cookies);
                listener.onSucceed(what, httpResponse);
            }

            @Override
            public void onFailed(int what, com.yanzhenjie.nohttp.rest.Response response) {
                if (requestBean.getRequestType() != RequestType.DOWNLOAD
                        || requestBean.getRequestType() != RequestType.UPLOAD) {
                    requestBean.setRequestState(RequestBean.REQUEST_STATE_FAIL);
                }
                Response httpResponse = new Response();
                httpResponse.setSucceed(response.isSucceed());
                httpResponse.setResponseCode(response.responseCode());
                httpResponse.setTag(response.getTag());
                httpResponse.setData(response.get());
                httpResponse.setException(response.getException() instanceof NetworkError ?
                        new ConnectException(response.getException().getMessage()) : response.getException());
                httpResponse.setHeaders(response.getHeaders().toResponseHeaders());
                List<HttpCookie> list = response.getHeaders().getCookies();
                HashMap<String, String> cookies = new HashMap<>();
                for (int i = 0; i < list.size(); i++) {
                    HttpCookie cookie = list.get(i);
                    cookies.put(list.get(i).getName(), list.get(i).getValue());
                    if (SESSION_ID.equals(cookie.getName().toUpperCase())) {
                        setSessionId(requestBean.getSysTag(), cookie.getValue());
                    }
                }
                httpResponse.setCookies(cookies);
                listener.onFailed(what, httpResponse);
            }

            @Override
            public void onFinish(int what) {
                if (requestBean.getRequestType() != RequestType.DOWNLOAD
                        || requestBean.getRequestType() != RequestType.UPLOAD) {
                    requestBean.setRequestState(RequestBean.REQUEST_STATE_FAIL);
                }
                listener.onFinish(what);
            }
        };
    }

    // 下载callback
    private DownloadListener getDownloadListener(final IResponseListener.OnDownloadListener listener,
                                                 final RequestBean requestBean) {
        return new DownloadListener() {

            @Override
            public void onDownloadError(int what, Exception exception) {
                mCheckProgressHandler.removeMessages(requestBean.hashCode());
                requestBean.setRequestState(RequestBean.REQUEST_STATE_FAIL);
                listener.onDownloadError(what, exception instanceof NetworkError ? new ConnectException(exception.getMessage()) : exception);
            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
                requestBean.setRequestState(RequestBean.REQUEST_STATE_START);
                listener.onStart(what, isResume, rangeSize, responseHeaders.toResponseHeaders(), allCount);
            }

            @Override
            public void onProgress(int what, int progress, long fileCount, long speed) {
                int msgWhat = requestBean.hashCode();
                mCheckProgressHandler.removeMessages(msgWhat);
                mCheckProgressHandler.sendMessageDelayed(Message.obtain(
                        mCheckProgressHandler, msgWhat, requestBean),
                        requestBean.getProgressTimeout());
                listener.onProgress(what, progress, fileCount, speed);
            }

            @Override
            public void onFinish(int what, String filePath) {
                mCheckProgressHandler.removeMessages(requestBean.hashCode());
                requestBean.setRequestState(RequestBean.REQUEST_STATE_FINISH);
                listener.onFinish(what, filePath);
            }

            @Override
            public void onCancel(int what) {
                mCheckProgressHandler.removeMessages(requestBean.hashCode());
                if (requestBean.getRequestState() != RequestBean.REQUEST_STATE_TIMEOUT) {
                    requestBean.setRequestState(RequestBean.REQUEST_STATE_CANCEL);
                }
                listener.onCancel(what);
            }
        };
    }

    private OnUploadListener getUploadListener(final IResponseListener.OnUploadListener listener,
                                               final RequestBean requestBean,
                                               final UploadRequestBean.FileBean fileBean) {
        return new OnUploadListener() {
            @Override
            public void onStart(int what) {
                requestBean.setRequestState(RequestBean.REQUEST_STATE_START);
                listener.onStart(what, fileBean);
            }

            @Override
            public void onCancel(int what) {
                mCheckProgressHandler.removeMessages(requestBean.hashCode());
                if (requestBean.getRequestState() != RequestBean.REQUEST_STATE_TIMEOUT) {
                    requestBean.setRequestState(RequestBean.REQUEST_STATE_CANCEL);
                }
                listener.onCancel(what, fileBean);
            }

            @Override
            public void onProgress(int what, int progress) {
                int msgWhat = requestBean.hashCode();
                mCheckProgressHandler.removeMessages(msgWhat);
                mCheckProgressHandler.sendMessageDelayed(Message.obtain(
                        mCheckProgressHandler, msgWhat, requestBean.getSign()),
                        requestBean.getProgressTimeout());
                listener.onProgress(what, fileBean, progress);
            }

            @Override
            public void onFinish(int what) {
                mCheckProgressHandler.removeMessages(requestBean.hashCode());
                requestBean.setRequestState(RequestBean.REQUEST_STATE_FINISH);
                listener.onFinish(what, fileBean);
            }

            @Override
            public void onError(int what, Exception exception) {
                mCheckProgressHandler.removeMessages(requestBean.hashCode());
                requestBean.setRequestState(RequestBean.REQUEST_STATE_FAIL);
                listener.onError(what, fileBean, exception instanceof NetworkError ? new ConnectException(exception.getMessage()) : exception);
            }
        };
    }

    @Override
    public void init(@NonNull Context context, HashMap<String, String> header) {
        mHeaderParams = header;

        DBCookieStore dbCookieStore = (DBCookieStore) new DBCookieStore(context).setEnable(true);
        dbCookieStore.setCookieStoreListener(new DBCookieStore.CookieStoreListener() {
            // 当NoHttp的Cookie被保存的时候被调用
            @Override
            public void onSaveCookie(URI uri, HttpCookie cookie) {
                if (SESSION_ID.equals(cookie.getName().toUpperCase())) {
                    cookie.setMaxAge(HeaderUtils.getMaxExpiryMillis());
                }
//                LogUtils.d(TAG, "onCookieSave url:" + uri.toString() +
//                        "\r\ncookie:" + cookie.toString());
            }

            // 当NoHttp的Cookie过期时被删除时此方法被调用
            @Override
            public void onRemoveCookie(URI uri, HttpCookie cookie) {
                LogUtils.d(TAG, "onCookieRemove url:" + uri.toString() +
                        "\r\ncookie:" + cookie.toString());
            }
        });

        // NoHttp初始化
        InitializationConfig.Builder configBuilder = InitializationConfig.newBuilder(context)
                // 全局连接服务器超时时间，单位毫秒，默认10s。
                .connectionTimeout(10 * 1000)
                // 全局等待服务器响应超时时间，单位毫秒，默认10s。
                .readTimeout(10 * 1000)
                // 配置缓存，默认保存数据库DBCacheStore，保存到SD卡使用DiskCacheStore。
                .cacheStore(
                        // 如果不使用缓存，setEnable(false)禁用。
                        new DBCacheStore(context).setEnable(false)
                )
                // 配置Cookie，默认保存数据库DBCookieStore，开发者可以自己实现CookieStore接口。
                .cookieStore(dbCookieStore)
                // 配置网络层，默认URLConnectionNetworkExecutor，可以自定义，实现NetworkExecutor即可。
                .networkExecutor(new URLConnectionNetworkExecutor())
                // 全局通用Header，add是添加，多次调用add不会覆盖上次add。
                .addHeader(MOBILE_MODEL_KEY, mMobileModel);
        if (mHeaderParams != null && mHeaderParams.size() > 0) {
            Collection keys = mHeaderParams.keySet();
            for (Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
                Object key = iterator.next();
                configBuilder.addHeader(key.toString(), mHeaderParams.get(key));
            }
        }
        configBuilder
                // 全局SSLSocketFactory。不要使用默认的（有些sl_ciphers会有问题）
                .sslSocketFactory(getSSLSocketFactory())
                // 全局HostnameVerifier。
                .hostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        LogUtils.d(TAG, "HostnameVerifier hostname:" + hostname +
                                "\r\nSSLSession:" + session.toString());
                        return true;
                    }
                })
                // 全局重试次数，配置后每个请求失败都会重试x次。
                .retry(0);
        NoHttp.initialize(configBuilder.build());

        mRequestQueue = NoHttp.newRequestQueue();
        mDownloadQueue = NoHttp.newDownloadQueue();
    }

    /**
     * Android 8.0（API26）才开始支持TLS1.3，因此如果服务器要求最低TLS1.3。请确保不要使用Android 8.0（API26）以下版本，否则会报：
     * javax.net.ssl.SSLHandshakeException: Read error: ssl=0xa2e07728: Failure in SSL library, usually a protocol error。
     * 对于服务器只支持TLS1.3的情况下，只能使用Android 8.0（API26）及以上的版本。
     *
     * @return
     */
    private SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {   // 信任所有证书
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            }}, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setBytesRequest(@NonNull RequestBean requestBean, @NonNull IResponseListener.OnResponseListener listener) {
        BasicRequest request = NoHttp.createByteArrayRequest(requestBean.getUrl(),
                transferToNoHttpHttpMethod(requestBean.getRequestMethod()));
        if (requestBean.getSign() != null) {
            request.setCancelSign(requestBean.getSign());
        }
        insertExtraRequestParams(request, requestBean);
        mRequestQueue.add(requestBean.getWhat(), (Request) addParams(requestBean.getUrl(), request,
                requestBean.getParams()), getResponseListener(listener, requestBean));
    }

    @Override
    public void setStringRequest(@NonNull RequestBean requestBean, @NonNull IResponseListener.OnResponseListener listener) {
        BasicRequest request = NoHttp.createStringRequest(requestBean.getUrl(),
                transferToNoHttpHttpMethod(requestBean.getRequestMethod()));
        if (requestBean.getSign() != null) {
            request.setCancelSign(requestBean.getSign());
        }
        insertExtraRequestParams(request, requestBean);
        mRequestQueue.add(requestBean.getWhat(), (Request) addParams(requestBean.getUrl(), request,
                requestBean.getParams()), getResponseListener(listener, requestBean));
    }

    @Override
    public void setBitmapRequest(@NonNull RequestBean requestBean, @NonNull IResponseListener.OnResponseListener listener) {
        BasicRequest request = NoHttp.createImageRequest(requestBean.getUrl(),
                transferToNoHttpHttpMethod(requestBean.getRequestMethod()));
        if (requestBean.getSign() != null) {
            request.setCancelSign(requestBean.getSign());
        }
        insertExtraRequestParams(request, requestBean);
        mRequestQueue.add(requestBean.getWhat(), (Request) addParams(requestBean.getUrl(), request,
                requestBean.getParams()), getResponseListener(listener, requestBean));
    }

    @Override
    public void setDownloadRequest(@NonNull DownloadRequestBean requestBean, @NonNull IResponseListener.OnDownloadListener listener) {
        BasicRequest request = NoHttp.createDownloadRequest(requestBean.getUrl(),
                transferToNoHttpHttpMethod(requestBean.getRequestMethod()),
                requestBean.getSaveFolder(), requestBean.getSaveFileName(),
                requestBean.isContinue(), requestBean.isDeleteOld());
        if (requestBean.getSign() != null) {
            request.setCancelSign(requestBean.getSign());
        }
        insertExtraRequestParams(request, requestBean);
        mDownloadQueue.add(requestBean.getWhat(), (DownloadRequest) addParams(requestBean.getUrl(), request,
                requestBean.getParams()), getDownloadListener(listener, requestBean));
    }

    @Override
    public void setUploadRequest(@NonNull UploadRequestBean requestBean, @NonNull IResponseListener.OnUploadListener processListener,
                                 @NonNull IResponseListener.OnResponseListener responseListener) {
        if (requestBean.getUploadFileList() == null) {
            return;
        }
        Request<String> request = NoHttp.createStringRequest(requestBean.getUrl(), com.yanzhenjie.nohttp.RequestMethod.POST);
        List<Binary> binaries = new ArrayList<>();
        boolean isMulFileKey = TextUtils.isEmpty(requestBean.getUpLoadFileKey());
        for (int i = 0; i < requestBean.getUploadFileList().size(); i++) {
            UploadRequestBean.FileBean fileBean = requestBean.getUploadFileList().get(i);
            BasicBinary binary = null;
            try {
                binary = new InputStreamBinary(new FileInputStream(fileBean.getFile()), fileBean.getFileName());
                binary.setUploadListener(fileBean.getWhat(), getUploadListener(processListener, requestBean, fileBean));
                binaries.add(binary);
                if (isMulFileKey) {
                    request.add(TextUtils.isEmpty(fileBean.getFileKey()) ? "file" + i : fileBean.getFileKey(), binary);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (!isMulFileKey) {
            request.add(requestBean.getUpLoadFileKey(), binaries);
        }
        if (requestBean.getParams() != null) {
            Iterator<Map.Entry<String, String>> iterator = requestBean.getParams().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String entryKey = entry.getKey();
                String entryValue = entry.getValue();
                request = (Request<String>) request.add(entryKey, entryValue);
            }
        }
        request.setCancelSign(requestBean.getSign());

        insertExtraRequestParams(request, requestBean);
        mRequestQueue.add(requestBean.getWhat(), request, getResponseListener(responseListener, requestBean));
    }

    private void insertExtraRequestParams(BasicRequest request, RequestBean requestBean) {
        HashMap<String, String> headerParams = requestBean.getHeaderParam();
        if (headerParams != null && headerParams.size() > 0) {
            Collection keys = headerParams.keySet();
            for (Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
                Object key = iterator.next();
                request.addHeader(key.toString(), headerParams.get(key));
            }
        }
        if (requestBean.getConnectTimeout() > 0) {
            request.setConnectTimeout(requestBean.getConnectTimeout());
        }
        if (requestBean.getReadTimeout() > 0) {
            request.setReadTimeout(requestBean.getReadTimeout());
        }
    }

    @Override
    public void cancelBySign(Object sign) {
        mRequestQueue.cancelBySign(sign);
        mDownloadQueue.cancelBySign(sign);
    }

    @Override
    public void cancelAll() {
        mRequestQueue.cancelAll();
        mDownloadQueue.cancelAll();
    }

    @Override
    public String getSessionId(String sysTag) {
        return mSessionIdMap.get(sysTag);
    }

    @Override
    public void setSessionId(String sysTag, String sessionId) {
        mSessionIdMap.put(sysTag, sessionId);
    }

    @Override
    public void clearCookie() {
        NoHttp.getInitializeConfig().getCookieManager().getCookieStore().removeAll();
    }

    @Override
    public Map<String, String> getLastSessionCookie() {
        List<HttpCookie> list = NoHttp.getInitializeConfig().getCookieManager().getCookieStore().getCookies();
        HashMap<String, String> cookies = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            cookies.put(list.get(i).getName(), list.get(i).getValue());
        }
        return cookies;
    }

    // 添加参数
    private BasicRequest addParams(@NonNull String requestUrl, BasicRequest request, Map<String, String> params) {
        if (params == null) {
            return request;
        }
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String entryKey = entry.getKey();
            String entryValue = entry.getValue();
            if (!requestUrl.contains("&" + entryKey + "=")
                    && !requestUrl.contains("?" + entryKey + "=")) {
                request = request.add(entryKey, entryValue);
            }
        }
        return request;
    }

    private com.yanzhenjie.nohttp.RequestMethod transferToNoHttpHttpMethod(RequestMethod method) {
        switch (method.toString().toUpperCase()) {
            case "GET":
                return com.yanzhenjie.nohttp.RequestMethod.GET;
            case "POST":
                return com.yanzhenjie.nohttp.RequestMethod.POST;
            case "PUT":
                return com.yanzhenjie.nohttp.RequestMethod.PUT;
            case "DELETE":
                return com.yanzhenjie.nohttp.RequestMethod.DELETE;
            case "PATCH":
                return com.yanzhenjie.nohttp.RequestMethod.PATCH;
            case "OPTIONS":
                return com.yanzhenjie.nohttp.RequestMethod.OPTIONS;
            case "TRACE":
                return com.yanzhenjie.nohttp.RequestMethod.TRACE;
        }
        return null;
    }
}
