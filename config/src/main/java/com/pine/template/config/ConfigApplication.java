package com.pine.template.config;

import com.pine.template.config.switcher.ConfigSwitcherServer;
import com.pine.tool.RootApplication;
import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2019/11/1.
 */

public class ConfigApplication extends RootApplication {
    private final static String TAG = LogUtils.makeLogTag(ConfigApplication.class);

    public static void onCreate() {
        ConfigSwitcherServer.init();
    }

    public static void attach() {

    }
}
