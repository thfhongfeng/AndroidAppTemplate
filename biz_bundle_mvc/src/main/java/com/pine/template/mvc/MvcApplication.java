package com.pine.template.mvc;

import com.pine.tool.RootApplication;
import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvcApplication extends RootApplication {
    private final static String TAG = LogUtils.makeLogTag(MvcApplication.class);

    public static void onCreate() {
        LogUtils.d(TAG, "onCreate");
    }

    public static void attach() {
        LogUtils.d(TAG, "attach");
    }
}
