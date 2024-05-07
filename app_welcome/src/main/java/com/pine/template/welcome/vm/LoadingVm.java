package com.pine.template.welcome.vm;

import androidx.lifecycle.MutableLiveData;

import com.pine.template.base.BaseApplication;
import com.pine.template.base.business.utils.AccountUtils;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.template.welcome.WelUrlConstants;
import com.pine.template.welcome.WelcomeApplication;
import com.pine.tool.architecture.mvvm.vm.ViewModel;

/**
 * Created by tanghongfeng on 2019/10/28.
 */

public class LoadingVm extends ViewModel {

    public void setupConfigSwitcher() {
        ConfigSwitcherServer.setupConfigSwitcher(WelUrlConstants.CONFIG(), BaseApplication.isLogin(),
                AccountUtils.getAccountInfoAndIpParams(WelcomeApplication.mApplication),
                new ConfigSwitcherServer.IConfigSwitcherCallback() {
                    @Override
                    public void onSetupComplete() {
                        setCheckVersion(true);
                    }

                    @Override
                    public boolean onSetupFail() {
                        setCheckVersion(true);
                        return false;
                    }
                });
    }

    private MutableLiveData<Boolean> checkVersionData = new MutableLiveData<>();

    public void setCheckVersion(boolean check) {
        checkVersionData.setValue(check);
    }

    public MutableLiveData<Boolean> getCheckVersionData() {
        return checkVersionData;
    }
}
