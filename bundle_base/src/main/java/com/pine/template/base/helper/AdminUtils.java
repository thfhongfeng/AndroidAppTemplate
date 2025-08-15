package com.pine.template.base.helper;

import android.text.TextUtils;

import com.pine.app.template.bundle_base.BuildConfigKey;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.template.bundle_base.BuildConfig;

public class AdminUtils {
    public static boolean checkAdminPwd(String pwd) {
        boolean match = BuildConfig.DEBUG || (!TextUtils.isEmpty(pwd) &&
                pwd.equals(ConfigSwitcherServer.getConfig(BuildConfigKey.CONFIG_ADMIN_PWD)));
        return match;
    }
}
