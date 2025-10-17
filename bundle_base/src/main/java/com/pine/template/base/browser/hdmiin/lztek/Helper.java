package com.pine.template.base.browser.hdmiin.lztek;


public final class Helper {
    public static boolean isSystemUser() {
        int uid = android.os.Process.myUid();
        return (0 == uid || uid == android.os.Process.SYSTEM_UID);
    }

    //android.os.SystemProperties.get
    public static String getSystemProperty(String key) {
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Class<?>[] clzParams = {String.class};
            java.lang.reflect.Method method = clazz.getDeclaredMethod("get", clzParams);
            Object obj = null == method ? "" : method.invoke(null, key);
            return obj != null && obj instanceof CharSequence ? obj.toString().trim() : "";
        } catch (Exception e) {
            return "";
        }
    }

    //android.os.SystemProperties.set
    public static void setSystemProperty(String key, String value) {
        if (null == key || (key = key.trim()).length() == 0) {
            throw new IllegalArgumentException("Invalid system properties key");
        }
        if (null == value) {
            value = "";
        }
        if (isSystemUser()) {
            try {
                Class<?> clazz = Class.forName("android.os.SystemProperties");
                Class<?>[] clzParams = {String.class, String.class};
                java.lang.reflect.Method method = clazz.getDeclaredMethod("set", clzParams);
                method.invoke(null, key, value);
            } catch (Exception e) {
                android.util.Log.e("#ERROR#",
                        "### SystemProperties.set(" + key + ", " + value + ") " +
                                "exception: " + e.getMessage(), e);
            }
        } else {
            ExecUtl.execCommand("setprop " + key + " " + value);
        }
    }
}
