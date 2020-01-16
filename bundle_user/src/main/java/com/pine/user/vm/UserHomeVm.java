package com.pine.user.vm;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.pine.base.bean.AccountBean;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.user.remote.UserRouterClient;

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
