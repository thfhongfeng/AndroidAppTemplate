package com.pine.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.pine.base.track.AppTrackManager;
import com.pine.config.switcher.ConfigSwitcherServer;
import com.pine.router.RouterApplication;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public class BaseApplication {
    private final static String TAG = LogUtils.makeLogTag(BaseApplication.class);
    private static volatile boolean mIsInit;
    public static volatile Activity mCurResumedActivity;
    public static Application mApplication;
    private static volatile boolean mIsLogin;

    protected BaseApplication() {
        throw new IllegalArgumentException(getClass() + " prohibited from being constructed");
    }

    public final synchronized static boolean isLogin() {
        return mIsLogin;
    }

    public final synchronized static void setLogin(boolean isLogin) {
        mIsLogin = isLogin;
        ConfigSwitcherServer.getInstance().setLogin(isLogin);
    }

    public final synchronized static void init(Application application) {
        if (mIsInit) {
            return;
        }
        mApplication = application;
        registerActivity();
        RouterApplication.attach(mApplication);

        if (mApplication.getPackageName().equals(AppUtils.getCurProcessName(mApplication))) {
            AppTrackManager.getInstance().init(application, BaseUrlConstants.APP_TRACK_UPLOAD);
            AppTrackManager.getInstance().uploadAllExistTrack(null);
        }
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

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                LogUtils.d(TAG, activity + " on destroyed");
            }
        });
    }
}
