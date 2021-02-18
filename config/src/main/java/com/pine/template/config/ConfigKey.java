package com.pine.template.config;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public interface ConfigKey {
    /*********************** 模块开关Key begin **********************/
    String BUNDLE_WELCOME_KEY = "bundle_welcome";
    String BUNDLE_LOGIN_KEY = "bundle_login";
    String BUNDLE_MAIN_KEY = "bundle_main";
    String BUNDLE_USER_KEY = "bundle_user";
    String BUNDLE_BUSINESS_MVC_KEY = "bundle_business_mvc";
    String BUNDLE_BUSINESS_MVP_KEY = "bundle_business_mvp";
    String BUNDLE_BUSINESS_MVVM_KEY = "bundle_business_mvvm";

    String BUNDLE_DB_SEVER_KEY = "bundle_db_server";
    /*********************** 模块开关Key end ************************/


    /*********************** 功能开关Key begin **********************/
    String FUN_ADD_SHOP_KEY = "fun_add_shop";
    String FUN_ADD_PRODUCT_KEY = "fun_add_product";
    String FUN_ADD_TRAVEL_NOTE_KEY = "fun_add_travel_note";
    /*********************** 功能开关Key end ************************/
}
