package com.pine.template.base.browser;

import android.graphics.Bitmap;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

public abstract class WebViewListener {

    public void onUrlStartLoad(String url) {

    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    public boolean onPageStarted(WebView view, String url, Bitmap favicon) {
        return false;
    }

    public void onProgressChange(WebView view, int newProgress) {
    }

    public boolean onPageFinished(WebView view, String url) {
        return false;
    }

    public boolean onReceivedError(WebView view, int errorCode, String description,
                                   String failingUrl) {
        return false;
    }

    public boolean onReceivedError(WebView view, WebResourceRequest request,
                                   WebResourceError error) {
        return false;
    }

    public boolean onReceivedHttpError(WebView view, WebResourceRequest request,
                                       WebResourceResponse errorResponse) {
        return false;
    }

    public boolean onJsAlert(WebView view, String url, String message) {
        return false;
    }
}
