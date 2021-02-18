package com.pine.template.user.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.pine.template.base.access.UiAccessType;
import com.pine.template.base.architecture.mvvm.ui.activity.BaseMvvmActionBarActivity;
import com.pine.template.base.bean.AccountBean;
import com.pine.tool.access.UiAccessAnnotation;
import com.pine.template.user.R;
import com.pine.template.user.databinding.UserRechargeActivityBinding;
import com.pine.template.user.vm.UserRechargeVm;

/**
 * Created by tanghongfeng on 2018/9/13
 */

@UiAccessAnnotation(AccessTypes = {UiAccessType.LOGIN}, AccessArgs = {""}, AccessActions = {""})
public class UserRechargeActivity extends BaseMvvmActionBarActivity<UserRechargeActivityBinding, UserRechargeVm> {

    @Override
    public void observeInitLiveData(Bundle savedInstanceState) {
        mViewModel.getAccountBeanData().observe(this, new Observer<AccountBean>() {
            @Override
            public void onChanged(@Nullable AccountBean accountBean) {

            }
        });
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.user_activity_recharge;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @Override
    protected void setupActionBar(View actionbar, ImageView goBackIv, TextView titleTv) {
        titleTv.setText(R.string.user_recharge_title);
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }
}
