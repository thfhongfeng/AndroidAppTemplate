package com.pine.template.login.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.pine.template.base.architecture.mvvm.ui.activity.BaseMvvmActionBarActivity;
import com.pine.template.login.LoginUrlConstants;
import com.pine.template.login.R;
import com.pine.template.login.bean.RegisterBean;
import com.pine.template.login.databinding.RegisterActivityBinding;
import com.pine.template.login.vm.RegisterVm;

/**
 * Created by tanghongfeng on 2018/11/15
 */

public class RegisterActivity extends
        BaseMvvmActionBarActivity<RegisterActivityBinding, RegisterVm> {

    @Override
    public void observeInitLiveData(Bundle savedInstanceState) {
        mViewModel.getRegisterBeanData().observe(this, new Observer<RegisterBean>() {
            @Override
            public void onChanged(@Nullable RegisterBean registerBean) {
                mBinding.setRegisterBean(registerBean);
            }
        });
    }

    @Override
    protected void setupActionBar(View actionbar, ImageView goBackIv, TextView titleTv) {
        titleTv.setText(R.string.login_register_title);
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.login_activity_register;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mBinding.setPresenter(new Presenter());
        initView();
    }

    private void initView() {
        mBinding.verifyCodeIv.init(LoginUrlConstants.VERIFY_CODE());
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    @Override
    public void onResume() {
        mBinding.verifyCodeIv.onResume();
        super.onResume();
    }

    public class Presenter {
        public void onRegisterClick(View view) {
            mViewModel.register(RegisterActivity.this);
        }
    }
}
