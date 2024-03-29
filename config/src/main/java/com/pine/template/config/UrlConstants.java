package com.pine.template.config;

import android.text.TextUtils;

import com.pine.template.config.switcher.ConfigSwitcherServer;

public class UrlConstants {
    public static String SERVER() {
        return getUrlFromConfig(ConfigKey.CONFIG_SERVER_URL);
    }

    public static String FILE_UPLOAD() {
        return getUrlFromConfig(ConfigKey.CONFIG_FILE_UPLOAD_URL);
    }

    public static String FILE_SERVER() {
        return getUrlFromConfig(ConfigKey.CONFIG_FILE_SERVER_URL);
    }

    public static String SOCKET() {
        return getUrlFromConfig(ConfigKey.CONFIG_SOCKET_URL);
    }

    public static String CONFIG() {
        return getUrlFromConfig(ConfigKey.CONFIG_APP_CONFIG_URL);
    }

    public static String APP_TRACK() {
        return getUrlFromConfig(ConfigKey.CONFIG_APP_TRACK_URL);
    }

    public static String APK_UPDATE() {
        return ConfigSwitcherServer.getConfig(ConfigKey.CONFIG_APK_UPDATE_URL);
    }

    public static String REGISTER_ACCOUNT() {
        return ConfigSwitcherServer.getConfig(ConfigKey.CONFIG_APP_REGISTER_ACCOUNT_URL);
    }

    public static String LOGIN() {
        return ConfigSwitcherServer.getConfig(ConfigKey.CONFIG_APP_LOGIN_URL);
    }

    public static String LOGOUT() {
        return ConfigSwitcherServer.getConfig(ConfigKey.CONFIG_APP_LOGOUT_URL);
    }

    public static String VERIFY_CODE() {
        return ConfigSwitcherServer.getConfig(ConfigKey.CONFIG_APP_VERIFY_CODE_URL);
    }

    public static String getUrlFromConfig(String configKey) {
        String url = ConfigSwitcherServer.getConfig(configKey);
        if (TextUtils.isEmpty(url) || url.endsWith("/")) {
            return url;
        } else {
            return url + "/";
        }
    }
}
