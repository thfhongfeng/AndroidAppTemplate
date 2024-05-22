package com.pine.template.base.device_sdk;

import android.content.Context;

public class DeviceSdkProxy extends DefaultDeviceSdkProxy {

    public boolean init(Context context) throws DeviceSdkException {
        return true;
    }

    @Override
    public boolean init(Context context, String authorityKey) throws DeviceSdkException {
        return true;
    }

    /////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
}
