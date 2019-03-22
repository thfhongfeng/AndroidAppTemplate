package com.pine.login.presenter;

import com.pine.base.architecture.mvp.model.IModelAsyncResponse;
import com.pine.base.architecture.mvp.presenter.BasePresenter;
import com.pine.base.bean.BaseInputParam;
import com.pine.login.LoginConstants;
import com.pine.login.R;
import com.pine.login.bean.AccountBean;
import com.pine.login.contract.IRegisterContract;
import com.pine.login.model.ILoginAccountModel;
import com.pine.login.model.LoginModelFactory;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/11/15
 */

public class RegisterPresenter extends BasePresenter<IRegisterContract.Ui>
        implements IRegisterContract.Presenter {
    private ILoginAccountModel mAccountModel = LoginModelFactory.getLoginAccountModel();

    @Override
    public void register() {
        if (mIsLoadProcessing) {
            return;
        }
        BaseInputParam<String> accountBean = getUi().getUserMobileParam(LoginConstants.LOGIN_ACCOUNT);
        BaseInputParam<String> pwdBean = getUi().getUserPasswordParam(LoginConstants.LOGIN_PASSWORD);
        if (accountBean.checkIsEmpty(R.string.login_input_empty_msg) ||
                pwdBean.checkIsEmpty(R.string.login_input_empty_msg) ||
                !accountBean.checkIsPhone(R.string.login_mobile_incorrect_format)) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(accountBean.getKey(), accountBean.getValue());
        params.put(pwdBean.getKey(), pwdBean.getValue());
        setUiLoading(true);
        mAccountModel.requestRegister(params, new IModelAsyncResponse<AccountBean>() {
            @Override
            public void onResponse(AccountBean accountBean) {
                setUiLoading(false);
                showShortToast(R.string.login_register_success);
                finishUi();
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
                showShortToast(R.string.login_register_fail);
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
