package com.pine.tool;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pine.tool.util.LogUtils;

import java.util.HashMap;
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
        mApplication.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                LogUtils.d("ActivityLifecycle", activity + " on created");
                if (mActivityStack.size() == 0) {
                    setAppCreated();
                }
                mActivityStack.add(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                LogUtils.d("ActivityLifecycle", activity + " on started");
                if (mActivityForegroundStack.size() == 0) {
                    setAppIsForeground(true);
                }
                mActivityForegroundStack.add(activity);
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
                mActivityForegroundStack.remove(activity);
                if (mActivityForegroundStack.size() == 0) {
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
                mActivityStack.remove(activity);
                if (mActivityStack.size() == 0) {
                    setAppDestroyed();
                }
            }
        });
    }

    public static HashMap<String, IOnAppStateListener> mOnAppStateListeners = new HashMap<>();
    private static volatile LinkedList<Activity> mActivityStack = new LinkedList<>();
    private static volatile Boolean mAppIsDestroyed = false;
    private static volatile LinkedList<Activity> mActivityForegroundStack = new LinkedList<>();
    private static volatile Boolean mAppIsForeground = false;

    private static void setAppIsForeground(boolean isForeground) {
        boolean change = false;
        synchronized (mAppIsDestroyed) {
            change = mAppIsForeground != isForeground;
            LogUtils.d(TAG, "onAppForegroundChange isForeground:" + isForeground + ", change:" + change);
            mAppIsForeground = isForeground;
        }
        synchronized (mOnAppStateListeners) {
            if (mOnAppStateListeners.size() > 0 && change) {
                Iterator<IOnAppStateListener> iterator
                        = mOnAppStateListeners.values().iterator();
                while (iterator.hasNext()) {
                    IOnAppStateListener listener = iterator.next();
                    if (listener != null) {
                        listener.onAppForegroundChange(isForeground);
                    }
                }
            }
        }
    }

    private static void setAppCreated() {
        synchronized (mAppIsDestroyed) {
            LogUtils.d(TAG, "onAppCreated");
            mAppIsDestroyed = false;
        }
        synchronized (mOnAppStateListeners) {
            if (mOnAppStateListeners.size() > 0) {
                Iterator<IOnAppStateListener> iterator
                        = mOnAppStateListeners.values().iterator();
                while (iterator.hasNext()) {
                    IOnAppStateListener listener = iterator.next();
                    if (listener != null) {
                        listener.onAppCreated();
                    }
                }
            }
        }
    }

    private static void setAppDestroyed() {
        synchronized (mAppIsDestroyed) {
            LogUtils.d(TAG, "onAppDestroyed");
            mAppIsDestroyed = true;
        }
        synchronized (mOnAppStateListeners) {
            if (mOnAppStateListeners.size() > 0) {
                Iterator<IOnAppStateListener> iterator
                        = mOnAppStateListeners.values().iterator();
                while (iterator.hasNext()) {
                    IOnAppStateListener listener = iterator.next();
                    if (listener != null) {
                        listener.onAppDestroyed();
                    }
                }
            }
        }
    }

    public static void addAppStateListener(String tag, IOnAppStateListener listener) {
        if (listener == null) {
            return;
        }
        synchronized (mOnAppStateListeners) {
            mOnAppStateListeners.put(tag, listener);
        }
    }

    public static void removeAppStateListener(String tag) {
        synchronized (mOnAppStateListeners) {
            mOnAppStateListeners.remove(tag);
        }
    }

    public static class ActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    }

    public interface IOnAppStateListener {
        void onAppForegroundChange(boolean isForeground);

        void onAppCreated();

        void onAppDestroyed();
    }

    private static volatile boolean mFinishApp = false;

    public static void finishApp() {
        mFinishApp = true;
        if (mCurResumedActivity != null) {
            mCurResumedActivity.finish();
        }
    }
}