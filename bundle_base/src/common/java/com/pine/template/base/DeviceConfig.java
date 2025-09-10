package com.pine.template.base;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.template.base.device_sdk.DeviceSdkException;
import com.pine.template.base.device_sdk.DeviceSdkManager;
import com.pine.template.base.helper.DefaultDeviceConfig;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

public class DeviceConfig extends DefaultDeviceConfig {
    private static final String TAG = DeviceConfig.class.getSimpleName();

    public final static String PRODUCT_CUSTOMER = "persist.vendor.product_customer_tag";

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

    public static boolean isMyDeviceId(@NonNull Context context, String deviceId) {
        return TextUtils.isEmpty(deviceId) || TextUtils.equals(deviceId,
                DeviceConfig.getDeviceUniqueNum(context));
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
        String productCustomer = "";
        try {
            productCustomer = DeviceSdkManager.getInstance().getProperty(PRODUCT_CUSTOMER, "");
        } catch (DeviceSdkException e) {
            LogUtils.w(TAG, "DeviceSdkException setProperty getProperty, ignore");
        }
        return productCustomer;
    }

    public static void setProductCustomer(String tag) {
        try {
            DeviceSdkManager.getInstance().setProperty(PRODUCT_CUSTOMER, tag);
        } catch (DeviceSdkException e) {
            LogUtils.w(TAG, "DeviceSdkException setProperty init, ignore");
        }
    }
}
