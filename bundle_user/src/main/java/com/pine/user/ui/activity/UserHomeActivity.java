package com.pine.user.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.pine.base.access.UiAccessType;
import com.pine.base.architecture.mvvm.ui.activity.BaseMvvmNoActionBarActivity;
import com.pine.router.IRouterCallback;
import com.pine.tool.access.UiAccessAnnotation;
import com.pine.user.R;
import com.pine.user.databinding.UserHomeActivityBinding;
import com.pine.user.remote.UserRouterClient;
import com.pine.user.vm.UserHomeVm;

/**
 * Created by tanghongfeng on 2018/9/13
 */

@UiAccessAnnotation(AccessTypes = {UiAccessType.LOGIN}, AccessArgs = {""}, AccessActions = {""})
public class UserHomeActivity extends BaseMvvmNoActionBarActivity<UserHomeActivityBinding, UserHomeVm> {

    @Override
    public void initLiveDataObserver() {

    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.user_activity_home;
    }

    @Override
    protected void init() {
        mBinding.setPresenter(new Presenter());
    }

    @Override
    public void onSyncLiveDataInit(int liveDataObjTag) {

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
    }
}
