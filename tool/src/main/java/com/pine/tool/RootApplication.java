package com.pine.tool;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.pine.tool.util.LogUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

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
                if (mActivityStack.size() == 0) {
                    setAppIsForeground(true);
                }
                mActivityStack.add(activity);
                if (mFinishApp) {
                    activity.finish();
                }
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
                mActivityStack.remove(activity);
                if (mActivityStack.size() == 0) {
                    setAppIsForeground(false);
                    mFinishApp = false;
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

    private static volatile LinkedList<Activity> mActivityStack = new LinkedList<>();
    private static volatile boolean mAppIsForeground = false;
    public static HashSet<IOnAppForegroundChangeListener> mOnAppForegroundChangeListenerSet = new HashSet<>();

    private synchronized static void setAppIsForeground(boolean isForeground) {
        boolean change = mAppIsForeground != isForeground;
        LogUtils.d(TAG, "onAppForegroundChange isForeground:" + isForeground + ", change:" + change);
        mAppIsForeground = isForeground;
        if (mOnAppForegroundChangeListenerSet.size() > 0 && change) {
            Iterator<IOnAppForegroundChangeListener> iterator = mOnAppForegroundChangeListenerSet.iterator();
            while (iterator.hasNext()) {
                IOnAppForegroundChangeListener listener = iterator.next();
                if (listener != null) {
                    listener.onAppForegroundChange(isForeground);
                }
            }
        }
    }

    public synchronized static void addAppForegroundChangeListener(IOnAppForegroundChangeListener listener) {
        mOnAppForegroundChangeListenerSet.add(listener);
    }

    public synchronized static void removeAppForegroundChangeListener(IOnAppForegroundChangeListener listener) {
        mOnAppForegroundChangeListenerSet.remove(listener);
    }

    public interface IOnAppForegroundChangeListener {
        void onAppForegroundChange(boolean isForeground);
    }

    private static volatile boolean mFinishApp = false;

    public static void finishApp() {
        mFinishApp = true;
        if (mCurResumedActivity != null) {
            mCurResumedActivity.finish();
        }
    }
}