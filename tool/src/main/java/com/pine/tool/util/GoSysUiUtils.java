package com.pine.tool.util;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

public class GoSysUiUtils {
    public static void goSysSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        context.startActivity(intent);
    }

    public static void goWifiSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        context.startActivity(intent);
    }

    public static void goLocationSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }

    // 跳转系统的辅助功能界面
    public static void goAccessibilitySettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        context.startActivity(intent);
    }

    // 跳转到应用程序界面【所有的】
    public static void goManageAllAppSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
        context.startActivity(intent);
    }

    // 跳转系统的蓝牙设置界面
    public static void goBluetoothSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        context.startActivity(intent);
    }

    // 跳转到移动网络设置界面
    public static void goDataRoamingSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
        context.startActivity(intent);
    }

    // 跳转手机显示界面
    public static void goDisplaySettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
        context.startActivity(intent);
    }

    // 跳转语言和输入设备
    public static void goInputMethodSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
        context.startActivity(intent);
    }

    // 跳转位置服务界面【管理已安装的应用程序。】
    public static void goLocationSourceSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }

    // 跳转到声音设置界面
    public static void goSoundSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_SOUND_SETTINGS);
        context.startActivity(intent);
    }
}
