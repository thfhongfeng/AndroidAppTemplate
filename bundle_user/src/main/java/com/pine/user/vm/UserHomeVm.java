package com.pine.user.vm;

import android.arch.lifecycle.MutableLiveData;

import com.pine.base.bean.AccountBean;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.architecture.state.UiState;
import com.pine.user.remote.UserRouterClient;

public class UserHomeVm extends ViewModel {

    @Override
    public void onUiState(UiState state) {
        super.onUiState(state);
        switch (state) {
            case UI_STATE_ON_RESUME:
                AccountBean accountBean = UserRouterClient.getLoginAccount(getContext(), null);
                accountBeanData.setValue(accountBean);
                break;
        }
    }

    MutableLiveData<AccountBean> accountBeanData = new MutableLiveData<>();

    public MutableLiveData<AccountBean> getAccountBeanData() {
        return accountBeanData;
    }
}
