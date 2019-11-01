package com.pine.base;

import com.pine.tool.RootApplication;
import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public class BaseApplication extends RootApplication {
    private final static String TAG = LogUtils.makeLogTag(BaseApplication.class);

    protected BaseApplication() {
        throw new IllegalArgumentException(getClass() + " prohibited from being constructed");
    }
}
