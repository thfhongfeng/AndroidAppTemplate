package com.pine.tool.util;

import android.content.Context;
import android.hardware.SensorManager;

public class SensorUtils {
    private static int hasLight = -1;

    public static boolean hasLightSensor(Context context) {
        if (hasLight == -1) {
            SensorManager mSensorManager = (SensorManager)
                    context.getSystemService(Context.SENSOR_SERVICE);
            if (mSensorManager.getDefaultSensor(SensorManager.SENSOR_LIGHT) != null) {
                hasLight = 1;
            } else {
                hasLight = 0;
            }
        }

        return hasLight == 1;
    }
}
