package com.pine.template.base.architecture.mvvm.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.pine.template.base.R;
import com.pine.tool.architecture.mvvm.vm.ViewModel;

public abstract class BaseMvvmActionBarCustomMenuActivity<T extends ViewDataBinding, VM extends ViewModel>
        extends BaseMvvmActivity<T, VM> {

    @Override
    protected final void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.base_activity_actionbar_custom_menu);

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

        // 初始化沉浸式状态栏
        initImmersionBar();
    }

    private void initImmersionBar() {
        findViewById(R.id.base_status_bar_view).setBackgroundResource(getStatusBarBgResId());
    }

    protected int getStatusBarBgResId() {
        return R.mipmap.base_iv_status_bar_bg;
    }

    @CallSuper
    @Override
    protected void afterInit() {
        View action_bar_ll = findViewById(R.id.action_bar_ll);
        ViewStub base_content_layout = findViewById(R.id.custom_menu_container_vs);
        base_content_layout.setLayoutResource(getMenuBarLayoutResId());
        action_bar_ll.findViewById(R.id.go_back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setupActionBar(action_bar_ll, (ImageView) action_bar_ll.findViewById(R.id.go_back_iv),
                (TextView) action_bar_ll.findViewById(R.id.title), base_content_layout.inflate());
        super.afterInit();
    }

    /**
     * onCreate中获取当前MenuBar的内容布局资源id
     *
     * @return MenuBar的内容布局资源id
     */
    protected int getMenuBarLayoutResId() {
        return R.layout.base_custom_menu_container;
    }


    protected abstract void setupActionBar(View actionbar, ImageView goBackIv, TextView titleTv, View menuContainer);

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
