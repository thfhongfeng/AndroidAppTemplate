package com.pine.template.base.browser;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pine.template.base.browser.hdmiin.HdmiInManager;
import com.pine.template.base.browser.hdmiin.IOnPlayEventListener;
import com.pine.template.bundle_base.R;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.UrlUtils;

import java.util.List;

public class WebBrowser extends FrameLayout {
    private final String TAG = this.getClass().getSimpleName();

    private Context mContext;
    private Handler mMainHandler;

    private String mLastFailUrl;

    private View rootView;
    private BrowserWebView webView;
    private FrameLayout hdmiinFrame;
    private WebControllerView controllerView;
    private WebLoadingView webLoadingView;
    private View errView;
    private ScreenSaverView screenSaverView;

    private final int UI_CONTENT = 0;
    private final int UI_ERR = 1;
    private final int UI_LOADING = 2;
    private volatile int mCurShowUI = -1;

    private boolean mEnableErrUI = true;

    public WebBrowser(@NonNull Context context) {
        super(context);
        init(context);
    }

    public WebBrowser(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WebBrowser(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(@NonNull Context context) {
        mContext = context;
        mMainHandler = new Handler();
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.web_browser_view, this, true);
        webView = findViewById(R.id.web_view);
        hdmiinFrame = findViewById(R.id.hdmiin_frame);
        errView = findViewById(R.id.err_view);
        webLoadingView = findViewById(R.id.loading_view);
        controllerView = findViewById(R.id.controller);
        screenSaverView = findViewById(R.id.screen_saver);
    }

    public void init(Activity activity, String startUrl, boolean matchWH, boolean autoLoad) {
        init(activity, startUrl, matchWH, autoLoad, null, null);
    }

    public void init(Activity activity, boolean matchWH, boolean autoLoad, WebViewListener listener) {
        init(activity, "", matchWH, autoLoad, null, listener);
    }

    public void init(Activity activity, String startUrl, boolean matchWH, boolean autoLoad, Class injectedCls) {
        init(activity, startUrl, matchWH, autoLoad, injectedCls, null);
    }

    public void init(Activity activity, String startUrl, boolean matchWH, boolean autoLoad, WebViewListener listener) {
        init(activity, startUrl, matchWH, autoLoad, null, listener);
    }

    public void init(Activity activity, boolean matchWH, boolean autoLoad, Class injectedCls, WebViewListener listener) {
        init(activity, "", matchWH, autoLoad, injectedCls, listener);
    }

    public void init(Activity activity, String startUrl, boolean matchWH, boolean autoLoad,
                     Class injectedCls, WebViewListener listener) {
        webView.init(activity, startUrl,matchWH, autoLoad, injectedCls, new WebViewListener() {
            @Override
            public void onUrlStartLoad(String url) {
                LogUtils.d(TAG, "onUrlStartLoad url:" + url + ", view hash code:" + hashCode());
                if (webView.isHttpOrFileUrl(url) && mCurShowUI != UI_ERR) {
                    showLoadingUi(true);
                }
                if (listener != null) {
                    listener.onUrlStartLoad(url);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtils.d(TAG, "shouldOverrideUrlLoading url:" + url + ", view hash code:" + hashCode());
                if (listener != null) {
                    return listener.shouldOverrideUrlLoading(view, url);
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public boolean onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtils.d(TAG, "onPageStarted url:" + url + ", view hash code:" + hashCode());
                if (mCurShowUI != UI_ERR) {
                    showLoadingUi(!TextUtils.isEmpty(mLastFailUrl));
                }
                if (listener != null) {
                    return listener.onPageStarted(view, url, favicon);
                }
                return super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onProgressChange(WebView view, int newProgress) {
                if (listener != null) {
                    listener.onProgressChange(view, newProgress);
                }
            }

            @Override
            public boolean onPageFinished(WebView view, String url) {
                LogUtils.d(TAG, "onPageFinished url:" + url
                        + ", mLastFailUrl:" + mLastFailUrl
                        + ", view hash code:" + hashCode());
                if (!mEnableErrUI || !UrlUtils.urlEquals(mLastFailUrl, url)) {
                    mMainHandler.removeCallbacks(loadingTimeoutRun);
                    showContentUi();
                }
                mLastFailUrl = "";
                if (listener != null) {
                    return listener.onPageFinished(view, url);
                }
                return super.onPageFinished(view, url);
            }

            @Override
            public boolean onReceivedError(WebView view, WebResourceRequest request,
                                           WebResourceError error) {
                boolean extraNeed404FailUrl = extraNeed404FailUrl(request.getUrl().toString());
                LogUtils.d(TAG, "onReceivedError fail url:" + request.getUrl().toString()
                        + ", loading url:" + webView.getUrl()
                        + ", extraNeed404FailUrl:" + extraNeed404FailUrl
                        + ", isForMainFrame:" + request.isForMainFrame()
                        + ", err code:" + error.getErrorCode()
                        + ", view hash code:" + hashCode());
                if (mEnableErrUI && (request.isForMainFrame()
                        || UrlUtils.urlEquals(request.getUrl().toString(), webView.getUrl())
                        || extraNeed404FailUrl)) {
                    showErrorUi();
                    mLastFailUrl = request.getUrl().toString();
                }
                if (listener != null) {
                    listener.onReceivedError(view, request, error);
                }
                return true;
            }

            @Override
            public boolean onReceivedHttpError(WebView view, WebResourceRequest request,
                                               WebResourceResponse errorResponse) {
                boolean extraNeed404FailUrl = extraNeed404FailUrl(request.getUrl().toString());
                LogUtils.d(TAG, "onReceivedHttpError fail url:" + request.getUrl().toString()
                        + ", loading url:" + webView.getUrl()
                        + ", extraNeed404FailUrl:" + extraNeed404FailUrl
                        + ", isForMainFrame:" + request.isForMainFrame()
                        + ", err code:" + errorResponse.getStatusCode()
                        + ", view hash code:" + hashCode());
                if (mEnableErrUI && (request.isForMainFrame()
                        || (UrlUtils.urlEquals(request.getUrl().toString(), webView.getUrl())
                        || extraNeed404FailUrl))) {
                    showErrorUi();
                    mLastFailUrl = request.getUrl().toString();
                }
                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message) {
                if (listener != null) {
                    return listener.onJsAlert(view, url, message);
                }
                return super.onJsAlert(view, url, message);
            }
        });
    }

    private List<String> mExtraNeed404FailUrlList;

    private boolean extraNeed404FailUrl(String failUrl) {
        if (mExtraNeed404FailUrlList == null || mExtraNeed404FailUrlList.size() < 1) {
            return false;
        }
        for (String url : mExtraNeed404FailUrlList) {
            if (UrlUtils.urlEquals(url, failUrl)) {
                return true;
            }
        }
        return false;
    }

    public void setConfig(BrowserConfig config) {
        mExtraNeed404FailUrlList = config.getExtraUrlNeed404List();
        ScreenSaverConfig screenSaverConfig = config.getScreensaverConfig();
        if (screenSaverConfig != null) {
            LogUtils.d(TAG, "has screen save, init it");
//            String path = getContext().getExternalFilesDir(null).getAbsolutePath() + "/assets/slides/screensaver/";
            screenSaverView.init(this, screenSaverConfig.getType(), screenSaverConfig.getFilePath(),
                    screenSaverConfig.getIdleTime());
        } else {
            screenSaverView.release(this);
        }
    }

    public void enableWebController(WebControllerListener listener) {
        if (controllerView == null || webView == null) {
            return;
        }
        controllerView.unregister();
        controllerView.register(this, listener);
    }

    public void disableWebController() {
        if (controllerView == null || webView == null) {
            return;
        }
        controllerView.unregister();
    }

    public void enableErrUI(boolean enable) {
        mEnableErrUI = enable;
    }

    private boolean mLastStop = false;
    private Handler mHdmiInHandler = new Handler(Looper.getMainLooper());

    public void startUrl(@NonNull String url) {
        mHdmiInHandler.post(new Runnable() {
            @Override
            public void run() {
                releaseAllHdmiIn();
                webView.loadUrl(url);
            }
        });
    }

    public void refresh() {
        mHdmiInHandler.post(new Runnable() {
            @Override
            public void run() {
                releaseAllHdmiIn();
                webView.reload();
            }
        });
    }

    public boolean initHdmiIn(@NonNull String tag,
                              int left, int top, int width, int height, int scaleType) {
        releaseHdmiIn(tag);
        LogUtils.d(TAG, "initHdmiIn tag:" + tag + ", left:" + left + ", top:" + top
                + ", width:" + width + ", height:" + height + ", scaleType:" + scaleType);
        boolean success = HdmiInManager.getInstance().initHdmiIn(tag, hdmiinFrame, left, top,
                width, height, scaleType);
        if (success) {
            hdmiinFrame.setVisibility(VISIBLE);
            HdmiInManager.getInstance().setOnPlayEventListener(tag, new IOnPlayEventListener() {
                @Override
                public void onSurfacePrepared() {
//                    webView.callJsJavascript("onSurfacePrepared", "", null);
                }

                @Override
                public void onHdmiInStart() {
//                    webView.callJsJavascript("onHdmiInStart", "", null);
                }

                @Override
                public void onHdmiInError() {
//                    webView.callJsJavascript("onHdmiInError", "", null);
                }
            });
        }
        return success;
    }

    public boolean startHdmiIn(@NonNull String tag) {
        LogUtils.d(TAG, "startHdmiIn tag:" + tag);
        return HdmiInManager.getInstance().startHdmiIn(tag);
    }

    public boolean stopHdmiIn(@NonNull String tag) {
        LogUtils.d(TAG, "stopHdmiIn tag:" + tag);
        return HdmiInManager.getInstance().stopHdmiIn(tag);
    }

    public boolean releaseHdmiIn(@NonNull String tag) {
        LogUtils.d(TAG, "releaseHdmiIn tag:" + tag);
        return HdmiInManager.getInstance().releaseHdmiIn(tag, hdmiinFrame);
    }

    public boolean releaseAllHdmiIn() {
        LogUtils.d(TAG, "releaseAllHdmiIn");
        return HdmiInManager.getInstance().releaseAllHdmiIn(hdmiinFrame);
    }

    public void onStart() {
        if (mLastStop && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            LogUtils.d(TAG, "reload when onStart from ui stop for low version (<R) android");
            refresh();
        }
    }

    public void onStop() {
        mLastStop = true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        screenSaverView.release(this);
        mMainHandler.removeCallbacks(mContentUiShortDelayRun);
        mMainHandler.removeCallbacks(mScheduleGoHomeRun);
        mMainHandler.removeCallbacks(mCheckStayContentUiRun);
        mMainHandler.removeCallbacks(loadingTimeoutRun);
        releaseAllHdmiIn();
        mHdmiInHandler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    private Runnable loadingTimeoutRun = new Runnable() {
        @Override
        public void run() {
            showTimeoutUi();
        }
    };

    private volatile int mScheduleGoHomeCount = 0;

    private Runnable mScheduleGoHomeRun = new Runnable() {
        @Override
        public void run() {
            if (mCurShowUI != UI_CONTENT) {
                LogUtils.d(TAG, "scheduleGoHome when ui is not content ui");
                webView.goHome();
            }
        }
    };

    private void scheduleGoHome() {
        mScheduleGoHomeCount++;
        long delay = Math.min(mScheduleGoHomeCount * 30 * 1000, 30 * 60 * 1000);
        LogUtils.d(TAG, "scheduleGoHome delay:" + delay);
        mMainHandler.removeCallbacks(mScheduleGoHomeRun);
        mMainHandler.postDelayed(mScheduleGoHomeRun, delay);
    }

    private void showTimeoutUi() {
        mCurShowUI = UI_ERR;
        errView.setVisibility(VISIBLE);
        webLoadingView.hide();
        LogUtils.d(TAG, "showTimeoutUi");
        scheduleGoHome();
    }

    private void showErrorUi() {
        mCurShowUI = UI_ERR;
        errView.setVisibility(VISIBLE);
        webLoadingView.hide();
        LogUtils.d(TAG, "showErrorUi");
        scheduleGoHome();
    }

    private void showLoadingUi(boolean hasBg) {
        mCurShowUI = UI_LOADING;
        mMainHandler.removeCallbacks(loadingTimeoutRun);
        mMainHandler.postDelayed(loadingTimeoutRun, 30000);
        errView.setVisibility(GONE);
        webLoadingView.show(hasBg);
        LogUtils.d(TAG, "showLoadingUi hasBg:" + hasBg);
    }

    private Runnable mCheckStayContentUiRun = new Runnable() {
        @Override
        public void run() {
            if (mCurShowUI == UI_CONTENT) {
                mScheduleGoHomeCount = 0;
            }
        }
    };

    private Runnable mContentUiShortDelayRun = new Runnable() {
        @Override
        public void run() {
            if (!checkWebViewState()) {
                return;
            }
            controllerView.enableGoBack(webView.canGoBack());
        }
    };

    private void showContentUi() {
        mCurShowUI = UI_CONTENT;
        errView.setVisibility(GONE);
        webLoadingView.setVisibility(GONE);
        mMainHandler.removeCallbacks(mContentUiShortDelayRun);
        mMainHandler.postDelayed(mContentUiShortDelayRun, 100);
        // 10秒后检查是否停留在内容页，如果是，说明页面正常
        mMainHandler.removeCallbacks(mCheckStayContentUiRun);
        mMainHandler.postDelayed(mCheckStayContentUiRun, 10 * 1000);
        LogUtils.d(TAG, "showContentUi");
    }

    private boolean checkWebViewState() {
        return isAttachedToWindow();
    }

    public BrowserWebView getWebView() {
        return webView;
    }
}
