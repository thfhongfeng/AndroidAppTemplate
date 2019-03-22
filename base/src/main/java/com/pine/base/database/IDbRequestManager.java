package com.pine.base.database;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Map;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public interface IDbRequestManager {
    String SESSION_ID = "JSESSIONID";
    String COOKIE_KEY = "Cookie";

    // 数据库名
    String DATABASE_NAME = "pine.db";
    // 数据库版本
    int DATABASE_VERSION = 1;

    // 表名
    String ACCOUNT_TABLE_NAME = "account";
    String ACCOUNT_LOGIN_TABLE_NAME = "account_login";
    String REG_VERIFY_CODE_TABLE_NAME = "reg_verify_code";
    String SWITCHER_CONFIG_TABLE_NAME = "switcher_config";
    String APP_VERSION_TABLE_NAME = "app_version";
    String SHOP_TABLE_NAME = "shop";
    String SHOP_TYPE_TABLE_NAME = "shop_type";
    String TRAVEL_NOTE_TABLE_NAME = "travel_note";

    boolean execSQL(@NonNull Context context, String sql);

    @NonNull
    DbResponse callCommand(@NonNull Context context, @NonNull DbRequestBean requestBean,
                           Map<String, Map<String, String>> header);

    enum ActionType {
        COMMON, // common
        RETRY_AFTER_RE_LOGIN, //  retry after re-login
        RETRY_WHEN_ERROR     // retry when error
    }
}
