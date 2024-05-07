package com.pine.template.base;

import android.content.Context;

import com.pine.template.base.helper.DefaultDeviceConfig;
import com.pine.tool.util.AppUtils;

public class DeviceConfig extends DefaultDeviceConfig {
    public static void init() {

    }

    public static String getDeviceUniqueNum(Context context) {
        return AppUtils.getIMEI(context);
    }

    public static String getDeviceModel(Context context) {
        return AppUtils.getDeviceModel();
    }

    public static String getSystemVersion(Context context) {
        return AppUtils.getFwVersion();
    }
}
