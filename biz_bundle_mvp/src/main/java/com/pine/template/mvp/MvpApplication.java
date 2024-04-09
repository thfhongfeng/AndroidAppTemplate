package com.pine.template.mvp;

import com.pine.tool.RootApplication;
import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public class MvpApplication extends RootApplication {
    private final static String TAG = LogUtils.makeLogTag(MvpApplication.class);

    public static void onCreate() {
        LogUtils.d(TAG, "onCreate");
    }

    public static void attach() {
        LogUtils.d(TAG, "attach");
    }
}
