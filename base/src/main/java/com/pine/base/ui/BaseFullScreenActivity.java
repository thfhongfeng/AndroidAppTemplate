package com.pine.base.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;

import com.pine.base.R;
import com.pine.tool.architecture.mvp.contract.IContract;
import com.pine.tool.architecture.mvp.presenter.Presenter;
import com.pine.tool.architecture.mvp.ui.MvpActivity;

public abstract class BaseFullScreenActivity<V extends IContract.Ui, P extends Presenter<V>>
        extends MvpActivity<V, P> implements IContract.Ui {

    @Override
    protected final void setContentView(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.base_activity_full_screen);

        ViewStub base_content_layout = findViewById(R.id.base_content_layout);
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
    protected void onDestroy() {
        super.onDestroy();
    }

    protected int getLoadingUiResId() {
        return R.layout.base_loading;
    }

    public void setLoadingUiVisibility(boolean visibility) {
        hideSoftInputFromWindow();
        findViewById(R.id.base_loading_layout).setVisibility(visibility ? View.VISIBLE : View.GONE);
    }
}
