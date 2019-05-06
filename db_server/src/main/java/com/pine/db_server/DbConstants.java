package com.pine.db_server;

import com.pine.base.BaseConstants;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public interface DbConstants extends BaseConstants {
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
    String PRODUCT_TABLE_NAME = "product";
    String TRAVEL_NOTE_TABLE_NAME = "travel_note";
    String TRAVEL_NOTE_SHOP_TABLE_NAME = "travel_note_shop";
    String TRAVEL_NOTE_COMMENT_TABLE_NAME = "travel_note_comment";

    String FILE_INFO_TABLE_NAME = "file_info";
}
