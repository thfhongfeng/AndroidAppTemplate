package com.pine.mvc.remote.arouter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.mvc.remote.MvcUiRemoteService;
import com.pine.router.IRouterCallback;
import com.pine.router.impl.arouter.ARouterBundleRemote;
import com.pine.router.impl.arouter.IARouterService;
import com.pine.tool.util.LogUtils;

import java.lang.reflect.Method;

/**
 * Created by tanghongfeng on 2018/9/12
 */

@Route(path = "/mvc/uiService")
public class MvcARouterUiRemote extends ARouterBundleRemote<MvcUiRemoteService> implements IARouterService {
    private MvcUiRemoteService mRemoteService;
    private Method[] mMethods;

    @Override
    public void init(Context context) {
        mRemoteService = new MvcUiRemoteService();
        mMethods = mRemoteService.getClass().getMethods();
    }

    @Override
    public void callCommand(final Activity activity, final String commandName,
                            final Bundle args, final IRouterCallback callback) {
        LogUtils.d(TAG, "callCommand execute");
        call(mRemoteService, mMethods, activity, commandName, args, callback);
    }
}
