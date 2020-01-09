package com.pine.base.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.CallSuper;

import com.gyf.barlibrary.ImmersionBar;
import com.gyf.barlibrary.OnKeyboardListener;
import com.pine.base.R;
import com.pine.tool.ui.Activity;

public abstract class BaseActionBarCustomMenuActivity extends Activity {
    private ImmersionBar mImmersionBar;

    @Override
    protected final void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.base_activity_actionbar_custom_menu);

        ViewStub base_content_layout = findViewById(R.id.base_content_layout);
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
        mImmersionBar = ImmersionBar.with(this)
                .statusBarDarkFont(true, 1f)
                .statusBarView(R.id.base_status_bar_view)
                .keyboardEnable(true);
        mImmersionBar.init();
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
    }

    /**
     * onCreate中获取当前MenuBar的内容布局资源id
     *
     * @return MenuBar的内容布局资源id
     */
    protected abstract int getMenuBarLayoutResId();

    protected abstract void setupActionBar(View actionbar, ImageView goBackIv, TextView titleTv, View menuContainer);

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
        super.onDestroy();
    }

    public void setKeyboardListener(OnKeyboardListener listener) {
        mImmersionBar.setOnKeyboardListener(listener);
    }

    protected int getLoadingUiResId() {
        return R.layout.base_loading;
    }

    public void setLoadingUiVisibility(boolean visibility) {
        hideSoftInputFromWindow();
        findViewById(R.id.base_loading_layout).setVisibility(visibility ? View.VISIBLE : View.GONE);
    }
}
