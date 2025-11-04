package com.pine.template.welcome;

import android.os.Bundle;

import androidx.databinding.ViewDataBinding;

import com.pine.template.base.architecture.mvvm.ui.activity.BaseMvvmFullScreenActivity;
import com.pine.template.welcome.track.TrackRecordHelper;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.util.LogUtils;

public abstract class WelBaseActivity<T extends ViewDataBinding, VM extends ViewModel>
        extends BaseMvvmFullScreenActivity<T, VM> {

    public abstract String makeUiName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d(TAG, "onResume");
        TrackRecordHelper.getInstance().recordInfoAppEnterUi(makeUiName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.d(TAG, "onPause");
//        TrackInfoHelper.getInstance().recordInfoAppExitUi(makeUiName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "onDestroy");
    }
}
