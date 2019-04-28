package com.pine.mvp;

import android.app.Application;

import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class MvpApplication extends Application {
    private final static String TAG = LogUtils.makeLogTag(MvpApplication.class);
    public static Application mApplication;

    public static void init(Application application) {
        mApplication = application;
    }
}
