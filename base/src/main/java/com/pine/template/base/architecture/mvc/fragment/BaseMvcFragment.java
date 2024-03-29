package com.pine.template.base.architecture.mvc.fragment;

import android.content.Context;

import androidx.annotation.CallSuper;

import com.pine.tool.ui.Fragment;

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
