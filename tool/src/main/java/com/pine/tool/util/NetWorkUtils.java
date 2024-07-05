package com.pine.tool.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by tanghongfeng on 2018/10/10
 */

public class NetWorkUtils {

    public static boolean checkNetWork() {
        return checkNetWork(AppUtils.getApplicationContext());
    }

    public static boolean checkNetWork(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            @SuppressLint("MissingPermission")
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int getNetworkType() {
        return getNetworkType(AppUtils.getApplicationContext());
    }

    /**
     * Get network type
     *
     * @param context context
     * @return 网络状态
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        NetworkInfo networkInfo = connectivityManager == null
                ? null
                : connectivityManager.getActiveNetworkInfo();
        return networkInfo == null ? -1 : networkInfo.getType();
    }

    /**
     * 获取本机IP地址
     *
     * @return null：没有网络连接
     */
    @SuppressLint("MissingPermission")
    public static String getIpAddress() {
        NetworkInfo networkInfo = ((ConnectivityManager) AppUtils.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                try {
                    Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
                    while (networkInterfaceEnumeration.hasMoreElements()) {
                        NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
                        Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
                        while (inetAddressEnumeration.hasMoreElements()) {
                            InetAddress inetAddress = inetAddressEnumeration.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException se) {

                }
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = ((WifiManager) AppUtils.getApplicationContext().getSystemService(Context.WIFI_SERVICE));
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                return intIP2StringIP(wifiInfo.getIpAddress());
            } else {
                return "";
            }
        }
        return "";
    }

    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * 判断是否包含SIM卡
     *
     * @return 状态
     */
    public static boolean hasSimCard(Context context) {
        TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false; // 没有SIM卡
                break;
        }
        return result;

    }

    public static int getSignalLevel(final SignalStrength signal) {
        try {
            final Method m = SignalStrength.class.getDeclaredMethod("getLevel", (Class[]) null);
            m.setAccessible(true);
            return (Integer) m.invoke(signal, (Object[]) null);
        } catch (Exception e) {
            return -1;
        }
    }
}
