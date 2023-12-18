package com.pine.template.user.vm;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.pine.template.base.business.bean.AccountBean;
import com.pine.template.user.remote.UserRouterClient;
import com.pine.tool.architecture.mvvm.vm.ViewModel;

public class UserRechargeVm extends ViewModel {

    @Override
    public void afterViewInit(Context activity) {
        setAccountBean(UserRouterClient.getLoginAccount(activity, null));
    }

    private MutableLiveData<AccountBean> accountBeanData = new MutableLiveData<>();

    public MutableLiveData<AccountBean> getAccountBeanData() {
        return accountBeanData;
    }

    public void setAccountBean(AccountBean accountBean) {
        accountBeanData.setValue(accountBean);
    }
}
