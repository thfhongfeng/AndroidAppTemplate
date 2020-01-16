package com.pine.login.vm;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.pine.base.bean.AccountBean;
import com.pine.login.LoginConstants;
import com.pine.login.R;
import com.pine.login.bean.RegisterBean;
import com.pine.login.manager.LoginManager;
import com.pine.login.model.ILoginResponse;
import com.pine.login.model.LoginAccountModel;
import com.pine.login.ui.activity.LoginActivity;
import com.pine.tool.architecture.mvp.model.IModelAsyncResponse;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.exception.MessageException;
import com.pine.tool.util.RegexUtils;
import com.pine.tool.util.SecurityUtils;

import java.util.HashMap;

public class RegisterVm extends ViewModel {
    private LoginAccountModel mAccountModel = new LoginAccountModel();

    @Override
    public void afterViewInit(Context activity) {
        registerBeanData.setValue(new RegisterBean());
    }

    public void register(final Context context) {
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
                LoginManager.autoLogin(accountBean.getAccount(), accountBean.getPassword(), new ILoginResponse() {
                    @Override
                    public boolean onLoginResponse(boolean isSuccess, String msg) {
                        setToastResId(R.string.login_register_success);
                        if (!isSuccess) {
                            goLoginActivity(context);
                        }
                        finishUi();
                        return false;
                    }

                    @Override
                    public void onCancel() {
                        setToastResId(R.string.login_register_success);
                        goLoginActivity(context);
                        finishUi();
                    }
                });
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
                if (e instanceof MessageException) {
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

    private void goLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private MutableLiveData<RegisterBean> registerBeanData = new MutableLiveData<>();

    public MutableLiveData<RegisterBean> getRegisterBeanData() {
        return registerBeanData;
    }

    public void setRegisterBean(RegisterBean registerBean) {
        registerBeanData.setValue(registerBean);
    }
}
