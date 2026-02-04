package com.pine.tool.util;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewRenderProcess;
import android.webkit.WebViewRenderProcessClient;

import androidx.annotation.NonNull;

/**
 * Created by tanghongfeng on 2018/10/10
 */

public class WebViewUtils {
    private WebViewUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void setupCommonWebView(WebView webView, DownloadListener downloadListener,
                                          WebChromeClient webChromeClient, WebViewClient webViewClient) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAllowContentAccess(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        if (downloadListener != null) {
            webView.setDownloadListener(downloadListener);
        }
        // [中危]WebView同源策略绕过
        // 风险详情：APP 的 WebView 加载本地资源文件并启用 JavaScript 时，存在信息泄漏风险。
        // 修复建议：避免同时使用 File 协议与 JavaScript。
        webSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // [高危]WebView应用克隆风险
            // 风险详情：APP使用WebView访问网络，当开启了允许JS脚本访问本地文件，一旦访问恶意网址，存在被窃取APP数据并复制APP的运行环境，造成“应用克隆”的后果，可能造成严重的经济损失。
            // 修复建议：建议禁用setAllowFileAccessFromFileURLs和setAllowUniversalAccessFromFileURLs；若需要允许JS访问本地文件，则应使用白名单等策略进行严格的访问控制。
            webSettings.setAllowFileAccessFromFileURLs(true);
        }
        if (webChromeClient != null) {
            // 必须设置为true，否则杀死APP后，再次加载时，onGeolocationPermissionsShowPrompt不会调用(除非清除APP缓存)。从而授权无法完成，导致页面加载出问题。
            webSettings.setDomStorageEnabled(true);
            webView.setWebChromeClient(webChromeClient);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (webViewClient != null) {
            webView.setWebViewClient(webViewClient);
        }
        webView.removeJavascriptInterface("searchBoxJavaBredge_");
    }

    public static void enableRenderProcessClient(@NonNull WebView webView, @NonNull Context context) {
        // Android 10+ 开启WebView渲染进程隔离（准确版本判断），
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            webView.setWebViewRenderProcessClient(context.getMainExecutor(), new WebViewRenderProcessClient() {
                // 渲染进程无响应（如摄像头锁阻塞）时，直接终止进程释放资源，避免主进程卡死（兜底）。
                @Override
                public void onRenderProcessUnresponsive(@NonNull WebView view, @NonNull WebViewRenderProcess renderer) {
                    renderer.terminate();
                }

                // 渲染进程恢复响应，可选回调（无需处理可省略）
                @Override
                public void onRenderProcessResponsive(@NonNull WebView view, @NonNull WebViewRenderProcess renderer) {

                }
            });
        }
    }

    public static void setForDesktopUserAgent(@NonNull WebView webView) {
        WebSettings webSettings = webView.getSettings();
        // 1. 获取桌面版 User-Agent（示例为 Chrome 的 PC 端标识）
        String desktopUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
        // 2. 设置 User-Agent
        webSettings.setUserAgentString(desktopUserAgent);
        // 清除缓存（可选）
        webView.clearCache(true);
        webView.clearHistory();
    }

    public static void setUserAgent(@NonNull WebView webView, @NonNull String agent) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setUserAgentString(agent);
    }

    public static String getUserAgent(@NonNull WebView webView) {
        WebSettings webSettings = webView.getSettings();
        return webSettings.getUserAgentString();
    }
}