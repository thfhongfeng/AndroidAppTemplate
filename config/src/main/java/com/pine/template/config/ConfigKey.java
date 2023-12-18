package com.pine.template.config;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public interface ConfigKey extends BaseConfigKey {
    /*********************** 模块开关Key begin **********************/
    String BUNDLE_DB_SEVER = "BUNDLE_DB_SEVER";
    String BUNDLE_BUSINESS_MVC = "BUNDLE_BUSINESS_MVC";
    String BUNDLE_BUSINESS_MVP = "BUNDLE_BUSINESS_MVP";
    String BUNDLE_BUSINESS_MVVM = "BUNDLE_BUSINESS_MVVM";
    /*********************** 模块开关Key end ************************/


    /*********************** 功能Key begin **********************/
    String FUN_ADD_SHOP = "FUN_ADD_SHOP";
    String FUN_ADD_PRODUCT = "FUN_ADD_PRODUCT";
    String FUN_ADD_TRAVEL_NOTE = "FUN_ADD_TRAVEL_NOTE";

    // 是否有禁用休眠功能（默认不禁止，有些机器不支持休眠需要禁止）
    String FUN_FORBID_SLEEP = "FUN_FORBID_SLEEP";
    /*********************** 功能Key begin **********************/


    /*********************** 功能开关Key begin **********************/
    // 是否开启离线模式
    String ENABLE_OFFLINE_MODE = "ENABLE_OFFLINE_MODE";
    /*********************** 功能开关Key end ************************/


    /*********************** 配置Key begin **********************/

    /*********************** 配置Key end ************************/


    /*********************** 缓存Key begin ************************/

    /*********************** 缓存Key end ************************/
}
