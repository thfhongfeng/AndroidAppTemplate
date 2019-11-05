package com.pine.user.vm;

import android.arch.lifecycle.MutableLiveData;

import com.pine.base.bean.AccountBean;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.user.remote.UserRouterClient;

public class UserRechargeVm extends ViewModel {

    @Override
    public void afterViewInit() {
        setAccountBean(UserRouterClient.getLoginAccount(getContext(), null));
    }

    private MutableLiveData<AccountBean> accountBeanData = new MutableLiveData<>();

    public MutableLiveData<AccountBean> getAccountBeanData() {
        return accountBeanData;
    }

    public void setAccountBean(AccountBean accountBean) {
        accountBeanData.setValue(accountBean);
    }
}
