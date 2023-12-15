package com.pine.tool.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.text.format.Formatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WifiUtils {
    private final static String TAG = WifiUtils.class.getSimpleName();

    /**
     * 判断wifi是否打开
     * 需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean getWifiEnabled(Context context) {
        @SuppressLint("WifiManagerLeak")
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    public static String transferWifiIp(int ipAddress) {
        return (ipAddress & 0xFF) + "." +
                ((ipAddress >> 8) & 0xFF) + "." +
                ((ipAddress >> 16) & 0xFF) + "." +
                (ipAddress >> 24 & 0xFF);
    }

    public static String getWifiIp(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        return (ipAddress & 0xFF) + "." +
                ((ipAddress >> 8) & 0xFF) + "." +
                ((ipAddress >> 16) & 0xFF) + "." +
                (ipAddress >> 24 & 0xFF);
    }

    /**
     * 清除保存的wifi（Android API小于30有效）
     *
     * @param context
     * @param ssid
     * @des 清除wifi配置信息
     */
    public static void clearWifiInfo(Context context, String ssid) {

        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        String newSSID = "\"" + ssid + "\"";
        if (!(ssid.startsWith("\"") && ssid.endsWith("\""))) {
            newSSID = "\"" + ssid + "\"";
        } else {
            newSSID = ssid;
        }

        WifiConfiguration configuration = getExistWifiConfig(context, newSSID);
        configuration.allowedAuthAlgorithms.clear();
        configuration.allowedGroupCiphers.clear();
        configuration.allowedKeyManagement.clear();
        configuration.allowedPairwiseCiphers.clear();
        configuration.allowedProtocols.clear();

        if (configuration != null) {

            wm.removeNetwork(configuration.networkId);
            wm.saveConfiguration();
        }
    }

    /**
     * （Android API小于30有效）
     *
     * @param ssid
     * @return
     */
    @SuppressLint("MissingPermission")
    public static WifiConfiguration getExistWifiConfig(Context context, String ssid) {
        if (TextUtils.isEmpty(ssid)) {
            return null;
        }
        String newSSID;
        if (!(ssid.startsWith("\"") && ssid.endsWith("\""))) {
            newSSID = "\"" + ssid + "\"";
        } else {
            newSSID = ssid;
        }
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configuredNetworks = wm.getConfiguredNetworks();

        for (WifiConfiguration configuration : configuredNetworks) {
            if (newSSID.equalsIgnoreCase(configuration.SSID)) {
                return configuration;
            }
        }

        return null;
    }

    /**
     * Wifi加锁方式
     *
     * @param capabilities
     * @return
     */
    public static int getWifiLockType(String capabilities) {
        if (TextUtils.isEmpty(capabilities)) {
            return -1;
        }
        int encryptType;

        if (capabilities.contains("WPA") && capabilities.contains("WPA2")) {
            encryptType = 0;
        } else if (capabilities.contains("WPA2")) {
            encryptType = 1;
        } else if (capabilities.contains("WPA")) {
            encryptType = 2;
        } else if (capabilities.contains("WEP")) {
            encryptType = 3;
        } else {
            encryptType = 4;
        }

        return encryptType;
    }

    /**
     * 获取WIFI列表
     * <p>需要权限{@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
     * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>}</p>
     * <p>注意Android6.0上需要主动申请定位权限，并且打开定位开关</p>
     *
     * @param context 上下文
     * @return wifi列表
     */
    public static List<ScanResult> getWifiList(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> scanResults = wm.getScanResults();
        WifiInfo info = wm.getConnectionInfo();

        List<ScanResult> list = new ArrayList<>();
        for (int i = 0; i < scanResults.size(); i++) {
            if (scanResults.get(i).SSID.isEmpty()) {
                continue;
            }
            if (info != null && info.getSSID() != null && scanResults.get(i).BSSID.equals(info.getBSSID())) {
                // 当前已连接设备不显示在列表中
                continue;
            }

            if (scanResults.get(i).SSID.contains("0460")) {
                continue;
            }

            // 该热点SSID是否已在列表中
            int position = getItemPosition(list, scanResults.get(i));
            if (position != -1) {
                // 已在列表
                // 相同SSID热点，取信号强的
                if (list.get(position).level < scanResults.get(i).level) {
                    list.remove(position);
                    list.add(position, scanResults.get(i));
                }
            } else {
                list.add(scanResults.get(i));
            }
        }

        Collections.sort(list, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult scanResult1, ScanResult scanResult2) {
                return scanResult2.level - scanResult1.level;
            }
        });
        return list;
    }

    /**
     * 返回item在list中的坐标
     */
    private static int getItemPosition(List<ScanResult> list, ScanResult item) {
        for (int i = 0; i < list.size(); i++) {
            if (item.SSID.equals(list.get(i).SSID)) {
                return i;
            }
        }
        return -1;
    }

    public static String getWifiEncryptTypeStr(String capabilities) {
        if (TextUtils.isEmpty(capabilities)) {
            return null;
        }

        String encryptType;

        if (capabilities.contains("WPA") && capabilities.contains("WPA2")) {
            encryptType = "WPA/WPA2 PSK";
        } else if (capabilities.contains("WPA2")) {
            encryptType = "WPA2 PSK";
        } else if (capabilities.contains("WPA")) {
            encryptType = "WPA PSK";
        } else if (capabilities.contains("WEP")) {
            encryptType = "WEP";
        } else {
            encryptType = "NONE";
        }

        return encryptType;
    }

    /**
     * wifi加密方式有5种
     * 0 - WPA/WPA2 PSK
     * 1 - WPA2 PSK
     * 2 - WPA PSK
     * 3 - WEP
     * 4 - NONE
     *
     * @param capabilities
     * @return
     */
    public static int getWifiEncryptType(String capabilities) {
        if (TextUtils.isEmpty(capabilities)) {
            return -1;
        }

        int encryptType;

        if (capabilities.contains("WPA") && capabilities.contains("WPA2")) {
            encryptType = 0;
        } else if (capabilities.contains("WPA2")) {
            encryptType = 1;
        } else if (capabilities.contains("WPA")) {
            encryptType = 2;
        } else if (capabilities.contains("WEP")) {
            encryptType = 3;
        } else {
            encryptType = 4;
        }

        return encryptType;
    }

    /**
     * 构建wifi连接配置（Android API小于30有效）
     */
    public static WifiConfiguration buildWifiConfig(Context context, String ssid,
                                                    String password, int encryptType) {
        WifiManager wm = (WifiManager) context
                .getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";

        WifiConfiguration configuration = getExistWifiConfig(context, ssid);

        if (configuration != null) {
            wm.removeNetwork(configuration.networkId);
        }
        switch (encryptType) {
            case 4:
                // 不加密
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
            case 3:
                //wep 加密
                config.hiddenSSID = true;
                config.wepKeys[0] = "\"" + password + "\"";
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
            case 0:
                // wpa/wap2加密
            case 1:
                // wpa2加密
            case 2:
                // wpa加密
                config.preSharedKey = "\"" + password + "\"";
                config.hiddenSSID = true;
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                config.status = WifiConfiguration.Status.ENABLED;
                break;
            default:
                break;
        }
        return config;
    }

    /**
     * （Android API小于30有效）
     *
     * @param context
     * @param ssid
     * @des 连接已经保存过配置的wifi
     */
    public static boolean connectExistWifi(Context context, String ssid) {
        if (!getWifiEnabled(context)) {
            return false;
        }

        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"" + ssid + "\"";
        WifiConfiguration configuration = getExistWifiConfig(context, ssid);
        if (configuration != null) {
            wm.disconnect();
            return wm.enableNetwork(configuration.networkId, true);
        }
        return false;
    }

    /**
     * （Android API小于30有效）
     *
     * @param context
     * @param password
     * @param result
     * @return
     */
    public static boolean connectWifi(Context context, String password, ScanResult result) {
        if (!getWifiEnabled(context) || result == null || TextUtils.isEmpty(result.SSID)) {
            return false;
        }
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wc = buildWifiConfig(context, result.SSID, password,
                getWifiEncryptType(result.capabilities));
        int network = wm.addNetwork(wc);
        if (network == -1) {
            return connectExistWifi(context, result.SSID);
        } else {
            wm.disconnect();
            return wm.enableNetwork(network, true);
        }
    }

    /**
     * （Android API小于30有效）
     *
     * @param context
     * @param ssid
     * @param password
     * @param encryptType
     * @des 连接没有配置过的wifi
     */
    public static boolean connectWifi(Context context, String ssid, String password, int encryptType) {
        if (!getWifiEnabled(context)) {
            return false;
        }
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wc = buildWifiConfig(context, ssid, password, encryptType);
        int network = wm.addNetwork(wc);
        if (network == -1) {
            return connectExistWifi(context, ssid);
        } else {
            wm.disconnect();
            return wm.enableNetwork(network, true);
        }
    }

    public static DhcpInfo getWifiRouteDhcpInfo(Context context) {
        if (!getWifiEnabled(context)) {
            return null;
        }
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wm.getDhcpInfo();
        LogUtils.d(TAG, "wifi route dhcpInfo:" + dhcpInfo);
        return dhcpInfo;
    }

    /**
     * 获取wifi连接的路由的ip地址
     *
     * @param
     * @return
     */
    public static String getWifiRouteIP(Context context) {
        DhcpInfo dhcpInfo = getWifiRouteDhcpInfo(context);
        if (dhcpInfo == null) {
            return "";
        }
        String ipAddress = Formatter.formatIpAddress(dhcpInfo.ipAddress);
        return ipAddress;
    }

    /**
     * 获取wifi连接的路由的网关地址
     *
     * @param
     * @return
     */
    public static String getWifiRouteGatewayIP(Context context) {
        DhcpInfo dhcpInfo = getWifiRouteDhcpInfo(context);
        if (dhcpInfo == null) {
            return "";
        }
        String ipAddress = Formatter.formatIpAddress(dhcpInfo.gateway);
        return ipAddress;
    }

    /**
     * 获取wifi连接的路由的ip地址
     *
     * @param
     * @return
     */
    public static String getWifiRouteServerIP(Context context) {
        DhcpInfo dhcpInfo = getWifiRouteDhcpInfo(context);
        if (dhcpInfo == null) {
            return "";
        }
        String ipAddress = Formatter.formatIpAddress(dhcpInfo.serverAddress);
        return ipAddress;
    }
}
