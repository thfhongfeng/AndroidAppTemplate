package com.pine.template.config;

public interface BaseConfigKey {
    /*********************** 模块开关Key begin **********************/
    // 欢迎模块
    String BUNDLE_WELCOME_KEY = "BUNDLE_WELCOME_KEY";
    // 登录模块
    String BUNDLE_LOGIN_KEY = "BUNDLE_LOGIN_KEY";
    // 主模块
    String BUNDLE_MAIN_KEY = "BUNDLE_MAIN_KEY";
    // 用户模块
    String BUNDLE_USER_KEY = "BUNDLE_USER_KEY";
    /*********************** 模块开关Key end ************************/

    /*********************** 功能Key begin **********************/
    // 应用跟踪功能
    String FUN_APP_TRACK = "FUN_APP_TRACK";

    // 检查设备是否获得了应用的授权
    String CHECK_DEVICE_SUPPORT_AUTHORITY = "CHECK_DEVICE_SUPPORT_AUTHORITY";
    /*********************** 功能Key begin **********************/

    /*********************** 功能开关Key begin **********************/
    // 是否开启自动更新
    String ENABLE_AUTO_CHECK_UPDATE = "ENABLE_AUTO_CHECK_UPDATE";
    // 是否开启应用跟踪
    String ENABLE_APP_TRACK = "ENABLE_APP_TRACK";
    // 是否允许应用跟踪信息上传
    String ENABLE_UPLOAD_APP_TRACK = "ENABLE_UPLOAD_APP_TRACK";
    // 是否开启自动重启
    String ENABLE_SCHEDULE_REBOOT = "ENABLE_SCHEDULE_REBOOT";

    // 是否开启TTS
    String ENABLE_OTHER_TTS = "ENABLE_OTHER_TTS";

    // 是否开启双语显示
    String ENABLE_BILINGUAL_TEXT = "ENABLE_BILINGUAL_TEXT";

    // 是否关闭设备检测
    String DISABLE_DEVICE_TEST = "DISABLE_DEVICE_TEST";
    /*********************** 功能开关Key end ************************/


    /*********************** 配置Key begin **********************/
    // 业务服务器根地址
    String CONFIG_SERVER_URL = "CONFIG_SERVER_URL";
    // 文件上传完整地址
    String CONFIG_FILE_UPLOAD_URL = "CONFIG_FILE_UPLOAD_URL";
    // 文件服务器完整地址
    String CONFIG_FILE_SERVER_URL = "CONFIG_FILE_SERVER_URL";
    // MQTT地址
    String CONFIG_MQTT_HOST = "CONFIG_MQTT_HOST";
    // SOCKET完整地址
    String CONFIG_SOCKET_URL = "CONFIG_SOCKET_URL";
    // APK更新完整地址
    String CONFIG_APK_UPDATE_URL = "CONFIG_APK_UPDATE_URL";
    // app配置项获取完整地址
    String CONFIG_APP_CONFIG_URL = "CONFIG_APP_CONFIG_URL";
    // app埋点日志完整地址
    String CONFIG_APP_TRACK_URL = "CONFIG_APP_TRACK_URL";
    // app账号注册完整地址
    String CONFIG_APP_REGISTER_ACCOUNT_URL = "CONFIG_APP_REGISTER_ACCOUNT_URL";
    // app账号登录完整地址
    String CONFIG_APP_LOGIN_URL = "CONFIG_APP_LOGIN_URL";
    // app账号登出完整地址
    String CONFIG_APP_LOGOUT_URL = "CONFIG_APP_LOGOUT_URL";
    // app账号图形验证码完整地址
    String CONFIG_APP_VERIFY_CODE_URL = "CONFIG_APP_VERIFY_CODE_URL";

    // 应用跟踪数据循环上传间隔（大于0为时间间隔，单位秒。否则不做循环上传）
    String CONFIG_APP_TRACK_LOOP_INTERVAL = "CONFIG_APP_TRACK_LOOP_INTERVAL";
    // 应用跟踪数据最大存储数量
    String CONFIG_APP_TRACK_MAX_COUNT = "CONFIG_APP_TRACK_MAX_COUNT";
    String CONFIG_H5_PRIVACY_USER = "CONFIG_H5_PRIVACY_USER";
    String CONFIG_H5_PRIVACY_POLICY = "CONFIG_H5_PRIVACY_POLICY";

    String CONFIG_BRIGHT = "CONFIG_BRIGHT";
    String CONFIG_VOLUME = "CONFIG_VOLUME";
    String CONFIG_SLEEP_TIME = "CONFIG_SLEEP_TIME";
    String CONFIG_SCHEDULE_REBOOT_TIME = "CONFIG_SCHEDULE_REBOOT_TIME";

    // 设置界面无操作自动回退到主界面延迟时间(小于等于0表示不返回)
    String CONFIG_AUTO_FINISH_SETTINGS_UI_DELAY = "ENABLE_AUTO_FINISH_SETTINGS_UI_DELAY";

    String CONFIG_FIRST_LOCAL = "CONFIG_FIRST_LOCAL";
    String CONFIG_SECOND_LOCAL = "CONFIG_SECOND_LOCAL";

    String CONFIG_LOCAL_PWD = "CONFIG_LOCAL_PWD";
    /*********************** 配置Key end ************************/

    /*********************** 缓存Key begin ************************/
    // 单位：秒
    String CACHE_MQTT_TTL_INTERVAL = "CACHE_MQTT_TTL_INTERVAL";
    /*********************** 缓存Key end ************************/
}
