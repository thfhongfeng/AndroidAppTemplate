package com.pine.template.base.browser.hdmiin.lztek;

public final class Amix {

    interface Proxy {
        public void setVolume(int level);

        public void lineInOn();

        public void lineInOff();
    }

    private static Proxy mProxy = getProxy();

    private static Proxy getProxy() {
        java.io.BufferedReader bufferedReader = null;
        try {
            bufferedReader = new java.io.BufferedReader(new java.io.FileReader("/proc/asound/cards"));

            String lineString = "";
            while ((lineString = bufferedReader.readLine()) != null) {
                if (lineString.contains("RK616")) {
                    android.util.Log.d("#DEBUG#", "/proc/asound/cards ==> " + lineString);
                    return new Amix_RK616();
                } else if (lineString.contains("WM8988")) {
                    android.util.Log.d("#DEBUG#", "/proc/asound/cards ==> " + lineString);
                    return new Amix_WM8988();
                }
            }

            android.util.Log.d("#DEBUG#", "/proc/asound/cards use defualt WM8988");
            return new Amix_WM8988(); // WM8988
        } catch (Exception ex) {
            android.util.Log.e("#ERROR#", "read /proc/asound/cards failed" + ex.getMessage(), ex);
            return new Amix_WM8988(); // WM8988
        } finally {
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (java.io.IOException e) {
                }
            }
        }
    }

    public static void setVolume(int level) {
        if (null != mProxy) {
            mProxy.setVolume(level);
        }
    }

    public static void lineInOn() {
        if (null != mProxy) {
            mProxy.lineInOn();
        }
    }

    public static void lineInOff() {
        if (null != mProxy) {
            mProxy.lineInOff();
        }
    }
}
