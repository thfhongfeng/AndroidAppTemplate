package com.pine.login.vm;

import android.arch.lifecycle.MutableLiveData;
import android.text.TextUtils;

import com.pine.login.LoginConstants;
import com.pine.login.R;
import com.pine.login.bean.AccountBean;
import com.pine.login.bean.RegisterBean;
import com.pine.login.manager.LoginManager;
import com.pine.login.model.LoginAccountModel;
import com.pine.tool.architecture.mvp.model.IModelAsyncResponse;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.exception.BusinessException;
import com.pine.tool.util.RegexUtils;
import com.pine.tool.util.SecurityUtils;

import java.util.HashMap;

public class RegisterVm extends ViewModel {
    private LoginAccountModel mAccountModel = new LoginAccountModel();

    @Override
    public void afterViewInit() {
        registerBeanData.setValue(new RegisterBean());
    }

    public void register() {
        if (isUiLoading()) {
            return;
        }
        RegisterBean registerBean = registerBeanData.getValue();
        if (TextUtils.isEmpty(registerBean.getMobile()) || TextUtils.isEmpty(registerBean.getPassword())) {
            setToastResId(R.string.login_input_empty_msg);
            return;
        }
        if (TextUtils.isEmpty(registerBean.getVerifyCode())) {
            setToastResId(R.string.login_register_verify_code_empty_msg);
            return;
        }
        if (!registerBean.getPassword().equals(registerBean.getConfirmPwd())) {
            setToastResId(R.string.login_register_pwd_need_same);
            return;
        }
        if (!RegexUtils.isMobilePhoneNumber(registerBean.getMobile())) {
            setToastResId(R.string.login_mobile_incorrect_format);
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(LoginConstants.LOGIN_ACCOUNT, registerBean.getMobile());
        String securityPwd = SecurityUtils.generateMD5(registerBean.getPassword());
        params.put(LoginConstants.LOGIN_PASSWORD, securityPwd);
        params.put(LoginConstants.LOGIN_VERIFY_CODE, registerBean.getVerifyCode());
        setUiLoading(true);
        mAccountModel.requestRegister(params, new IModelAsyncResponse<AccountBean>() {
            @Override
            public void onResponse(AccountBean accountBean) {
                setUiLoading(false);
                setToastResId(R.string.login_register_success);
                LoginManager.autoLogin(accountBean.getAccount(), accountBean.getPassword(), null);
                finishUi();
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
                if (e instanceof BusinessException) {
                    if (!TextUtils.isEmpty(e.getMessage())) {
                        setToastMsg(e.getMessage());
                    } else {
                        setToastResId(R.string.login_register_fail);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onCancel() {
                setUiLoading(false);
                setToastResId(R.string.login_register_fail);
            }
        });
    }

    private MutableLiveData<RegisterBean> registerBeanData = new MutableLiveData<>();

    public MutableLiveData<RegisterBean> getRegisterBeanData() {
        return registerBeanData;
    }

    public void setRegisterBean(RegisterBean registerBean) {
        registerBeanData.setValue(registerBean);
    }
}
