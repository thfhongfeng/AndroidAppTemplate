package com.pine.router.impl.arouter;

import android.content.Context;
import android.os.Bundle;

import com.pine.router.IRouterCallback;
import com.pine.router.IServiceCallback;
import com.pine.router.RouterConstants;
import com.pine.router.annotation.RouterAnnotation;
import com.pine.router.impl.IRouterManager;
import com.pine.tool.util.LogUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by tanghongfeng on 2019/2/21
 */

public abstract class ARouterBundleRemote<T> {
    protected final String TAG = LogUtils.makeLogTag(this.getClass());

    public void call(T t, Method[] methods, Context context, String commandName,
                     Bundle args, final IRouterCallback callback) {
        for (int i = 0; i < methods.length; i++) {
            RouterAnnotation annotation = methods[i].getAnnotation(RouterAnnotation.class);
            if (annotation != null && annotation.CommandName().equals(commandName)) {
                methods[i].setAccessible(true);
                final Bundle responseBundle = new Bundle();
                try {
                    responseBundle.putString(RouterConstants.REMOTE_CALL_STATE_KEY, RouterConstants.ON_SUCCEED);
                    methods[i].invoke(t, context, args, new IServiceCallback() {
                        @Override
                        public void onResponse(Bundle bundle) {
                            if (bundle == null) {
                                bundle = new Bundle();
                            }
                            responseBundle.putBundle(RouterConstants.REMOTE_CALL_RESULT_KEY, bundle);
                            if (callback != null) {
                                callback.onSuccess(responseBundle);
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

    public <R> R callDirect(T t, Method[] methods, Context context, String commandName,
                            Bundle args) {
        for (int i = 0; i < methods.length; i++) {
            RouterAnnotation annotation = methods[i].getAnnotation(RouterAnnotation.class);
            if (annotation != null && annotation.CommandName().equals(commandName)) {
                methods[i].setAccessible(true);
                final Bundle responseBundle = new Bundle();
                try {
                    responseBundle.putString(RouterConstants.REMOTE_CALL_STATE_KEY, RouterConstants.ON_SUCCEED);
                    return (R) methods[i].invoke(t, context, args);
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
