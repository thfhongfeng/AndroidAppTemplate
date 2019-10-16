package com.pine.login.ui.activity;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pine.base.architecture.mvvm.ui.activity.BaseMvvmActionBarActivity;
import com.pine.login.LoginUrlConstants;
import com.pine.login.R;
import com.pine.login.bean.RegisterBean;
import com.pine.login.databinding.RegisterActivityBinding;
import com.pine.login.vm.RegisterVm;

/**
 * Created by tanghongfeng on 2018/11/15
 */

public class RegisterActivity extends
        BaseMvvmActionBarActivity<RegisterActivityBinding, RegisterVm> {

    @Override
    public void initLiveDataObserver() {
        mViewModel.getRegisterBeanData().observe(this, new Observer<RegisterBean>() {
            @Override
            public void onChanged(@Nullable RegisterBean registerBean) {
                mBinding.setRegisterBean(registerBean);
            }
        });
    }

    @Override
    protected void setupActionBar(ImageView goBackIv, TextView titleTv) {
        titleTv.setText(R.string.login_register_title);
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.login_activity_register;
    }

    @Override
    protected void init() {
        mBinding.setPresenter(new Presenter());
        initView();
    }

    private void initView() {
        mBinding.verifyCodeIv.init(LoginUrlConstants.Verify_Code_Image);
    }

    @Override
    public void onSyncLiveDataInit(int liveDataObjTag) {

    }

    @Override
    public void onResume() {
        mBinding.verifyCodeIv.onResume();
        super.onResume();
    }

    public class Presenter {
        public void onRegisterClick(View view) {
            mViewModel.register();
        }
    }
}
