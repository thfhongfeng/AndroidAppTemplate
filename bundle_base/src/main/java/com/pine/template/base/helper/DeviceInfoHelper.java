package com.pine.template.base.helper;

import android.content.Context;

import com.pine.template.base.DeviceConfig;
import com.pine.template.bundle_base.BuildConfig;
import com.pine.tool.util.AppUtils;

import java.util.HashMap;
import java.util.Locale;

public class DeviceInfoHelper {
    private static DeviceInfo mDeviceInfo;

    public static HashMap<String, String> getDeviceInfoParams() {
        DeviceInfo deviceInfo = getDeviceInfo();
        HashMap<String, String> params = new HashMap<>();
        params.put("appKey", deviceInfo.getApkPkgName());
        params.put("packageName", deviceInfo.getApkPkgName());
        params.put("sdkType", deviceInfo.getSdkType());
        params.put("versionName", deviceInfo.getApkVersionName());
        params.put("versionCode", deviceInfo.getApkVersionCode());
        params.put("systemVersion", deviceInfo.getSystemVersion());
        params.put("deviceModel", deviceInfo.getDeviceModel());
        params.put("deviceId", deviceInfo.getDeviceId());
        params.put("local", deviceInfo.getLocal());
        params.put("productCustomer", deviceInfo.getProductCustomer());
        return params;
    }

    public static DeviceInfo getDeviceInfo() {
        Context context = AppUtils.getApplicationContext();
        Locale current = context.getResources().getConfiguration().locale;
        String language = current.getLanguage(); // 获取语言代码
        String country = current.getCountry(); // 获取国家/地区代码
        // 组合成完整的语言区域代码
        String localeString = language + "-" + (country.isEmpty() ? "" : country);
        if (mDeviceInfo == null) {
            mDeviceInfo = new DeviceInfo();
            mDeviceInfo.setSdkType(BuildConfig.FLAVOR);
            mDeviceInfo.setApkPkgName(context.getPackageName());
            mDeviceInfo.setApkVersionCode(String.valueOf(AppUtils.getVersionCode()));
            mDeviceInfo.setApkVersionName(AppUtils.getVersionName());

            mDeviceInfo.setDeviceId(DeviceConfig.getDeviceUniqueNum(context));
            mDeviceInfo.setDeviceModel(DeviceConfig.getDeviceModel(context));
            mDeviceInfo.setSystemVersion(DeviceConfig.getSystemVersion(context));
            mDeviceInfo.setLocal(localeString);
            mDeviceInfo.setSystemDisplayId(DeviceConfig.getSystemDisplayId());
            mDeviceInfo.setProductCustomer(DeviceConfig.getProductCustomer());
        }
        DeviceInfo deviceInfo = mDeviceInfo.copy();
        deviceInfo.setLocal(localeString);
        deviceInfo.setProductCustomer(DeviceConfig.getProductCustomer());
        return deviceInfo;
    }
}