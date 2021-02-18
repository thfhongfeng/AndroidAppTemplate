package com.pine.template.welcome.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.pine.template.base.architecture.mvvm.ui.activity.BaseMvvmActionBarActivity;
import com.pine.tool.util.WebViewUtils;
import com.pine.template.welcome.R;
import com.pine.template.welcome.databinding.UserPrivacyH5DetailActivityBinding;
import com.pine.template.welcome.vm.UserPrivacyDetailVm;

import cn.pedant.SafeWebViewBridge.InjectedChromeClient;

public class UserPrivacyH5DetailActivity extends BaseMvvmActionBarActivity<UserPrivacyH5DetailActivityBinding, UserPrivacyDetailVm> {
    private TextView mTitleTv;

    @Override
    protected int getActionBarTag() {
        return ACTION_BAR_CENTER_TITLE_TAG;
    }

    @Override
    protected void setupActionBar(View actionbar, ImageView goBackIv, TextView titleTv) {
        mTitleTv = titleTv;
    }

    @Override
    public void observeInitLiveData(Bundle savedInstanceState) {
        mViewModel.mPrivacyTypeData.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer privacyType) {
                if (privacyType == 1) {
                    mTitleTv.setText(R.string.wel_user_privacy_user_detail_title);
                } else if (privacyType == 2) {
                    mTitleTv.setText(R.string.wel_user_privacy_policy_detail_title);
                }
                loadUrl();
            }
        });
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.wel_activity_user_privacy_h5_detail;
    }

    @Override
    protected void init(Bundle onCreateSavedInstanceState) {
        initWebView();
    }

    private void initWebView() {
        WebViewUtils.setupCommonWebView(mBinding.webView, new DownloadListener() {
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
        }, new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                loadUrl();
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
    }

    private void loadUrl() {
        mBinding.webView.loadUrl(mViewModel.mH5Url);
    }

    static class JsInterface {

    }
}
