package com.pine.template.user.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.pine.template.base.access.UiAccessType;
import com.pine.template.base.architecture.mvvm.ui.activity.BaseMvvmNoActionBarActivity;
import com.pine.template.base.bean.AccountBean;
import com.pine.tool.access.UiAccessAnnotation;
import com.pine.tool.router.IRouterCallback;
import com.pine.template.user.R;
import com.pine.template.user.databinding.UserHomeActivityBinding;
import com.pine.template.user.remote.UserRouterClient;
import com.pine.template.user.vm.UserHomeVm;

/**
 * Created by tanghongfeng on 2018/9/13
 */

@UiAccessAnnotation(AccessTypes = {UiAccessType.LOGIN}, AccessArgs = {""}, AccessActions = {""})
public class UserHomeActivity extends BaseMvvmNoActionBarActivity<UserHomeActivityBinding, UserHomeVm> {
    private final int REQUEST_CODE_GO_RECHARGE = 1;

    @Override
    public void observeInitLiveData(Bundle savedInstanceState) {
        mViewModel.getAccountBeanData().observe(this, new Observer<AccountBean>() {
            @Override
            public void onChanged(@Nullable AccountBean accountBean) {
                if (accountBean == null) {
                    accountBean = new AccountBean();
                }
                mBinding.setAccountBean(accountBean);
            }
        });
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.user_activity_home;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mBinding.setPresenter(new Presenter());
        mViewModel.refreshUserData(this);
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GO_RECHARGE) {
            if (resultCode == RESULT_OK) {
                mViewModel.refreshUserData(this);
            }
        }
    }

    public class Presenter {
        public void onLogoutClick(View view) {
            UserRouterClient.logout(UserHomeActivity.this, null,
                    new IRouterCallback() {
                        @Override
                        public void onSuccess(Bundle responseBundle) {
                            finish();
                        }

                        @Override
                        public boolean onFail(int failCode, String errorInfo) {
                            return false;
                        }
                    });
        }

        public void onScanClick(View view) {
            Intent intent = new Intent(UserHomeActivity.this, UserScanActivity.class);
            startActivity(intent);
        }

        public void onRechargeClick(View view) {
            Intent intent = new Intent(UserHomeActivity.this, UserRechargeActivity.class);
            startActivityForResult(intent, REQUEST_CODE_GO_RECHARGE);
        }
    }
}
