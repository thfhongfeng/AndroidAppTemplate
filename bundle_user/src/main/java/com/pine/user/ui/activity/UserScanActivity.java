package com.pine.user.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.pine.base.architecture.mvvm.ui.activity.BaseMvvmFullScreenActivity;
import com.pine.base.component.scan.IScanAnalyzeListener;
import com.pine.base.component.scan.ScanManager;
import com.pine.tool.permission.PermissionsAnnotation;
import com.pine.tool.util.LogUtils;
import com.pine.user.R;
import com.pine.user.databinding.UserScanActivityBinding;
import com.pine.user.vm.UserScanVm;

/**
 * Created by tanghongfeng on 2018/9/13
 */

@PermissionsAnnotation(Permissions = {Manifest.permission.CAMERA})
public class UserScanActivity extends BaseMvvmFullScreenActivity<UserScanActivityBinding, UserScanVm> {

    @Override
    public void observeInitLiveData(Bundle savedInstanceState) {

    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.user_activity_scan;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mBinding.setPresenter(new Presenter());

        ScanManager.attachScanSurface(this, R.id.fragment_content);
        setAnalyzeListener();
    }

    private void setAnalyzeListener() {
        ScanManager.setAnalyzeListener(new IScanAnalyzeListener() {
            @Override
            public void onAnalyzeSuccess(Bitmap bitmap, String result) {
                LogUtils.d(TAG, "scan result:" + result);
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(result);
                intent.setData(content_url);
                startActivity(intent);
            }

            @Override
            public boolean onAnalyzeFailed() {
                LogUtils.d(TAG, "scan analyze failed");
                return false;
            }
        }, R.id.fragment_content);
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    @Override
    protected void onDestroy() {
        ScanManager.detachScanSurface(R.id.fragment_content);
        super.onDestroy();
    }

    public class Presenter {
        public void onGoBackClick(View view) {
            finish();
        }
    }
}
