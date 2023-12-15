package com.pine.template.base.architecture.mvvm.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.pine.template.base.R;
import com.pine.tool.architecture.mvvm.vm.ViewModel;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public abstract class BaseMvvmImmersionBarActivity<T extends ViewDataBinding, VM extends ViewModel>
        extends BaseMvvmActivity<T, VM> {
    private View mCustomRootView;

    protected void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.base_activity_full_screen_status_bar);

        ViewStub base_content_layout = findViewById(R.id.base_content_layout);
        base_content_layout.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                mCustomRootView = inflated;
                mBinding = DataBindingUtil.bind(inflated);
            }
        });
        base_content_layout.setLayoutResource(getActivityLayoutResId());
        base_content_layout.inflate();

        ViewStub base_loading_layout = findViewById(R.id.base_loading_layout);
        base_loading_layout.setLayoutResource(getLoadingUiResId());
        base_loading_layout.inflate();
        findViewById(R.id.base_loading_layout).setVisibility(View.GONE);

        // 初始化沉浸式状态栏
        initImmersionBar();
    }

    private void initImmersionBar() {
        findViewById(R.id.base_container).setBackground(mCustomRootView.getBackground());
        mCustomRootView.setBackground(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}