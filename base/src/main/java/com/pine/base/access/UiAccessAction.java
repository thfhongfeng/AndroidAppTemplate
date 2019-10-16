package com.pine.base.access;

/**
 * Created by tanghongfeng on 2018/9/16
 */

public interface UiAccessAction {

    // UiAccessTimeInterval.UI_ACCESS_ON_CREATE阶段的UI Login准入检查不通过时，不跳转到登陆界面
    String LOGIN_ACCESS_FALSE_ON_CREATE_NOT_GO_LOGIN = "login_access_false_on_create_not_go_login";
    // UiAccessTimeInterval.UI_ACCESS_ON_CREATE阶段的UI Login准入检查不通过时，不结束当前UI
    String LOGIN_ACCESS_FALSE_ON_CREATE_NOT_FINISH_UI = "login_access_false_on_create_not_finish_ui";
    // UiAccessTimeInterval.UI_ACCESS_ON_NEW_INTENT阶段的UI Login准入检查不通过时，不跳转到登陆界面
    String LOGIN_ACCESS_FALSE_ON_NEW_INTENT_NOT_GO_LOGIN = "login_access_false_on_new_intent_not_go_login";
    // UiAccessTimeInterval.UI_ACCESS_ON_NEW_INTENT阶段的UI Login准入检查不通过时，不结束当前UI
    String LOGIN_ACCESS_FALSE_ON_NEW_INTENT_NOT_FINISH_UI = "login_access_false_on_new_intent_not_finish_ui";
    // UiAccessTimeInterval.UI_ACCESS_ON_RESUME阶段的UI Login准入检查不通过时，不跳转到登陆界面
    String LOGIN_ACCESS_FALSE_ON_RESUME_NOT_GO_LOGIN = "login_access_false_on_resume_not_go_login";
    // UiAccessTimeInterval.UI_ACCESS_ON_RESUME阶段的UI Login准入检查不通过时，不结束当前UI
    String LOGIN_ACCESS_FALSE_ON_RESUME_NOT_FINISH_UI = "login_access_false_on_resume_not_finish_ui";

    // UiAccessTimeInterval.UI_ACCESS_ON_CREATE阶段的UI Vip准入检查不通过时，不跳转到VIP界面
    String VIP_ACCESS_FALSE_ON_CREATE_NOT_GO_VIP_UI = "vip_access_false_on_create_not_go_vip_ui";
    // UiAccessTimeInterval.UI_ACCESS_ON_CREATE阶段的UI Vip准入检查不通过时，不结束当前UI
    String VIP_ACCESS_FALSE_ON_CREATE_NOT_FINISH_UI = "vip_access_false_on_create_not_finish_ui";
    // UiAccessTimeInterval.UI_ACCESS_ON_NEW_INTENT阶段的UI Vip准入检查不通过时，不跳转到VIP界面
    String VIP_ACCESS_FALSE_ON_NEW_INTENT_NOT_GO_VIP_UI = "vip_access_false_on_new_intent_not_go_vip_ui";
    // UiAccessTimeInterval.UI_ACCESS_ON_NEW_INTENT阶段的UI Vip准入检查不通过时，不结束当前UI
    String VIP_ACCESS_FALSE_ON_NEW_INTENT_NOT_FINISH_UI = "vip_access_false_on_new_intent_not_finish_ui";
    // UiAccessTimeInterval.UI_ACCESS_ON_RESUME阶段的UI Vip准入检查不通过时，不跳转到VIP界面
    String VIP_ACCESS_FALSE_ON_RESUME_NOT_GO_VIP_UI = "vip_access_false_on_resume_not_go_vip_ui";
    // UiAccessTimeInterval.UI_ACCESS_ON_RESUME阶段的UI Vip准入检查不通过时，不结束当前UI
    String VIP_ACCESS_FALSE_ON_RESUME_NOT_FINISH_UI = "vip_access_false_on_resume_not_finish_ui";
}
