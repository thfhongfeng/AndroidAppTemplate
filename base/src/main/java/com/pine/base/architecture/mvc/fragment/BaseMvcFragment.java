package com.pine.base.architecture.mvc.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import com.pine.tool.ui.Fragment;

public abstract class BaseMvcFragment extends Fragment {

    @CallSuper
    @Override
    protected void beforeInitOnCreateView(@Nullable Bundle savedInstanceState) {

    }

    @CallSuper
    @Override
    protected void afterInit() {

    }

    @Override
    public Context getContext() {
        return getActivity();
    }
}
