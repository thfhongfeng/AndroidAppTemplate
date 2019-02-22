package com.pine.demo;

import android.app.Application;

import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class DemoApplication {
    private final static String TAG = LogUtils.makeLogTag(DemoApplication.class);
    public static Application mApplication;

    public static void init(Application application) {
        mApplication = application;
    }
}
