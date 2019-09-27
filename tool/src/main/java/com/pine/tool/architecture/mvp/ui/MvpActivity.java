package com.pine.tool.architecture.mvp.ui;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import com.pine.tool.architecture.mvp.contract.IContract;
import com.pine.tool.architecture.mvp.presenter.Presenter;
import com.pine.tool.architecture.state.UiState;
import com.pine.tool.ui.Activity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by tanghongfeng on 2018/9/12
 */

public abstract class MvpActivity<V extends IContract.Ui, P extends Presenter<V>>
        extends Activity implements IContract.Ui {
    protected P mPresenter;

    @CallSuper
    @Override
    protected void beforeInitOnCreate(@Nullable Bundle savedInstanceState) {
        // 创建并绑定presenter
        mPresenter = createPresenter();
        if (mPresenter == null) {
            Class presenterClazz;
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                presenterClazz = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
                try {
                    mPresenter = (P) presenterClazz.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if (mPresenter != null) {
            mPresenter.attachUi((V) this);
        } else {
            throw new RuntimeException("must initialize a presenter!");
        }
    }

    protected P createPresenter() {
        return null;
    }

    @Override
    protected final boolean parseIntentData() {
        if (mPresenter != null) {
            return mPresenter.parseIntentData(getIntent().getExtras() == null ?
                    new Bundle() : getIntent().getExtras());
        }
        return false;
    }

    @CallSuper
    @Override
    protected void afterInit() {
        if (mPresenter != null) {
            mPresenter.onUiState(UiState.UI_STATE_ON_INIT);
            mPresenter.afterViewInit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onUiState(UiState.UI_STATE_ON_RESUME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPresenter != null) {
            mPresenter.onUiState(UiState.UI_STATE_ON_PAUSE);
        }
    }

    @Override
    protected void onStop() {
        if (mPresenter != null) {
            mPresenter.onUiState(UiState.UI_STATE_ON_STOP);
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除绑定
        if (mPresenter != null) {
            mPresenter.detachUi();
        }
    }

    @Override
    public android.app.Activity getContextActivity() {
        return this;
    }
}
