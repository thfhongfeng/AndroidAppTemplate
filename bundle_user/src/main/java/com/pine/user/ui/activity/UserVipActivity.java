package com.pine.user.ui.activity;

import android.widget.ImageView;
import android.widget.TextView;

import com.pine.base.access.UiAccessType;
import com.pine.base.architecture.mvvm.ui.activity.BaseMvvmActionBarActivity;
import com.pine.tool.access.UiAccessAnnotation;
import com.pine.user.R;
import com.pine.user.databinding.UserVipActivityBinding;
import com.pine.user.vm.UserVipVm;

/**
 * Created by tanghongfeng on 2018/9/13
 */

@UiAccessAnnotation(AccessTypes = {UiAccessType.LOGIN, UiAccessType.VIP_LEVEL},
        AccessArgs = {"", ""}, AccessActions = {""})
public class UserVipActivity extends BaseMvvmActionBarActivity<UserVipActivityBinding, UserVipVm> {

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.user_activity_vip;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void setupActionBar(ImageView goBackIv, TextView titleTv) {

    }

    @Override
    public void observeInitLiveData() {

    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }
}
