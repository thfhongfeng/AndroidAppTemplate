package com.pine.template.base.helper;

import android.content.Context;

import com.pine.template.base.DeviceConfig;
import com.pine.template.bundle_base.BuildConfig;
import com.pine.tool.util.AppUtils;

import java.util.HashMap;

public class DeviceInfoHelper {
    public static HashMap<String, String> getDeviceInfoParams() {
        final String versionName = AppUtils.getVersionName();
        final int versionCode = AppUtils.getVersionCode();
        HashMap<String, String> params = new HashMap<>();
        Context context = AppUtils.getApplicationContext();
        params.put("appKey", context.getPackageName());
        params.put("packageName", context.getPackageName());
        params.put("sdkType", BuildConfig.FLAVOR);
        params.put("versionName", versionName);
        params.put("versionCode", versionCode + "");
        params.put("systemVersion", DeviceConfig.getSystemVersion(context));
        params.put("deviceModel", DeviceConfig.getDeviceModel(context));
        params.put("deviceId", DeviceConfig.getDeviceUniqueNum(context));
        return params;
    }
}
