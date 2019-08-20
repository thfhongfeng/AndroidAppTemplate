package com.pine.tool.permission;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.annotation.StyleRes;

/**
 * Created by tanghongfeng on 2019/2/27
 */

public class PermissionBean {
    /**
     * 权限数组  {@link Manifest}
     */
    private String[] perms;
    // 请求code
    private int requestCode;
    // 回调
    private IPermissionCallback callback;

    // 如果上一次申请的权限没有被全部授予，则再次进入申请时会弹出Rationale弹出框提示是否要进行权限授予。
    // Rationale弹出框标题
    private String rationaleTitle = null;
    // Rationale弹出框描述内容
    private String rationaleContent = null;
    // Rationale弹出框同意按键文本
    private String rationalePositiveBtnText = null;
    // Rationale弹出框不同意按键文本
    private String rationaleNegativeBtnText = null;
    // Rationale弹出框样式id
    @StyleRes
    private int rationaleTheme = -1;

    // 如果申请的权限没有被全部授予，且有未被授予的权限勾选了“禁止后不在询问”选项，则会弹出goSetting弹出框提示是否要进入设置里去开启权限。
    // goSetting弹出框标题
    private String goSettingTitle = null;
    // goSetting弹出框描述内容
    private String goSettingContent = null;
    // goSetting弹出框同意按键文本
    private String goSettingPositiveBtnText = null;
    // goSetting弹出框不同意按键文本
    private String goSettingNegativeBtnText = null;
    // goSetting弹出框样式id
    @StyleRes
    private int goSettingTheme = -1;

    public PermissionBean(int requestCode, @Size(min = 1) @NonNull String... perms) {
        this.requestCode = requestCode;
        this.perms = perms;
    }

    public String[] getPerms() {
        return perms;
    }

    public void setPerms(String[] perms) {
        this.perms = perms;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public IPermissionCallback getCallback() {
        return callback;
    }

    public void setCallback(IPermissionCallback callback) {
        this.callback = callback;
    }

    public String getRationaleTitle() {
        return rationaleTitle;
    }

    public void setRationaleTitle(String rationaleTitle) {
        this.rationaleTitle = rationaleTitle;
    }

    public String getRationaleContent() {
        return rationaleContent;
    }

    public void setRationaleContent(String rationaleContent) {
        this.rationaleContent = rationaleContent;
    }

    public String getRationalePositiveBtnText() {
        return rationalePositiveBtnText;
    }

    public void setRationalePositiveBtnText(String rationalePositiveBtnText) {
        this.rationalePositiveBtnText = rationalePositiveBtnText;
    }

    public String getRationaleNegativeBtnText() {
        return rationaleNegativeBtnText;
    }

    public void setRationaleNegativeBtnText(String rationaleNegativeBtnText) {
        this.rationaleNegativeBtnText = rationaleNegativeBtnText;
    }

    @StyleRes
    public int getRationaleTheme() {
        return rationaleTheme;
    }

    public void setRationaleTheme(@StyleRes int rationaleTheme) {
        this.rationaleTheme = rationaleTheme;
    }

    public String getGoSettingTitle() {
        return goSettingTitle;
    }

    public void setGoSettingTitle(String goSettingTitle) {
        this.goSettingTitle = goSettingTitle;
    }

    public String getGoSettingContent() {
        return goSettingContent;
    }

    public void setGoSettingContent(String goSettingContent) {
        this.goSettingContent = goSettingContent;
    }

    public String getGoSettingPositiveBtnText() {
        return goSettingPositiveBtnText;
    }

    public void setGoSettingPositiveBtnText(String goSettingPositiveBtnText) {
        this.goSettingPositiveBtnText = goSettingPositiveBtnText;
    }

    public String getGoSettingNegativeBtnText() {
        return goSettingNegativeBtnText;
    }

    public void setGoSettingNegativeBtnText(String goSettingNegativeBtnText) {
        this.goSettingNegativeBtnText = goSettingNegativeBtnText;
    }

    @StyleRes
    public int getGoSettingTheme() {
        return goSettingTheme;
    }

    public void setGoSettingTheme(@StyleRes int goSettingTheme) {
        this.goSettingTheme = goSettingTheme;
    }
}
