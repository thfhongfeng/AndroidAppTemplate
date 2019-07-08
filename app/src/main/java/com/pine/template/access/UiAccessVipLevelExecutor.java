package com.pine.template.access;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.pine.tool.access.IUiAccessCallback;
import com.pine.tool.access.IUiAccessExecutor;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public class UiAccessVipLevelExecutor implements IUiAccessExecutor {
    private int mForbiddenToastResId = -1;
    private IUiAccessCallback mCallback;

    public UiAccessVipLevelExecutor(int forbiddenToastResId, IUiAccessCallback callback) {
        mForbiddenToastResId = forbiddenToastResId;
        mCallback = callback;
    }

    @Override
    public boolean onExecute(Activity activity, String args, boolean isResumeUi) {
        return true;
    }

    @Override
    public boolean onExecute(Fragment fragment, String args, boolean isResumeUi) {
        return true;
    }
}
