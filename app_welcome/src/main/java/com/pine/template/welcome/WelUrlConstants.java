package com.pine.template.welcome;

import com.pine.app.template.app_welcome.BuildConfigKey;
import com.pine.template.base.BaseUrlConstants;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;

public class WelUrlConstants extends BaseUrlConstants {
    public static String H5_PRIVACY_USER() {
        return ConfigSwitcherServer.getConfig(BuildConfigKey.CONFIG_H5_PRIVACY_USER);
    }

    public static String PRIVACY_POLICY() {
        return ConfigSwitcherServer.getConfig(BuildConfigKey.CONFIG_H5_PRIVACY_POLICY);
    }
}
