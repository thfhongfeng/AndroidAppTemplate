package com.pine.template.base;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.pine.app.template.bundle_base.BuildConfigKey;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.template.base.helper.DefaultDeviceConfig;
import com.pine.tool.util.AppUtils;

public class DeviceConfig extends DefaultDeviceConfig {
    public static void init() {

    }

    public static String getDeviceUniqueNum(Context context) {
        String deviceId = AppUtils.getDeviceNumId(context);
        if (TextUtils.equals("020000000000", deviceId)) {
            deviceId = AppUtils.getIMEI(context);
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = Settings.Secure.getString(
                        context.getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }
        return TextUtils.isEmpty(deviceId) ? "020000000000" : deviceId;
    }

    public static String getDeviceModel(Context context) {
        return AppUtils.getDeviceModel();
    }

    public static String getSystemVersion(Context context) {
        return AppUtils.getFwVersion();
    }

    public static String getSystemDisplayId() {
        return AppUtils.getSystemDisplayId();
    }

    public static String getProductCustomer() {
        String productCustomer = ConfigSwitcherServer.getConfig(BuildConfigKey.PRODUCT_CUSTOMER_TAG);
        return productCustomer;
    }

    public static void setProductCustomer(String productCustomer) {
        ConfigSwitcherServer.saveConfig(BuildConfigKey.PRODUCT_CUSTOMER_TAG, productCustomer);
    }
}