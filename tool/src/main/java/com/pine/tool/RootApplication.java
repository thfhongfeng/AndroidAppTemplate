package com.pine.tool;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2019/11/1.
 */

public class RootApplication {
    private final static String TAG = LogUtils.makeLogTag(RootApplication.class);
    private static volatile boolean mIsInit;
    public static volatile Activity mCurResumedActivity;
    public static Application mApplication;
    private static volatile boolean mIsLogin;

    public synchronized static boolean isLogin() {
        return mIsLogin;
    }

    public synchronized static void setLogin(boolean isLogin) {
        mIsLogin = isLogin;
    }

    public static void init(Application application) {
        if (mIsInit) {
            return;
        }
        mApplication = application;
        registerActivity();
        mIsInit = true;
    }

    private synchronized static void registerActivity() {
        mApplication.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                LogUtils.d(TAG, activity + " on created");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                LogUtils.d(TAG, activity + " on started");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                LogUtils.d(TAG, activity + " on resumed");
                mCurResumedActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                LogUtils.d(TAG, activity + " on paused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                LogUtils.d(TAG, activity + " on stopped");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                LogUtils.d(TAG, activity + " on saveInstanceState");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                LogUtils.d(TAG, activity + " on destroyed");
            }
        });
    }
}
