package com.pine.template.user.vm;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.pine.template.base.business.bean.AccountBean;
import com.pine.template.user.remote.UserRouterClient;
import com.pine.tool.architecture.mvvm.vm.ViewModel;

public class UserHomeVm extends ViewModel {

    public void refreshUserData(Context context) {
        AccountBean accountBean = UserRouterClient.getLoginAccount(context, null);
        accountBeanData.setValue(accountBean);
    }

    MutableLiveData<AccountBean> accountBeanData = new MutableLiveData<>();

    public MutableLiveData<AccountBean> getAccountBeanData() {
        return accountBeanData;
    }
}
