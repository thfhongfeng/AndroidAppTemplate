package com.pine.login.ui.activity;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pine.base.architecture.mvvm.ui.activity.BaseMvvmActionBarActivity;
import com.pine.login.R;
import com.pine.login.bean.LoginBean;
import com.pine.login.databinding.LoginActivityBinding;
import com.pine.login.vm.LoginVm;

/**
 * Created by tanghongfeng on 2018/9/11.
 */

public class LoginActivity extends BaseMvvmActionBarActivity<LoginActivityBinding, LoginVm> {

    @Override
    public void observeInitLiveData() {
        mViewModel.getLoginBeanData().observe(this, new Observer<LoginBean>() {
            @Override
            public void onChanged(@Nullable LoginBean loginBean) {
                mBinding.setLoginBean(loginBean);
            }
        });
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.login_activity_login;
    }

    @Override
    protected void init() {
        mBinding.setPresenter(new Presenter());
        initView();
    }

    private void initView() {

    }

    @Override
    protected void setupActionBar(ImageView goBackIv, TextView titleTv) {
        titleTv.setText(R.string.login_login_title);
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    public class Presenter {
        public void onLoginClick(View view) {
            mViewModel.login();
        }

        public void onGoRegisterClick(View view) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        }
    }
}
