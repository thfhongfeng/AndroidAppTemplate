package com.pine.template.base;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.pine.template.base.device_sdk.DeviceSdkException;
import com.pine.template.base.device_sdk.DeviceSdkManager;
import com.pine.template.base.helper.DefaultDeviceConfig;
import com.pine.tool.util.AppUtils;

public class DeviceConfig extends DefaultDeviceConfig {
    public final static String PRODUCT_CUSTOMER = "persist.vendor.product_customer_tag";

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

    public static String getProductCustomer(Context context) {
        String productCustomer = "";
        try {
            productCustomer = DeviceSdkManager.getInstance().getProperty(PRODUCT_CUSTOMER, "");
        } catch (DeviceSdkException e) {
            e.printStackTrace();
        }
        return productCustomer;
    }
}
