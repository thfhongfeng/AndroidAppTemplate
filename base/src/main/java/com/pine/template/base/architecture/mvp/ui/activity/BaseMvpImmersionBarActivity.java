package com.pine.template.base.architecture.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;

import com.pine.template.base.R;
import com.pine.tool.architecture.mvp.contract.IContract;
import com.pine.tool.architecture.mvp.presenter.Presenter;

public abstract class BaseMvpImmersionBarActivity<V extends IContract.Ui, P extends Presenter<V>>
        extends BaseMvpActivity<V, P> implements IContract.Ui {
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
