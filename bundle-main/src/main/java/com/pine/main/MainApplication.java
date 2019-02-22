package com.pine.main;

import android.app.Application;

import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class MainApplication {
    private final static String TAG = LogUtils.makeLogTag(MainApplication.class);
    public static Application mApplication;

    public static void init(Application application) {
        mApplication = application;
    }
}
