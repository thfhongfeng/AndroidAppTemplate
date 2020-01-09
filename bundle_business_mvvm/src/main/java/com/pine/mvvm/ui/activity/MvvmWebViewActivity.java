package com.pine.mvvm.ui.activity;

import android.app.AlertDialog;
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

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.pine.base.architecture.mvvm.ui.activity.BaseMvvmActionBarImageMenuActivity;
import com.pine.base.component.share.bean.ShareBean;
import com.pine.base.component.share.manager.ShareManager;
import com.pine.mvvm.R;
import com.pine.mvvm.databinding.MvvmWebViewActivityBinding;
import com.pine.mvvm.vm.MvvmWebViewVm;
import com.pine.tool.util.WebViewUtils;

import java.util.ArrayList;

import cn.pedant.SafeWebViewBridge.InjectedChromeClient;

/**
 * Created by tanghongfeng on 2018/10/9
 */

public class MvvmWebViewActivity extends
        BaseMvvmActionBarImageMenuActivity<MvvmWebViewActivityBinding, MvvmWebViewVm> {
    // 分享dialog
    private AlertDialog mShareDialog;

    @Override
    public void observeInitLiveData(Bundle savedInstanceState) {
        mViewModel.getH5UrlData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                loadUrl();
            }
        });
        mViewModel.getShareBeanListData().observe(this, new Observer<ArrayList<ShareBean>>() {
            @Override
            public void onChanged(@Nullable ArrayList<ShareBean> shareBeans) {
                if (shareBeans != null) {
                    if (mShareDialog != null) {
                        mShareDialog.dismiss();
                    }
                    mShareDialog = ShareManager.getInstance().createShareDialog(
                            MvvmWebViewActivity.this, shareBeans);
                }
            }
        });
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.mvvm_activity_web_view;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mBinding.setPresenter(new Presenter());
        initView();
    }

    private void initView() {
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

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    @Override
    protected void setupActionBar(View actionbar, ImageView goBackIv, TextView titleTv, ImageView menuBtnIv) {
        titleTv.setText(R.string.mvvm_web_view_title);
        menuBtnIv.setImageResource(R.mipmap.res_ic_share);

        menuBtnIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShareDialog != null) {
                    mShareDialog.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ShareManager.getInstance().onActivityResult(requestCode, resultCode, data,
                new ShareManager.ShareCallback() {
                    @Override
                    public void onShareSuccess(ShareBean shareBean) {
                        showShortToast(R.string.mvvm_share_success);
                    }

                    @Override
                    public void onShareCancel(ShareBean shareBean) {
                        showShortToast(R.string.mvvm_share_cancel);
                    }

                    @Override
                    public void onShareFail(ShareBean shareBean) {
                        showShortToast(R.string.mvvm_share_fail);
                    }
                });
    }

    private void loadUrl() {
        mBinding.webView.loadUrl(mViewModel.getH5UrlData().getValue());
    }

    public class Presenter {
        public void onRefreshBtnClick(View view) {
            loadUrl();
        }
    }

    static class JsInterface {

    }
}
