package com.pine.login.presenter;

import android.text.TextUtils;

import com.pine.login.LoginConstants;
import com.pine.login.R;
import com.pine.login.bean.AccountBean;
import com.pine.login.contract.IRegisterContract;
import com.pine.login.manager.LoginManager;
import com.pine.login.model.LoginAccountModel;
import com.pine.tool.architecture.mvp.model.IModelAsyncResponse;
import com.pine.tool.architecture.mvp.presenter.Presenter;
import com.pine.tool.bean.InputParam;
import com.pine.tool.exception.BusinessException;
import com.pine.tool.util.SecurityUtils;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/11/15
 */

public class RegisterPresenter extends Presenter<IRegisterContract.Ui>
        implements IRegisterContract.Presenter {
    private LoginAccountModel mAccountModel = new LoginAccountModel();

    @Override
    public void register() {
        if (mIsLoadProcessing) {
            return;
        }
        InputParam<String> accountBean = getUi().getUserMobileParam(LoginConstants.LOGIN_ACCOUNT);
        InputParam<String> pwdBean = getUi().getUserPasswordParam(LoginConstants.LOGIN_PASSWORD);
        if (accountBean.checkIsEmpty(R.string.login_input_empty_msg) ||
                pwdBean.checkIsEmpty(R.string.login_input_empty_msg) ||
                !accountBean.checkIsPhone(R.string.login_mobile_incorrect_format)) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(accountBean.getKey(), accountBean.getValue());
        String securityPwd = SecurityUtils.generateMD5(pwdBean.getValue());
        params.put(pwdBean.getKey(), securityPwd);
        setUiLoading(true);
        mAccountModel.requestRegister(params, new IModelAsyncResponse<AccountBean>() {
            @Override
            public void onResponse(AccountBean accountBean) {
                setUiLoading(false);
                showShortToast(R.string.login_register_success);
                LoginManager.autoLogin(accountBean.getAccount(), accountBean.getPassword(), null);
                finishUi();
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
                if (e instanceof BusinessException) {
                    if (!TextUtils.isEmpty(e.getMessage())) {
                        showShortToast(e.getMessage());
                    } else {
                        showShortToast(R.string.login_register_fail);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onCancel() {
                setUiLoading(false);
                showShortToast(R.string.login_register_fail);
            }
        });
    }
}
