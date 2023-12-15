package com.pine.tool.permission;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.pine.tool.R;
import com.pine.tool.permission.easy.AppSettingsDialog;
import com.pine.tool.permission.easy.EasyPermissions;
import com.pine.tool.permission.easy.PermissionRequest;
import com.pine.tool.ui.Activity;
import com.pine.tool.ui.Fragment;

import java.util.Arrays;

/**
 * Created by tanghongfeng on 2019/2/28
 */

public class PermissionManager {
    public static boolean hasPermissions(@NonNull Context context,
                                         @Size(min = 1) @NonNull String... perms) {
        return EasyPermissions.hasPermissions(context, perms);
    }

    public static void requestPermission(@NonNull Activity activity, int requestCode,
                                         IPermissionCallback callback,
                                         @Size(min = 1) @NonNull String... perms) {
        PermissionBean bean = new PermissionBean(requestCode, perms);
        bean.setRationaleContent(activity.getString(R.string.tool_rationale_need));
        bean.setCallback(callback);
        requestPermission(activity, bean);
    }

    public static void requestPermission(@NonNull Activity activity, PermissionBean bean) {
        EasyPermissions.requestPermissions(
                new PermissionRequest.Builder(activity, bean.getRequestCode(), bean.getPerms())
                        .setRationale(bean.getRationaleContent())
                        .setPositiveButtonText(bean.getRationalePositiveBtnText())
                        .setNegativeButtonText(bean.getRationaleNegativeBtnText())
                        .setTheme(bean.getRationaleTheme())
                        .build());
        activity.getPermissionRequestMap().put(bean.getRequestCode(), bean);
    }

    public static void requestPermission(@NonNull Fragment fragment, int requestCode,
                                         IPermissionCallback callback,
                                         @Size(min = 1) @NonNull String... perms) {
        PermissionBean bean = new PermissionBean(requestCode, perms);
        bean.setRationaleContent(fragment.getString(R.string.tool_rationale_need));
        bean.setCallback(callback);
        requestPermission(fragment, bean);
    }

    public static void requestPermission(@NonNull Fragment fragment, PermissionBean bean) {
        EasyPermissions.requestPermissions(
                new PermissionRequest.Builder(fragment, bean.getRequestCode(), bean.getPerms())
                        .setRationale(bean.getRationaleContent())
                        .setPositiveButtonText(bean.getRationalePositiveBtnText())
                        .setNegativeButtonText(bean.getRationaleNegativeBtnText())
                        .setTheme(bean.getRationaleTheme())
                        .build());
        fragment.getPermissionRequestMap().put(bean.getRequestCode(), bean);
    }

    public static boolean showGoAppSettingsDialog(@NonNull Activity activity,
                                                  int requestCode,
                                                  PermissionBean bean,
                                                  @NonNull String... perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(activity, Arrays.asList(perms))) {
            AppSettingsDialog.Builder builder = new AppSettingsDialog.Builder(activity).setPermRequestCode(requestCode);
            if (bean != null) {
                builder.setTitle(bean.getGoSettingContent())
                        .setThemeResId(bean.getGoSettingTheme())
                        .setRationale(bean.getGoSettingContent())
                        .setPositiveButton(bean.getGoSettingPositiveBtnText())
                        .setNegativeButton(bean.getGoSettingPositiveBtnText())
                        .setRationale(bean.getGoSettingContent() != null ?
                                bean.getGoSettingContent() : getDefaultGoSettingContent(activity, perms));
            }
            builder.build(perms).show();
            return true;
        }
        return false;
    }

    public static boolean showGoAppSettingsDialog(@NonNull Fragment fragment,
                                                  int requestCode,
                                                  PermissionBean bean,
                                                  @NonNull String... perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(fragment, Arrays.asList(perms))) {
            AppSettingsDialog.Builder builder = new AppSettingsDialog.Builder(fragment).setPermRequestCode(requestCode);
            if (bean != null) {
                builder.setTitle(bean.getGoSettingContent())
                        .setThemeResId(bean.getGoSettingTheme())
                        .setRationale(bean.getGoSettingContent())
                        .setPositiveButton(bean.getGoSettingPositiveBtnText())
                        .setNegativeButton(bean.getGoSettingPositiveBtnText())
                        .setRationale(bean.getGoSettingContent() != null ?
                                bean.getGoSettingContent() : getDefaultGoSettingContent(fragment.getContext(), perms));
            }
            builder.build(perms).show();
            return true;
        }
        return false;
    }

    private static String getDefaultGoSettingContent(Context context, @NonNull String... perms) {
        String permsStr = PermissionTranslate.translate(perms);
        String rational = TextUtils.isEmpty(permsStr) ? context.getString(R.string.tool_content_1_settings_dialog) :
                context.getString(R.string.tool_content_2_settings_dialog, permsStr);
        return rational;
    }
}
