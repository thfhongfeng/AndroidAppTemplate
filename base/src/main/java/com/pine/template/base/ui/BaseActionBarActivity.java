package com.pine.template.base.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.CallSuper;

import com.pine.template.base.R;

public abstract class BaseActionBarActivity extends BaseActivity {
    // 默认ActionBar布局类型
    public static final int ACTION_BAR_TYPE_DEFAULT = 0x0;
    // ActionBar布局Title居中
    public static final int ACTION_BAR_CENTER_TITLE_TAG = 0x0001;
    // ActionBar布局无goBack按键
    public static final int ACTION_BAR_NO_GO_BACK_TAG = 0x0002;
    private int mActionBarTag = ACTION_BAR_TYPE_DEFAULT;

    @Override
    protected final void setContentView(Bundle savedInstanceState) {
        if ((getActionBarTag() & ACTION_BAR_CENTER_TITLE_TAG) == ACTION_BAR_CENTER_TITLE_TAG) {
            setContentView(R.layout.base_activity_actionbar_center_title);
        } else {
            setContentView(R.layout.base_activity_actionbar);
        }

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
    }

    protected int getStatusBarBgResId() {
        return R.mipmap.base_iv_status_bar_bg;
    }

    @CallSuper
    @Override
    protected void afterInit() {
        View action_bar_ll = findViewById(R.id.action_bar_ll);
        if ((getActionBarTag() & ACTION_BAR_NO_GO_BACK_TAG) == ACTION_BAR_NO_GO_BACK_TAG) {
            action_bar_ll.findViewById(R.id.go_back_iv).setVisibility(View.GONE);
        } else {
            action_bar_ll.findViewById(R.id.go_back_iv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        setupActionBar(action_bar_ll, (ImageView) action_bar_ll.findViewById(R.id.go_back_iv),
                (TextView) action_bar_ll.findViewById(R.id.title));
    }

    protected abstract void setupActionBar(View actionbar, ImageView goBackIv, TextView titleTv);

    /**
     * 获取actionbar内容的显示方式，重载该方法改变actionBar内容的显示方式
     */
    protected int getActionBarTag() {
        return mActionBarTag;
    }

    /**
     * 设置actionbar内容的显示方式，需在{@link #beforeInitOnCreate}中设置才有效
     *
     * @param tag
     */
    protected void setActionBarTag(int tag) {
        mActionBarTag = tag;
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
