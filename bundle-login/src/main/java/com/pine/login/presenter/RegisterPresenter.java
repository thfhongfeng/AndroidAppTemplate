package com.pine.login.presenter;

import com.pine.base.architecture.mvp.presenter.BasePresenter;
import com.pine.login.contract.IRegisterContract;

/**
 * Created by tanghongfeng on 2018/11/15
 */

public class RegisterPresenter extends BasePresenter<IRegisterContract.Ui>
        implements IRegisterContract.Presenter {
    @Override
    public boolean parseIntentData() {
        return false;
    }

    @Override
    public void onUiState(BasePresenter.UiState state) {

    }

    @Override
    public void register() {

    }
}
