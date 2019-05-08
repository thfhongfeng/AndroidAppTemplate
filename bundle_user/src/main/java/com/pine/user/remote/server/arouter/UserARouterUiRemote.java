package com.pine.user.remote.server.arouter;

import android.content.Context;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pine.router.IRouterCallback;
import com.pine.router.impl.arouter.ARouterBundleRemote;
import com.pine.router.impl.arouter.IARouterService;
import com.pine.tool.util.LogUtils;
import com.pine.user.remote.server.UserUiRemoteService;

import java.lang.reflect.Method;

/**
 * Created by tanghongfeng on 2019/2/21
 */

@Route(path = "/user/uiService")
public class UserARouterUiRemote extends ARouterBundleRemote<UserUiRemoteService> implements IARouterService {
    private UserUiRemoteService mRemoteService;
    private Method[] mMethods;

    @Override
    public void init(Context context) {
        mRemoteService = new UserUiRemoteService();
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
