package com.pine.template.mvc.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pine.template.base.architecture.mvc.activity.BaseMvcActionBarActivity;
import com.pine.template.mvc.R;

/**
 * Created by tanghongfeng on 2019/1/14
 */

public class MvcHomeActivity extends BaseMvcActionBarActivity {

    @Override
    protected boolean beforeInitOnCreate(@Nullable Bundle savedInstanceState) {
        super.beforeInitOnCreate(savedInstanceState);
        setActionBarTag(ACTION_BAR_CENTER_TITLE_TAG | ACTION_BAR_NO_GO_BACK_TAG);
        return false;
    }

    @Override
    protected void setupActionBar(View actionbar, ImageView goBackIv, TextView titleTv) {
        titleTv.setText(R.string.mvc_home_title);
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.mvc_activity_home;
    }

    @Override
    protected void findViewOnCreate(Bundle savedInstanceState) {

    }

    @Override
    protected boolean parseIntentData() {
        return false;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }
}
