package com.pine.template.access;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.pine.base.BaseApplication;
import com.pine.router.IRouterCallback;
import com.pine.template.TemplateApplication;
import com.pine.template.remote.TemplateClientManager;
import com.pine.tool.access.IUiAccessCallback;
import com.pine.tool.access.IUiAccessExecutor;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public class UiAccessLoginExecutor implements IUiAccessExecutor {
    private int mForbiddenToastResId = -1;
    private IUiAccessCallback mCallback;

    public UiAccessLoginExecutor(int forbiddenToastResId, IUiAccessCallback callback) {
        mForbiddenToastResId = forbiddenToastResId;
        mCallback = callback;
    }

    @Override
    public boolean onExecute(final Activity activity, String args, boolean isResumeUi) {
        boolean canAccess = BaseApplication.isLogin();
        if (!canAccess) {
            if (mCallback != null) {
                mCallback.onAccessForbidden(activity, args, isResumeUi);
            }
            if (isResumeUi) {
                if (activity != null && !activity.isFinishing()) {
                    activity.finish();
                }
            } else {
                TemplateClientManager.goLoginActivity(TemplateApplication.getContext(), null, new IRouterCallback() {
                    @Override
                    public void onSuccess(Bundle responseBundle) {
                        if (activity != null && !activity.isFinishing()) {
                            activity.finish();
                        }
                    }

                    @Override
                    public boolean onFail(int failCode, String errorInfo) {
                        if (activity != null && !activity.isFinishing()) {
                            activity.finish();
                        }
                        return true;
                    }
                });
            }
        }
        return canAccess;
    }

    @Override
    public boolean onExecute(Fragment fragment, String args, boolean isResumeUi) {
        return true;
    }
}
