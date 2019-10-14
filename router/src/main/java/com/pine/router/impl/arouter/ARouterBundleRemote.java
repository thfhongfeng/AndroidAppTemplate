package com.pine.router.impl.arouter;

import android.content.Context;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.pine.router.IRouterCallback;
import com.pine.router.IRouterManager;
import com.pine.router.IServiceCallback;
import com.pine.router.annotation.RouterCommand;
import com.pine.tool.util.LogUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by tanghongfeng on 2019/2/21
 */

public abstract class ARouterBundleRemote<T> implements IProvider {
    protected final String TAG = LogUtils.makeLogTag(this.getClass());

    private T mRemoteService;
    private Method[] mMethods;

    @Override
    public void init(Context context) {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Class<T> clazz = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
            try {
                mRemoteService = clazz.newInstance();
                mMethods = clazz.getMethods();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public void call(Context context, String commandName,
                     Bundle args, final IRouterCallback callback) {
        if (mRemoteService == null) {
            throw new IllegalStateException(getClass() + " was not init.");
        }
        for (int i = 0; i < mMethods.length; i++) {
            RouterCommand annotation = mMethods[i].getAnnotation(RouterCommand.class);
            if (annotation != null && annotation.CommandName().equals(commandName)) {
                mMethods[i].setAccessible(true);
                try {
                    mMethods[i].invoke(mRemoteService, context, args, new IServiceCallback() {
                        @Override
                        public void onResponse(Bundle bundle) {
                            if (bundle == null) {
                                bundle = new Bundle();
                            }
                            if (callback != null) {
                                callback.onSuccess(bundle);
                            }
                        }
                    });
                    return;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFail(IRouterManager.FAIL_CODE_ERROR, e.toString());
                    }
                    return;
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFail(IRouterManager.FAIL_CODE_ERROR, e.toString());
                    }
                    return;
                }
            }
        }
        if (callback != null) {
            callback.onFail(IRouterManager.FAIL_CODE_INVALID, "");
        }
    }

    public <R> R callDirect(Context context, String commandName,
                            Bundle args) {
        if (mRemoteService == null) {
            throw new IllegalStateException(getClass() + " was not init.");
        }
        for (int i = 0; i < mMethods.length; i++) {
            RouterCommand annotation = mMethods[i].getAnnotation(RouterCommand.class);
            if (annotation != null && annotation.CommandName().equals(commandName)) {
                mMethods[i].setAccessible(true);
                try {
                    return (R) mMethods[i].invoke(mRemoteService, context, args);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }
}
