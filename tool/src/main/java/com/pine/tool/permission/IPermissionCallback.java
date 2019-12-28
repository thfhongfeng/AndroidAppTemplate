package com.pine.tool.permission;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by tanghongfeng on 2019/2/27
 */

public interface IPermissionCallback {
    // 用户授予某些权限回调（在用户对所有权限申请做出操作后，如果有被同意的权限，则回调，否则不回调）
    void onPermissionsGranted(int requestCode, @NonNull List<String> perms);

    // 用户拒绝某些权限回调（在用户对所有权限申请做出操作后，如果有被拒绝的权限，则回调，否则不回调）
    void onPermissionsDenied(int requestCode, @NonNull List<String> perms);

    // 用户授予了所有申请的权限回调（在用户对所有权限申请做出操作后，如果所有权限被授予，则回调，否则不回调）
    void onAllPermissionGranted(int requestCode);

    // 如果上一次申请的权限没有被全部授予，则再次进入申请时会弹出Rationale弹出框提示是否要进行权限授予。
    // 用户确认要进入权限授予操作的回调
    void onRationaleAccepted(int requestCode);

    // 如果上一次申请的权限没有被全部授予，则再次进入申请时会弹出Rationale弹出框提示是否要进行权限授予。
    // 用户不进入权限授予操作的回调
    void onRationaleDenied(int requestCode);
}
