package com.pine.base.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;

import com.gyf.barlibrary.ImmersionBar;
import com.gyf.barlibrary.OnKeyboardListener;
import com.pine.base.R;
import com.pine.tool.architecture.mvp.contract.IContract;
import com.pine.tool.architecture.mvp.presenter.Presenter;
import com.pine.tool.architecture.mvp.ui.MvpActivity;

public abstract class BaseFullScreenStatusBarActivity<V extends IContract.Ui, P extends Presenter<V>>
        extends MvpActivity<V, P> implements IContract.Ui {
    private ImmersionBar mImmersionBar;
    private View mCustomRootView;

    @Override
    protected final void setContentView(Bundle savedInstanceState) {
        setContentView(R.layout.base_activity_no_actionbar);

        ViewStub base_content_layout = findViewById(R.id.base_content_layout);
        base_content_layout.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                mCustomRootView = inflated;
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
        mImmersionBar = ImmersionBar.with(this)
                .statusBarDarkFont(true, 1f)
                .statusBarView(R.id.base_status_bar_view)
                .keyboardEnable(true);
        mImmersionBar.init();
    }

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
