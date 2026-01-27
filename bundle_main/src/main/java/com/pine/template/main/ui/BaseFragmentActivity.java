package com.pine.template.main.ui;

import android.os.Bundle;

import androidx.databinding.ViewDataBinding;

import com.pine.tool.architecture.mvvm.vm.ViewModel;

public abstract class BaseFragmentActivity<T extends ViewDataBinding, VM extends ViewModel>
        extends AppBaseActivity<T, VM> {
    public BaseFragment switchHomeFragment() {
        return switchHomeFragment(null);
    }

    public abstract BaseFragment switchHomeFragment(Bundle args);

    public abstract BaseFragment switchFragment(int index, Bundle args);

    public abstract BaseFragment switchFragment(int index, int subIndex, Bundle args);

    public abstract BaseFragment getSubShowFragment();

    public abstract void reloadUi(Bundle args);

    public abstract void setFragmentLoading(boolean visibility);

    public abstract void callActivityEvent(String event, Bundle args);
}
