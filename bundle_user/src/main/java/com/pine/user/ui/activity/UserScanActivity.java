package com.pine.user.ui.activity;

import android.widget.ImageView;
import android.widget.TextView;

import com.pine.base.architecture.mvvm.ui.activity.BaseMvvmActionBarActivity;
import com.pine.user.R;
import com.pine.user.databinding.UserScanActivityBinding;
import com.pine.user.vm.UserScanVm;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class UserScanActivity extends BaseMvvmActionBarActivity<UserScanActivityBinding, UserScanVm> {

    @Override
    public void observeInitLiveData() {

    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.user_activity_scan;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void setupActionBar(ImageView goBackIv, TextView titleTv) {
        titleTv.setText(R.string.user_scan_title);
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }
}
