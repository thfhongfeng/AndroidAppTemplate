package com.pine.tool.architecture.mvp.ui;

import android.app.Activity;
import android.os.Bundle;

import com.pine.tool.architecture.mvp.contract.IContract;
import com.pine.tool.architecture.mvp.presenter.Presenter;
import com.pine.tool.architecture.state.UiState;
import com.pine.tool.ui.Fragment;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

/**
 * Created by tanghongfeng on 2018/9/12
 */

public abstract class MvpFragment<V extends IContract.Ui, P extends Presenter<V>>
        extends Fragment implements IContract.Ui {
    protected P mPresenter;

    @CallSuper
    @Override
    protected void beforeInitOnCreateView(@Nullable Bundle savedInstanceState) {
        // 创建并绑定presenter
        mPresenter = createPresenter();
        if (mPresenter == null) {
            Class presenterClazz;
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                presenterClazz = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
                try {
                    mPresenter = (P) presenterClazz.newInstance();
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if (mPresenter != null) {
            mPresenter.attachUi((V) this);
        }
    }

    protected P createPresenter() {
        return null;
    }

    @CallSuper
    @Override
    protected final boolean parseArguments() {
        if (mPresenter != null) {
            return mPresenter.parseIntentData(getArguments() == null ? new Bundle() : getArguments());
        }
        return false;
    }

    @CallSuper
    @Override
    protected void afterInit() {
        if (mPresenter != null) {
            mPresenter.onUiState(UiState.UI_STATE_ON_INIT);
        }
    }

    @CallSuper
    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.onUiState(UiState.UI_STATE_ON_RESUME);
        }
    }

    @CallSuper
    @Override
    public void onPause() {
        super.onPause();
        if (mPresenter != null) {
            mPresenter.onUiState(UiState.UI_STATE_ON_PAUSE);
        }
    }

    @CallSuper
    @Override
    public void onStop() {
        if (mPresenter != null) {
            mPresenter.onUiState(UiState.UI_STATE_ON_STOP);
        }
        super.onStop();
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        //解除绑定
        if (mPresenter != null) {
            mPresenter.detachUi();
        }
        super.onDestroyView();
    }

    @Override
    public Activity getContextActivity() {
        return getActivity();
    }

    public void setLoadingUiVisibility(boolean visibility) {
        hideSoftInputFromWindow();
    }
}
