package com.pine.base.widget.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.pine.base.R;
import com.pine.base.component.share.bean.ShareBean;
import com.pine.base.component.share.manager.ShareManager;
import com.pine.base.remote.BaseRouterClient;
import com.pine.base.util.DialogUtils;
import com.pine.tool.request.IRequestManager;
import com.pine.tool.request.RequestManager;
import com.pine.tool.router.IRouterCallback;
import com.pine.tool.util.DensityUtils;
import com.pine.tool.util.ImageUtils;
import com.pine.tool.util.NetWorkUtils;
import com.pine.tool.util.UrlUtils;
import com.pine.tool.util.WebViewUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SafeWebViewBridge.InjectedChromeClient;

public class CommonWebView extends WebView {
    private Activity mActivity;
    private JsInterface mJsInterface;

    private boolean mIsUseJsGoBackHistory = false;
    private IWebViewListener mListener;

    // 当前网页链接
    private String mUrl;

    private int mWebViewState = 0;  //0 开始  1：正在加载ing  2:加载成功   3:加载失败
    private boolean mIsErrorViewState;

    private final int MAX_TRY_LOGIN_COUNT = 3;
    private int mTryLoginCount;

    public CommonWebView(Context context) {
        super(context);
    }

    public CommonWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Activity activity, String startUrl) {
        init(activity, startUrl, null);
    }

    public void init(Activity activity, String startUrl, IWebViewListener listener) {
        mListener = listener;
        mUrl = startUrl;
        setupUrlHistoryType();
        mActivity = activity;
        initWebView();
    }

    private void initWebView() {
        WebViewUtils.setupCommonWebView(this, new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        }, new InjectedChromeClient("appInterface", JsInterface.class) {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                if (mListener == null || !mListener.onJsAlert(view, url, message)) {
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
        }, new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http")) {
                    mUrl = url;
                    setupUrlHistoryType();
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mWebViewState = 1;   //正在加载
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebViewState = 2;   //加载成功
                mUrl = url;
                setupUrlHistoryType();
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                receiverError();
                mWebViewState = 3;   //设置加载失败
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                if (mActivity == null) {
                    handler.cancel();
                    return;
                }
                if (mTryLoginCount < MAX_TRY_LOGIN_COUNT) {
                    BaseRouterClient.autoLogin(mActivity, null, new IRouterCallback() {
                        @Override
                        public void onSuccess(Bundle responseBundle) {
                            loadUrl();
                        }

                        @Override
                        public boolean onFail(int code, String errorInfo) {
                            BaseRouterClient.goLoginActivity(mActivity, null, null);
                            return true;
                        }
                    });
                    mTryLoginCount++;
                } else {
                    BaseRouterClient.goLoginActivity(mActivity, null, null);
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
        });
        mJsInterface = new JsInterface(this);
        addJavascriptInterface(mJsInterface, "appInterface");
        if (!TextUtils.isEmpty(mUrl)) {
            loadUrl();
        }
    }

    private void loadUrl() {
        synCookies(mUrl);
        loadUrl(mUrl);
    }

    //为WebView设置请求的cookie
    private void synCookies(String url) {
        CookieSyncManager.createInstance(mActivity);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();
        cookieManager.setCookie(url,
                IRequestManager.SESSION_ID + "=" + RequestManager.getSessionId() + ";path=/;");
        CookieSyncManager.getInstance().sync();
    }

    //收到错误反馈，如无网络，404等
    private void receiverError() {
        if (mActivity != null && !mActivity.isFinishing()) {
            if (NetWorkUtils.checkNetWork(mActivity)) {
                DialogUtils.showConfirmDialog(mActivity, getResources()
                        .getString(R.string.tool_server_err), null);
            } else {
                //无网络
                DialogUtils.showConfirmDialog(mActivity, getResources()
                        .getString(R.string.tool_network_err), null);
            }
        }
        mIsErrorViewState = true;
    }

    @Override
    public void onDetachedFromWindow() {
        mJsInterface.clear();
        mActivity = null;
        destroy();
        super.onDetachedFromWindow();
    }

    /**
     * 调用了webview 的goback 清除历史数据
     */
    private void callWebViewBack() {
        if (canGoBack()) {
            goBack();
        } else {
            finishUi();
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

    private void finishUi() {
        if (mActivity != null && !mActivity.isFinishing()) {
            mActivity.finish();
        }
    }

    public void reload() {
        mIsErrorViewState = false;
        loadUrl();
    }

    public void goBackAction() {
        if (mIsUseJsGoBackHistory && mWebViewState == 2) {
            callJsBack();
        } else {
            callWebViewBack();
        }
    }

    private void goShare(ArrayList<ShareBean> shareList) {
        if (shareList != null && shareList.size() > 0) {
            ShareManager.getInstance().createShareDialog(mActivity, shareList).show();
        }
    }

    private void setupUrlHistoryType() {
        if (TextUtils.isEmpty(mUrl)) {
            String backTag = UrlUtils.getValueByNameFromUrl(mUrl, "isBlockPrBack");
            if ("1".equals(backTag)) {
                mIsUseJsGoBackHistory = true;
            } else {
                mIsUseJsGoBackHistory = false;
            }
        } else {
            mIsUseJsGoBackHistory = false;
        }
    }

    @SuppressLint("WrongThread")
    private void getSnapshot(boolean result, String name) {
        float scale = getScale();
        int webViewHeight = (int) (getContentHeight() * scale + 0.5);
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), webViewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        saveSnapshot(result, name, bitmap);
    }

    @SuppressLint("WrongThread")
    private void getSnapshot(boolean result, String name, int x, int y, int widthValue, int heightValue) {
        float scale = getScale();
        int webViewHeight = (int) (getContentHeight() * scale + 0.5);
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), webViewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        Bitmap bitmapEnd = Bitmap.createBitmap(bitmap, x, y, widthValue, heightValue);
        bitmap.recycle();
        saveSnapshot(result, name, bitmapEnd);
    }

    @SuppressLint("WrongThread")
    private void saveSnapshot(boolean result, String name, Bitmap bitmap) {
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "pineCapturePic";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        try {
            String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "pineCapturePic/" + name + ".jpg";
            FileOutputStream fos = new FileOutputStream(fileName);
            //压缩bitmap到输出流中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
            fos.close();
            if (result) {
                Toast.makeText(mActivity, R.string.base_save_success, Toast.LENGTH_SHORT).show();
            }
            bitmap.recycle();
            String fileRealName = name + ".jpg";
            File file = new File(appDir, fileRealName);
            //7.0新URI
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {// sdk >= 24  android7.0以上
                Uri contentUri = FileProvider.getUriForFile(mActivity,
                        mActivity.getApplicationContext().getPackageName() + ".fileProvider",//与清单文件中android:authorities的值保持一致
                        file);//FileProvider方式或者ContentProvider也可使用VmPolicy方式
                uri = contentUri;

            } else {
                uri = Uri.fromFile(file);
            }
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } catch (Exception e) {

        }
    }

    static class JsInterface {
        private static CommonWebView mWebView;

        public JsInterface(CommonWebView webView) {
            mWebView = webView;
        }

        /**
         * 以后通用模版 用于webview和本地交互
         * 使用者根据自己业务数据返回格式来解析参数（所示参数只是作为一个模板样例）。
         *
         * @param value
         */
        @JavascriptInterface
        public static void onJsCallBack(String value) {
            // Test code begin
            try {
                JSONObject jsonData = new JSONObject(value);
                if (mWebView != null && !TextUtils.isEmpty(jsonData.optString("actionName"))) {
                    String actionName = jsonData.optString("actionName");
                    switch (actionName) {
                        case "shareURL":
                            mWebView.jsShareURL(jsonData);
                            break;
                        case "loginToApp":
                            mWebView.jsLoginToApp(jsonData);
                            break;
                        case "shareImage":
                            mWebView.jsShareImage(jsonData);
                            break;
                        case "captureScreen":   //保存图片到本地相册
                            mWebView.jsCaptureScreen(jsonData);
                            break;
                        case "saveImageByProtoCapture":
                            mWebView.jsSaveImageByProtoCapture(jsonData);
                            break;
                        case "shareImageByProtoCapture":
                            mWebView.jsShareImageByProtoCapture(jsonData);
                            break;
                        case "redirectToApp":
                            mWebView.jsRedirectToApp(jsonData);
                            break;
                        case "goBack":    //退回上一页  如果没有goback 则退出界面
                            mWebView.jsGoBack(jsonData);
                            break;
                        case "goBackToTarget":  //退回指定页面
                            mWebView.jsGoBackToTarget(jsonData);
                            break;
                        case "goBackToExit":  //关闭页面
                            mWebView.jsGoBackToExit(jsonData);
                            break;
                        case "shareImageByHTML5Capture":
                            mWebView.jsShareImageByHTML5Capture(jsonData);
                            break;
                        case "redirectToBrowser":
                            mWebView.jsRedirectToBrowser(jsonData);
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Test code end

        public void clear() {
            mWebView = null;
        }
    }

    // Test code begin
    private void jsShareURL(JSONObject jsonData) {
        ArrayList<ShareBean> shareList = new ArrayList<>();
        // make you share item list
        goShare(shareList);
    }

    private void jsLoginToApp(JSONObject jsonData) {
        ArrayList<ShareBean> shareList = new ArrayList<>();
        // make you share item list
        goShare(shareList);
    }

    private void jsShareImage(JSONObject jsonData) {
        ArrayList<ShareBean> shareList = new ArrayList<>();
        // make you share item list
        goShare(shareList);
    }

    private void jsCaptureScreen(JSONObject jsonData) {
        getSnapshot(true, "CaptureScreen" +
                new SimpleDateFormat("ddHHmmss").format(Calendar.getInstance().getTime()));
    }

    private void jsSaveImageByProtoCapture(JSONObject jsonData) {
        JSONObject params = new JSONObject();
        if (jsonData.has("actionParam")) {
            params = jsonData.optJSONObject("actionParam");
        }
        if (params == null) {
            return;
        }
        int width = params.optInt("width");
        int height = params.optInt("height");
        int x = params.optInt("startX");
        int y = params.optInt("startY");
        x = DensityUtils.dp2px(mActivity, x);
        y = DensityUtils.dp2px(mActivity, y);
        width = DensityUtils.dp2px(mActivity, width);
        height = DensityUtils.dp2px(mActivity, height);
        getSnapshot(true, "CaptureScreen" +
                        new SimpleDateFormat("ddHHmmss").format(Calendar.getInstance().getTime()),
                x, y, width, height);
    }

    private void jsShareImageByProtoCapture(JSONObject jsonData) {
        JSONObject params = new JSONObject();
        if (jsonData.has("actionParam")) {
            params = jsonData.optJSONObject("actionParam");
        }
        if (params == null) {
            return;
        }
        int widthS = params.optInt("width");
        int heightS = params.optInt("height");
        int xS = params.optInt("startX");
        int yS = params.optInt("startY");
        xS = DensityUtils.dp2px(mActivity, xS);
        yS = DensityUtils.dp2px(mActivity, yS);
        widthS = DensityUtils.dp2px(mActivity, widthS);
        heightS = DensityUtils.dp2px(mActivity, heightS);

        String name = "CaptureScreen" +
                new SimpleDateFormat("ddHHmmss").format(Calendar.getInstance().getTime());
        getSnapshot(false, name, xS, yS, widthS, heightS);
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + name;
        ImageUtils.getBitmap(storePath);

        ArrayList<ShareBean> shareList = new ArrayList<>();
        // make you share item list
        goShare(shareList);
    }

    private void jsRedirectToApp(JSONObject jsonData) {
        JSONObject params = new JSONObject();
        if (jsonData.has("actionParam")) {
            params = jsonData.optJSONObject("actionParam");
        }
        if (params == null) {
            return;
        }
        String targetAppUrl = params.optString("targetAppUrl");
        if (!TextUtils.isEmpty(targetAppUrl)) {
            String classPath = UrlUtils.getClassNameFromRedirectUrl(targetAppUrl);
            String[] JumpPathD = targetAppUrl.split("<" + classPath + ">");
            String paramsVV = "";
            ArrayList<String> keyStrAl = new ArrayList<>();
            ArrayList<String> valueStrAl = new ArrayList<>();
            if (JumpPathD.length > 1) {
                paramsVV = JumpPathD[1].toString().replaceAll("\\?", "");
                String[] paramsData = paramsVV.split("&");
                for (int i = 0; i < paramsData.length; i++) {
                    String[] detailD = paramsData[i].split("=");
                    if (detailD.length == 2) {
                        keyStrAl.add(detailD[0]);
                        valueStrAl.add(detailD[1]);
                    }
                }
            }
            Intent intent = new Intent();
            try {
                intent.setClass(mActivity, Class.forName(classPath));
                if (keyStrAl.size() != 0 && keyStrAl.size() == valueStrAl.size()) {
                    for (int j = 0; j < keyStrAl.size(); j++) {
                        intent.putExtra(keyStrAl.get(j), valueStrAl.get(j));
                    }
                }
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
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
        mUrl = params.optString("targetHttpUrl");
        setupUrlHistoryType();
        loadUrl();
    }

    private void jsGoBackToExit(JSONObject jsonData) {
        loadUrl("about:blank");
        finishUi();
    }

    private void jsShareImageByHTML5Capture(JSONObject jsonData) {
        JSONObject params = new JSONObject();
        if (jsonData.has("actionParam")) {
            params = jsonData.optJSONObject("actionParam");
        }
        if (params == null) {
            return;
        }
        String imageData = params.optString("imageData");
        String[] aa = imageData.split(",");
        if (aa.length == 2 && aa[0].startsWith("data:image")) {
            imageData = aa[1];
        }
        Bitmap shareBitmap = ImageUtils.base64ToBitmap(imageData);
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "pbdCapturePic";
        String name = "CaptureScreen" +
                new SimpleDateFormat("ddHHmmss").format(Calendar.getInstance().getTime());
        ImageUtils.save(shareBitmap, storePath, Bitmap.CompressFormat.JPEG);
        final String sharePath = storePath + File.separator + name + ".jpg";
        final File pathFile = new File(sharePath);
        ArrayList<ShareBean> shareList = new ArrayList<>();
        // make you share item list
        goShare(shareList);
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

    private String getGoBackUrlKeyFromPreUrl() {
        return "pineUrlSource";
    }
    // Test code end

    public interface IWebViewListener {
        void onProgressChange(WebView view, int newProgress);

        boolean onJsAlert(WebView view, String url, String message);
    }
}
