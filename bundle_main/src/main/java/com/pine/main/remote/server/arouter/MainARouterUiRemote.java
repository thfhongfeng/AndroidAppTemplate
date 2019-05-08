package com.pine.main.remote.server.arouter;

import android.content.Context;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.main.remote.server.MainUiRemoteService;
import com.pine.router.IRouterCallback;
import com.pine.router.impl.arouter.ARouterBundleRemote;
import com.pine.router.impl.arouter.IARouterService;
import com.pine.tool.util.LogUtils;

import java.lang.reflect.Method;

/**
 * Created by tanghongfeng on 2019/2/21
 */

@Route(path = "/main/uiService")
public class MainARouterUiRemote extends ARouterBundleRemote<MainUiRemoteService> implements IARouterService {
    private MainUiRemoteService mRemoteService;
    private Method[] mMethods;

    @Override
    public void init(Context context) {
        mRemoteService = new MainUiRemoteService();
        Class clazz = mRemoteService.getClass();
        mMethods = clazz.getMethods();
    }

    @Override
    public void callCommand(final Context context, final String commandName,
                            final Bundle args, final IRouterCallback callback) {
        LogUtils.d(TAG, "callCommand execute");
        call(mRemoteService, mMethods, context, commandName, args, callback);
    }

    @Override
    public <R> R callCommandDirect(Context context, String commandName, Bundle args) {
        return callDirect(mRemoteService, mMethods, context, commandName, args);
    }
}
