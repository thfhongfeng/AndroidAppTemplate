package com.pine.login.vm;

import android.arch.lifecycle.MutableLiveData;
import android.text.TextUtils;

import com.pine.login.R;
import com.pine.login.bean.LoginBean;
import com.pine.login.manager.LoginManager;
import com.pine.login.model.ILoginResponse;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.util.RegexUtils;

public class LoginVm extends ViewModel {
    @Override
    public void afterViewInit() {
        LoginBean loginBean = new LoginBean();
        // Test code begin
        loginBean.setMobile("15221464292");
        loginBean.setPassword("111aaa");
        // Test code end
        setLoginBean(loginBean);
    }

    public void login() {
        if (isUiLoading()) {
            return;
        }
        LoginBean loginBean = loginBeanData.getValue();
        if (TextUtils.isEmpty(loginBean.getMobile()) || TextUtils.isEmpty(loginBean.getPassword())) {
            setToastResId(R.string.login_input_empty_msg);
            return;
        }
        if (!RegexUtils.isMobilePhoneNumber(loginBean.getMobile())) {
            setToastResId(R.string.login_mobile_incorrect_format);
            return;
        }
        setUiLoading(true);
        LoginManager.login(loginBean.getMobile(), loginBean.getPassword(), new ILoginResponse() {
            @Override
            public boolean onLoginResponse(boolean isSuccess, String msg) {
                setUiLoading(false);
                if (!isSuccess) {
                    if (TextUtils.isEmpty(msg)) {
                        return false;
                    } else {
                        setToastMsg(msg);
                    }
                } else {
                    finishUi();
                }
                return true;
            }

            @Override
            public void onCancel() {
                setUiLoading(false);
            }
        });
    }

    private MutableLiveData<LoginBean> loginBeanData = new MutableLiveData<>();

    public MutableLiveData<LoginBean> getLoginBeanData() {
        return loginBeanData;
    }

    public void setLoginBean(LoginBean loginBean) {
        loginBeanData.setValue(loginBean);
    }
}
