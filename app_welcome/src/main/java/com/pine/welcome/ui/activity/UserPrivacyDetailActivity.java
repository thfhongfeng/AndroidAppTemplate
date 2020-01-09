package com.pine.welcome.ui.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.pine.base.architecture.mvvm.ui.activity.BaseMvvmActionBarActivity;
import com.pine.welcome.R;
import com.pine.welcome.databinding.UserPrivacyDetailActivityBinding;
import com.pine.welcome.vm.UserPrivacyDetailVm;

public class UserPrivacyDetailActivity extends BaseMvvmActionBarActivity<UserPrivacyDetailActivityBinding, UserPrivacyDetailVm> {
    private TextView mTitleTv;

    @Override
    protected int getActionBarTag() {
        return ACTION_BAR_CENTER_TITLE_TAG;
    }

    @Override
    protected void setupActionBar(View actionbar, ImageView goBackIv, TextView titleTv) {
        mTitleTv = titleTv;
    }

    @Override
    public void observeInitLiveData(Bundle savedInstanceState) {
        mViewModel.mPrivacyTypeData.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer privacyType) {
                if (privacyType == 1) {
                    mTitleTv.setText(R.string.wel_user_privacy_user_detail_title);
                    mBinding.contentTv.setText(Html.fromHtml(getResources().getString(R.string.wel_user_privacy_user_detail_content)));
                } else if (privacyType == 2) {
                    mTitleTv.setText(R.string.wel_user_privacy_policy_detail_title);
                    mBinding.contentTv.setText(Html.fromHtml(getResources().getString(R.string.wel_user_privacy_policy_detail_content)));
                }
            }
        });
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.wel_activity_user_privacy_detail;
    }

    @Override
    protected void init(Bundle onCreateSavedInstanceState) {

    }
}
