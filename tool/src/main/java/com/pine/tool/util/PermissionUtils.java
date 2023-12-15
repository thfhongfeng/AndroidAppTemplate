package com.pine.tool.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {
    /**
     * 检查单个权限
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkPermission(Context context, @NonNull String permission) {
        // 检查该权限是否已经获取
        int i = ContextCompat.checkSelfPermission(context, permission);
        // 权限是否已经 授权 GRANTED---授权 DINIED---拒绝
        if (i == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    /**
     * 检查多权限
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String[] checkPermission(Context context, @NonNull String... permissions) {
        List<String> noPermission = new ArrayList<>();
        for (String permission : permissions) {
            // 检查该权限是否已经获取
            if (!checkPermission(context, permission)) {
                noPermission.add(permission);
            }
        }
        String[] result = new String[noPermission.size()];
        return noPermission.toArray(result);
    }
}
