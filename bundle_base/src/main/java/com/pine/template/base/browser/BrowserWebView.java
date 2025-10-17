package com.pine.template.base.browser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JsResult;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.pine.template.bundle_base.R;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.NetWorkUtils;
import com.pine.tool.util.UrlUtils;
import com.pine.tool.util.WebViewUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.pedant.SafeWebViewBridge.InjectedChromeClient;

public class BrowserWebView extends WebView {
    private static final String TAG = BrowserWebView.class.getSimpleName();

    private Activity mActivity;

    private boolean mIsUseJsGoBackHistory = false;
    private WebViewListener mListener;

    public String mHomeUrl;
    // 当前网页链接
    private String mRealUrl;
    public String mInitUrl;

    private int mWebViewState = 0;  //0 开始  1：正在加载ing  2:加载成功   3:加载失败
    private boolean mIsErrorViewState;

    private int mWidth, mHeight;

    public BrowserWebView(Context context) {
        super(context);
    }

    public BrowserWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BrowserWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Activity activity, String startUrl, boolean matchWH, boolean autoLoad) {
        init(activity, startUrl, matchWH, autoLoad, null, null);
    }

    public void init(Activity activity, String startUrl, boolean matchWH, boolean autoLoad, Class injectedCls) {
        init(activity, startUrl, matchWH, autoLoad, injectedCls, null);
    }

    public void init(Activity activity, String startUrl, boolean matchWH, boolean autoLoad, WebViewListener listener) {
        init(activity, startUrl, matchWH, autoLoad, null, listener);
    }

    public void init(Activity activity, String startUrl, boolean matchWH, boolean autoLoad, Class injectedCls, WebViewListener listener) {
        mListener = listener;
        mActivity = activity;
        initWebView(activity, injectedCls);

        if (matchWH) {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // 确保只执行一次
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                    mWidth = getWidth();
                    mHeight = getHeight();
                    LogUtils.d(TAG, "onGlobalLayout w:" + mWidth + ", h:" + mHeight);
                    if (!TextUtils.isEmpty(startUrl)) {
                        setUrl(startUrl);
                        setupUrlHistoryType();
                        if (autoLoad) {
                            loadUrl();
                        }
                    }
                }
            });
        } else {
            if (!TextUtils.isEmpty(startUrl)) {
                setUrl(startUrl);
                setupUrlHistoryType();
                if (autoLoad) {
                    loadUrl();
                }
            }
        }
    }

    private void initWebView(Activity activity, Class injectedCls) {
        InjectedChromeClient injectedChrome = null;
        if (injectedCls != null) {
            injectedChrome = new InjectedChromeClient("android", injectedCls) {
                @Override
                public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                    callback.invoke(origin, true, false);
                    super.onGeolocationPermissionsShowPrompt(origin, callback);
                }

                @Override
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    boolean exhaust = false;
                    if (mListener != null) {
                        exhaust = mListener.onJsAlert(view, url, message);
                    }
                    if (!exhaust) {
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    }
                    return super.onJsAlert(view, url, message, result);
                }

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (mListener != null) {
                        mListener.onProgressChange(view, newProgress);
                    }
                    super.onProgressChanged(view, newProgress);
                }
            };
        }
        // 禁止掉调试功能，需要时再打开
        setWebContentsDebuggingEnabled(true);
        WebViewUtils.setupCommonWebView(this, new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        }, injectedChrome, new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (isHttpOrFileUrl(url)) {
                    setUrl(url);
                    setupUrlHistoryType();
                }
                if (mListener != null) {
                    return mListener.shouldOverrideUrlLoading(view, url);
                } else {
                    return false;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mWebViewState = 1;   //正在加载
                if (mListener != null) {
                    mListener.onPageStarted(view, url, favicon);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebViewState = 2;   //加载成功
                setUrl(url);
                setupUrlHistoryType();
                super.onPageFinished(view, url);
                if (mListener != null) {
                    mListener.onPageFinished(view, url);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (UrlUtils.urlEquals(failingUrl, getUrl())) {
                    mWebViewState = 3;   //设置加载失败
                }
                boolean exhaust = false;
                if (mListener != null) {
                    exhaust = mListener.onReceivedError(view, errorCode, description, failingUrl);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (UrlUtils.urlEquals(request.getUrl().toString(), getUrl())) {
                    mWebViewState = 3;   //设置加载失败
                }
                boolean exhaust = false;
                if (mListener != null) {
                    exhaust = mListener.onReceivedError(view, request, error);
                }
                if (!exhaust) {
                    receiverError();
                }
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                mWebViewState = 3;   //设置加载失败
                boolean exhaust = false;
                if (mListener != null) {
                    exhaust = mListener.onReceivedHttpError(view, request, errorResponse);
                }
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                if (mActivity == null) {
                    handler.cancel();
                    return;
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                           SslError error) {
                // [高危]WebView未校验HTTPS证书
                // 风险详情：APP 的 WebView 证书认证错误时，未取消加载页面，存在中间人攻击风险。
                // 修复建议使用： handler.cancel() 停止加载问题页面。
                handler.proceed();
//                handler.cancel();
            }

            @Override
            public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                return super.onRenderProcessGone(view, detail);
            }
        });
    }

    private void setUrl(String url) {
        mInitUrl = url;
        if (mWidth > 0 && mHeight > 0) {
            if (url.contains("?")) {
                url = url + "&w=" + mWidth + "&h=" + mHeight;
            } else {
                url = url + "?w=" + mWidth + "&h=" + mHeight;
            }
        }
        if (TextUtils.isEmpty(mHomeUrl) && isHttpOrFileUrl(url)) {
            mHomeUrl = url;
            LogUtils.d(TAG, "set home url:" + mHomeUrl);
        }
        mRealUrl = url;
    }

    private void loadUrl() {
        if (!TextUtils.isEmpty(mRealUrl)) {
            super.loadUrl(mRealUrl);
            if (mListener != null) {
                mListener.onUrlStartLoad(mInitUrl);
            }
        }
    }

    public boolean isHttpOrFileUrl(String url) {
        return !TextUtils.isEmpty(url)
                && (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("file://"));
    }

    @Override
    public void loadUrl(@NonNull String url) {
        setUrl(url);
        super.loadUrl(mRealUrl);
        if (mListener != null) {
            mListener.onUrlStartLoad(mInitUrl);
        }
    }

    @Override
    public void loadUrl(@NonNull String url, @NonNull Map<String, String> additionalHttpHeaders) {
        setUrl(url);
        super.loadUrl(mRealUrl, additionalHttpHeaders);
        if (mListener != null) {
            mListener.onUrlStartLoad(mInitUrl);
        }
    }

    //收到错误反馈，如无网络，404等
    private void receiverError() {
        if (mActivity != null && !mActivity.isFinishing()) {
            if (NetWorkUtils.checkNetWork(mActivity)) {
                Toast.makeText(mActivity, getResources()
                        .getString(R.string.tool_server_err), Toast.LENGTH_SHORT).show();
            } else {
                //无网络
                Toast.makeText(mActivity, getResources()
                        .getString(R.string.tool_network_err), Toast.LENGTH_SHORT).show();
            }
        }
        mIsErrorViewState = true;
    }

    @Override
    public void onDetachedFromWindow() {
        mActivity = null;
        extraOnTouchListenerList.clear();
        destroy();
        super.onDetachedFromWindow();
    }

    private List<OnTouchListener> extraOnTouchListenerList = new ArrayList<>();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (extraOnTouchListenerList.size() > 0) {
            for (OnTouchListener item : extraOnTouchListenerList) {
                item.onTouch(this, event);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (extraOnTouchListenerList.size() > 0) {
            for (OnTouchListener item : extraOnTouchListenerList) {
                item.onTouch(this, event);
            }
        }
        return super.onGenericMotionEvent(event);
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        if (extraOnTouchListenerList.size() > 0) {
            for (OnTouchListener item : extraOnTouchListenerList) {
                item.onTouch(this, event);
            }
        }
        return super.onHoverEvent(event);
    }

    public void addOnTouchListener(OnTouchListener l) {
        extraOnTouchListenerList.add(l);
    }

    public void removeOnTouchListener(OnTouchListener l) {
        extraOnTouchListenerList.remove(l);
    }

    /**
     * 调用了webview 的goback 清除历史数据
     */
    private void callWebViewBack() {
        if (canGoBack()) {
            goBack();
        } else {
//            finishUi();
            return;
        }
    }

    /**
     * 判断一旦失败情况下 可以回退
     */
    private void callJsBack() {
        if (mIsErrorViewState) {
            callWebViewBack();
        } else {
            loadUrl(getJsGoBackFunStr());
        }
    }

    private void startActivity(Intent intent) {
        if (mActivity != null) {
            mActivity.startActivity(intent);
        }
    }

    private void startActivityForResult(Intent intent, int requestCode) {
        if (mActivity != null) {
            mActivity.startActivityForResult(intent, requestCode);
        }
    }

    private void sendBroadcast(Intent intent) {
        if (mActivity != null) {
            mActivity.sendBroadcast(intent);
        }
    }

    public boolean isPagePrepared() {
        return mWebViewState == 2;
    }

    private void finishUi() {
        if (mActivity != null && !mActivity.isFinishing()) {
            mActivity.finish();
        }
    }

    public void reloadUrl() {
        mIsErrorViewState = false;
        LogUtils.d(TAG, "reloadUrl url:" + mRealUrl + ", init url:" + mInitUrl);
        loadUrl();
    }

    public void goBackAction() {
        LogUtils.d(TAG, "goBackAction");
        if (mIsUseJsGoBackHistory && mWebViewState == 2) {
            callJsBack();
        } else {
            callWebViewBack();
        }
    }

    public void goHome() {
        mIsErrorViewState = false;
        mRealUrl = mHomeUrl;
        LogUtils.d(TAG, "goHome url:" + mRealUrl + ", init url:" + mInitUrl);
        loadUrl();
    }

    private void setupUrlHistoryType() {
        if (TextUtils.isEmpty(mRealUrl)) {
            String backTag = UrlUtils.getValueByNameFromUrl(mRealUrl, "isBlockPrBack");
            if ("1".equals(backTag)) {
                mIsUseJsGoBackHistory = true;
            } else {
                mIsUseJsGoBackHistory = false;
            }
        } else {
            mIsUseJsGoBackHistory = false;
        }
    }

    private void jsGoBack(JSONObject jsonData) {
        if (canGoBack()) {
            callWebViewBack();
        } else {
            loadUrl("about:blank");
            finishUi();
        }
    }

    private void jsGoBackToTarget(JSONObject jsonData) {
        JSONObject params = new JSONObject();
        if (jsonData.has("actionParam")) {
            params = jsonData.optJSONObject("actionParam");
        }
        if (params == null) {
            return;
        }
        setUrl(params.optString("targetHttpUrl"));
        setupUrlHistoryType();
        loadUrl();
    }

    private void jsGoBackToExit(JSONObject jsonData) {
        loadUrl("about:blank");
        finishUi();
    }

    private void jsRedirectToBrowser(JSONObject jsonData) {
        JSONObject params = new JSONObject();
        if (jsonData.has("actionParam")) {
            params = jsonData.optJSONObject("actionParam");
        }
        if (params == null) {
            return;
        }
        String webTagUrl = params.optString("targetUrl");
        Uri uri = Uri.parse(webTagUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private String getJsGoBackFunStr() {
        return "javascript:mobileAppCallBack()";
    }

    public void callJsJavascript(String funcName, String jsonParams,
                                 ValueCallback<String> callback) {
        jsonParams = jsonParams == null ? "" : jsonParams;
        String js = "javascript:" + funcName;
        if (!TextUtils.isEmpty(jsonParams)) {
            js += "('" + jsonParams + "')";
        } else {
            js += "()";
        }
        LogUtils.d(TAG, "javascript called " + js + ", view hash code:" + hashCode());
        evaluateJavascript(js, callback);
    }

    public void callJsJavascript(int cmdCode, String funcName, String jsonParams,
                                 final ICmdCallback callback) {
        jsonParams = jsonParams == null ? "" : jsonParams;
        String js = "javascript:" + funcName;
        if (!TextUtils.isEmpty(jsonParams)) {
            js += "('" + jsonParams + "')";
        } else {
            js += "()";
        }
        LogUtils.d(TAG, "javascript called " + js + ", view hash code:" + hashCode());
        evaluateJavascript(js, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                boolean success = !TextUtils.isEmpty(value) && !"null".equals(value.toLowerCase());
                if (callback != null) {
                    callback.onResponse(cmdCode, success, value);
                }
            }
        });
    }

    public interface ICmdCallback {
        void onResponse(int cmdCode, boolean success, String receiveValue);
    }
}
