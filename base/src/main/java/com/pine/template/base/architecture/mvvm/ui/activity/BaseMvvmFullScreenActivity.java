package com.pine.template.base.architecture.mvvm.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.pine.template.base.R;
import com.pine.tool.architecture.mvvm.vm.ViewModel;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public abstract class BaseMvvmFullScreenActivity<T extends ViewDataBinding, VM extends ViewModel>
        extends BaseMvvmActivity<T, VM> {

    protected void setContentView(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        setContentView(R.layout.base_activity_full_screen);

        ViewStub base_content_layout = findViewById(R.id.base_content_layout);
        base_content_layout.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                mBinding = DataBindingUtil.bind(inflated);
            }
        });
        base_content_layout.setLayoutResource(getActivityLayoutResId());
        base_content_layout.inflate();

        ViewStub base_loading_layout = findViewById(R.id.base_loading_layout);
        base_loading_layout.setLayoutResource(getLoadingUiResId());
        base_loading_layout.inflate();
        findViewById(R.id.base_loading_layout).setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
