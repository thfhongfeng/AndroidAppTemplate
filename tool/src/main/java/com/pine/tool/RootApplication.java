package com.pine.tool;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.pine.tool.util.LogUtils;

import java.util.HashSet;
import java.util.Iterator;

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
                LogUtils.d("ActivityLifecycle", activity + " on created");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                LogUtils.d("ActivityLifecycle", activity + " on started");
                if (mAppIsForegroundHelperCount == 0) {
                    setAppIsForeground(true);
                }
                mAppIsForegroundHelperCount++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                LogUtils.d("ActivityLifecycle", activity + " on resumed");
                mCurResumedActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                LogUtils.d("ActivityLifecycle", activity + " on paused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                LogUtils.d("ActivityLifecycle", activity + " on stopped");
                mAppIsForegroundHelperCount--;
                if (mAppIsForegroundHelperCount == 0) {
                    setAppIsForeground(false);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                LogUtils.d("ActivityLifecycle", activity + " on saveInstanceState");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                LogUtils.d("ActivityLifecycle", activity + " on destroyed");
            }
        });
    }

    private static volatile int mAppIsForegroundHelperCount;
    private static volatile boolean mAppIsForeground = false;
    public static HashSet<IOnAppLifecycleListener> mOnAppLifecycleListenerSet = new HashSet<>();

    private synchronized static void setAppIsForeground(boolean isForeground) {
        mAppIsForeground = isForeground;
        if (mOnAppLifecycleListenerSet.size() > 0) {
            Iterator<IOnAppLifecycleListener> iterator = mOnAppLifecycleListenerSet.iterator();
            while (iterator.hasNext()) {
                IOnAppLifecycleListener listener = iterator.next();
                if (listener != null) {
                    listener.onAppForegroundChange(isForeground);
                }
            }
        }
    }

    public synchronized static void addAppLifecycleListener(IOnAppLifecycleListener listener) {
        mOnAppLifecycleListenerSet.add(listener);
    }

    public synchronized static void removeAppLifecycleListener(IOnAppLifecycleListener listener) {
        mOnAppLifecycleListenerSet.remove(listener);
    }

    public interface IOnAppLifecycleListener {
        void onAppForegroundChange(boolean isForeground);
    }
}
