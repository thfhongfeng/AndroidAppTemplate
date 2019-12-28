package com.pine.tool.permission;

import android.Manifest;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

/**
 * Created by tanghongfeng on 2019/2/27
 */

public class PermissionTranslate {
    public static String translate(@NonNull List<String> perms) {
        String retStr = "";
        for (String perm : perms) {
            retStr += translate(perm) + ",";
        }
        if (retStr.length() > 0) {
            retStr = retStr.substring(0, retStr.length() - 1);
        }
        return retStr;
    }

    public static String translate(@Size(min = 1) @NonNull String... perms) {
        String retStr = "";
        for (String perm : perms) {
            retStr += translate(perm) + ",";
        }
        if (retStr.length() > 0) {
            retStr = retStr.substring(0, retStr.length() - 1);
        }
        return retStr;
    }

    public static String translate(@NonNull String perm) {
        String retStr = "";
        switch (perm) {
            case Manifest.permission.READ_CONTACTS:
                retStr = "通讯录权限";
                break;
            case Manifest.permission.ACCESS_FINE_LOCATION:
                retStr = "位置权限";
                break;
            case Manifest.permission.READ_PHONE_STATE:
                retStr = "电话权限";
                break;
        }
        return retStr;
    }
}
