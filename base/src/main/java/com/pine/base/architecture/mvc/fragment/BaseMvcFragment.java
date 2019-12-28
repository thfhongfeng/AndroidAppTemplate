package com.pine.base.architecture.mvc.fragment;

import android.content.Context;

import com.pine.tool.ui.Fragment;

import androidx.annotation.CallSuper;

public abstract class BaseMvcFragment extends Fragment {

    @CallSuper
    @Override
    protected void afterInit() {

    }

    @Override
    public Context getContext() {
        return getActivity();
    }
}
