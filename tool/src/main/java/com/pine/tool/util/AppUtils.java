package com.pine.tool.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by tanghongfeng on 2018/9/5.
 */

public class AppUtils {
    private static final String TAG = "AppUtils";

    private static Application mApplication;

    // Cur Application version
    private static String mVersionName = "";
    private static int mVersionCode = -1;

    // Firmware version
    private static String mFwVersion = "";

    private static String mHwRevision = "";

    private static String mDeviceModel = "";

    // MAC
    private static String mEth0Mac = "";
    private static String mWifiMac = "";
    private static String mMac = "";
    private static String mMacNoColon = "";

    // Screen
    private static int mScreenWidth = -1;
    private static int mScreenHeight = -1;

    // Device id
    private static String sDeviceId = "";
    private static String sDeviceNumId = "";

    private AppUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static String getScreenOrientation() {
        String result = "land";
        try {
            Configuration mConfiguration = getApplicationContext().getResources().getConfiguration(); //获取设置的配置信息
            int ori = mConfiguration.orientation; //获取屏幕方向
            result = ori == mConfiguration.ORIENTATION_LANDSCAPE ? "land" : "port";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isLandScreen() {
        boolean result = true;
        try {
            Configuration mConfiguration = getApplicationContext().getResources().getConfiguration(); //获取设置的配置信息
            int ori = mConfiguration.orientation; //获取屏幕方向
            result = ori == mConfiguration.ORIENTATION_LANDSCAPE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isPortScreen() {
        boolean result = true;
        try {
            Configuration mConfiguration = getApplicationContext().getResources().getConfiguration(); //获取设置的配置信息
            int ori = mConfiguration.orientation; //获取屏幕方向
            result = ori == mConfiguration.ORIENTATION_PORTRAIT;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String SHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
            }
            return hexString.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 检测当前应用是否是Debug版本
     * 在AndroidManifest.xml中最好不设置android:debuggable属性置，而是由打包方式来决定其值.
     *
     * @param context
     * @return
     */
    public static boolean isApkDebuggable(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Application getApplication() {
        if (mApplication != null) {
            return mApplication;
        }
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app == null) {
                throw new NullPointerException("you should init first");
            }
            mApplication = (Application) app;
            return mApplication;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("u should init first");
    }

    public static Context getApplicationContext() {
        return getApplication().getApplicationContext();
    }

    /**
     * 检测服务是否运行
     *
     * @param context   上下文
     * @param className 类名
     * @return 是否运行的状态
     */
    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager
                = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> servicesList
                = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo si : servicesList) {
            if (className.equals(si.service.getClassName())) {
                isRunning = true;
            }
        }
        return isRunning;
    }

    /**
     * 停止运行服务
     *
     * @param context   上下文
     * @param className 类名
     * @return 是否执行成功
     */
    public static boolean stopRunningService(Context context, String className) {
        Intent intent_service = null;
        boolean ret = false;
        try {
            intent_service = new Intent(context, Class.forName(className));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (intent_service != null) {
            ret = context.stopService(intent_service);
        }
        return ret;
    }

    /**
     * 获取当前进程名
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : runningApps) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return null;
    }

    /**
     * 指定进程名的进程是否存在
     *
     * @param context     上下文
     * @param processName 进程名
     * @return 是否含有当前的进程
     */
    public static boolean isNamedProcessExist(Context context, String processName) {
        if (context == null || TextUtils.isEmpty(processName)) {
            return false;
        }

        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfoList
                = manager.getRunningAppProcesses();
        if (processInfoList == null) {
            return true;
        }

        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid &&
                    processName.equalsIgnoreCase(processInfo.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 应用是否在后台运行
     * <ul>
     * <li>need use permission android.permission.GET_TASKS in Manifest.xml</li>
     * </ul>
     *
     * @param context 上下文
     * @return if application is in background return true, otherwise return
     * false
     */
    public static boolean isApplicationInBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
        if (taskList != null && !taskList.isEmpty()) {
            ComponentName topActivity = taskList.get(0).topActivity;
            if (topActivity != null && !topActivity.getPackageName()
                    .equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取设备的可用内存大小
     *
     * @param context 应用上下文对象context
     * @return 当前内存大小
     */
    public static int getDeviceUsableMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        // 返回当前系统的可用内存
        return (int) (mi.availMem / (1024 * 1024));
    }

    /**
     * 获取手机系统SDK版本
     *
     * @return 如API 17 则返回 17
     */
    public static int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0   支持4.1.2,4.1.23.4.1.rc111这种形式
     *
     * @param version1
     * @param version2
     * @return
     */
    public static int compareVersion(String version1, String version2) throws Exception {
        if (version1 == null || version2 == null) {
            throw new Exception("compareVersion error:illegal params.");
        }
        String[] versionArray1 = version1.split("\\.");//注意此处为正则匹配，不能用"."；
        String[] versionArray2 = version2.split("\\.");
        int idx = 0;
        int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
        int diff = 0;
        while (idx < minLength
                && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//先比较长度
                && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {//再比较字符
            ++idx;
        }
        //如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
        diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
        return diff;
    }

    /**
     * 获取应用运行的最大内存
     *
     * @return 最大内存
     */
    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory() / 1024;
    }

    /**
     * 得到App名称
     *
     * @return App名称
     */
    public static String getAppName() {
        return getApplication().getApplicationInfo().loadLabel(getApplication().getPackageManager()).toString();
    }

    public static int getVersionCode() {
        return getVersionCode(getApplicationContext());
    }


    public static String getVersionName() {
        return getVersionName(getApplicationContext());
    }

    /**
     * Get application version name
     *
     * @param context Context
     * @return version name
     */
    public static String getVersionName(Context context) {
        if (TextUtils.isEmpty(mVersionName)) {
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), 0);
                mVersionName = info.versionName;
                mVersionCode = info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return mVersionName;
    }

    /**
     * Get application version code
     *
     * @param context Context
     * @return version code
     */
    public static int getVersionCode(Context context) {
        if (-1 == mVersionCode) {
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(
                        context.getPackageName(), 0);
                mVersionName = info.versionName;
                mVersionCode = info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return mVersionCode;
    }

    public static String getVersionName(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    packageName, 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }

    public static int getVersionCode(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    packageName, 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return -1;
    }

    public static ApkInfo getApkInfo(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = null;
        ApkInfo apkInfo = null;
        // 该方法会导致文件句柄不被释放，
        // 特别是读取u盘文件时，拔出u盘会导致 "W/vold: Sending Interrupt to xxx",造成程序崩溃
        pkgInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (pkgInfo != null) {
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
            String appName = pm.getApplicationLabel(appInfo).toString();
            String packageName = appInfo.packageName; // 得到包名
            String versionName = pkgInfo.versionName; // 得到版本信息
            int versionCode = pkgInfo.versionCode;
            apkInfo = new ApkInfo(appName, packageName, versionName, versionCode);
            String pkgInfoStr = String.format("appName:%s, PackageName:%s, versionName: %s, versionCode: %s, AppName: %s",
                    appName, packageName, versionName, versionCode, appName);
            LogUtils.i(TAG, String.format("PkgInfo: %s", pkgInfoStr));
        }
        return apkInfo;
    }

    /**
     * Get firmware version
     *
     * @return version
     */
    public static String getFwVersion() {
        if (TextUtils.isEmpty(mFwVersion)) {
            mFwVersion = getProperty("ro.vendor.product_version_number", "1.0.0.000");
        }
        return mFwVersion;
    }

    public static String getHwRevision() {
        if (TextUtils.isEmpty(mHwRevision)) {
            mHwRevision = getProperty("ro.boot.hardware.revision", "");
        }
        return mHwRevision;
    }

    public static String getDeviceModel() {
        if (TextUtils.isEmpty(mDeviceModel)) {
            mDeviceModel = getProperty("ro.product.model", "");
        }
        return mDeviceModel;
    }

    /**
     * Get serial number
     *
     * @return serial number
     */
    @SuppressLint("HardwareIds")
    public static String getSerialNo() {
        String sn = Build.SERIAL;
        if (TextUtils.isEmpty(sn)) {
            sn = getProperty("ro.serialno", "");
            if (TextUtils.isEmpty(sn)) {
                sn = getProperty("ro.boot.serialno", "");
                if (TextUtils.isEmpty(sn)) {
                    sn = getCPUSerial();
                }
            }
        }

        return sn;
    }

    /**
     * Get cpu serial
     *
     * @return success: cpu serial, failed: "0000000000000000"
     */
    public static String getCPUSerial() {
        String cpuAddress = "0000000000000000";

        try {
            Process process = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader is = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(is);

            String str;
            while ((str = input.readLine()) != null) {
                if (!TextUtils.isEmpty(str)) {
                    if (str.contains("Serial")) {
                        String cpuStr = str.substring(str.indexOf(":") + 1);
                        cpuAddress = cpuStr.trim();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            LogUtils.e(TAG, "getCPUSerial, " + e.getMessage());
        }

        return cpuAddress;
    }

    /**
     * Get device id
     *
     * @return device id
     */
    public static String getDeviceId(Context context) {
        if (TextUtils.isEmpty(sDeviceId)) {
            sDeviceId = getMac(context);
        }
        return sDeviceId;
    }

    public static String getDeviceNumId(Context context) {
        if (TextUtils.isEmpty(sDeviceNumId)) {
            String deviceId = getDeviceId(context);
            if (!TextUtils.isEmpty(deviceId)) {
                sDeviceNumId = getDeviceId(context).replaceAll(":", "");
            }
        }
        return sDeviceNumId;
    }

    /**
     * Get current country
     *
     * @return country
     */
    public static String getCountry() {
        return Locale.getDefault().getCountry();
    }

    /**
     * Get current language
     *
     * @return language
     */
    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * Whether the network is connected
     *
     * @param context Context
     * @return true: connected, false: disconnected
     */
    @SuppressLint("MissingPermission")
    public static boolean isConnNetWork(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return ((networkInfo != null) && networkInfo.isConnected());
    }

    /**
     * Whether WiFi is connected
     *
     * @param context Context
     * @return true: connected, false: disconnected
     */
    @SuppressLint("MissingPermission")
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return ((wifiNetworkInfo != null) && wifiNetworkInfo.isConnected());
    }

    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getDeviceId();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Get Ethernet MAC
     *
     * @param context Context
     * @return Mac
     */
    public static String getEth0Mac(Context context) {
        if (TextUtils.isEmpty(mEth0Mac)) {
            try {
                int numRead = 0;
                char[] buf = new char[1024];
                StringBuffer strBuf = new StringBuffer(1000);
                BufferedReader reader = new BufferedReader(new FileReader(
                        "/sys/class/net/eth0/address"));
                while ((numRead = reader.read(buf)) != -1) {
                    String readData = String.valueOf(buf, 0, numRead);
                    strBuf.append(readData);
                }
                mEth0Mac = strBuf.toString().replaceAll("\r|\n", "");
                reader.close();
            } catch (IOException ex) {
                LogUtils.w(TAG, "eth0 mac not exist");
            }
        }
        return mEth0Mac;
    }

    /**
     * Get WiFi MAC
     *
     * @param context Context
     * @return Mac
     */
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public static String getWifiMac(Context context) {
        if (TextUtils.isEmpty(mWifiMac)) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            mWifiMac = wifiInfo.getMacAddress();
        }
        return mWifiMac;
    }

    /**
     * Get MAC, get the Ethernet MAC first, then get the WiFi MAC if it is empty.
     *
     * @param context Context
     * @return Mac
     */
    public static String getMac(Context context) {
        if (TextUtils.isEmpty(mMac)) {
            mMac = getEth0Mac(context);
            if (TextUtils.isEmpty(mMac)) {
                mMac = getWifiMac(context);
            }
        }
        return mMac;
    }

    /**
     * Get the MAC with the colon removed
     *
     * @param context Context
     * @return Mac
     */
    public static String getMacNoColon(Context context) {
        if (TextUtils.isEmpty(mMacNoColon)) {
            String mac = getMac(context);
            if (!TextUtils.isEmpty(mac)) {
                mMacNoColon = mac.replace(":", "");
            }
        }
        return mMacNoColon;
    }

    /**
     * Get screen width
     *
     * @param context Activity
     * @return screen width
     */
    public static int getScreenWidth(Activity context) {
        if (-1 == mScreenWidth) {
            mScreenWidth = context.getWindowManager().getDefaultDisplay().getWidth();
        }
        return mScreenWidth;
    }

    /**
     * Get screen height
     *
     * @param context Activity
     * @return screen height
     */
    public static int getScreenHeight(Activity context) {
        if (-1 == mScreenHeight) {
            mScreenHeight = context.getWindowManager().getDefaultDisplay().getHeight();
        }
        return mScreenHeight;
    }

    /**
     * Get property
     *
     * @param key          property key
     * @param defaultValue default value
     * @return property value
     */
    @SuppressLint("PrivateApi")
    public static String getProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, defaultValue));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    /**
     * Set property
     *
     * @param key   property key
     * @param value property value
     */
    @SuppressLint("PrivateApi")
    public static void setProperty(String key, String value) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("set", String.class, String.class);
            set.invoke(c, key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * start app
     *
     * @param context     Context
     * @param packageName PackageName
     */
    public static void startApp(@NonNull Context context, @NonNull String packageName) {
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(packageName);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            LogUtils.e(TAG, "startApp, Package does not exist.");
        }
    }

    public static List<ApkInfo> getAllSystemPackages(Context context) {
        return getPackages(context, 1, 0);
    }

    public static List<ApkInfo> getAllNonSystemPackages(Context context) {
        return getPackages(context, 2, 0);
    }

    public static List<ApkInfo> getAllPackages(Context context) {
        return getPackages(context, 0, 0);
    }

    /**
     * 读取包名
     *
     * @param context
     * @param type    0-所有包名；1-系统包名；2-非系统包名
     * @param flags
     * @return
     */
    public static List<ApkInfo> getPackages(Context context, int type, int flags) {
        //获取PackageManager
        PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(flags);
        //用于存储所有已安装程序的包名
        List<ApkInfo> apkInfoList = new ArrayList<>();
        if (packageInfoList != null) {
            for (int i = 0; i < packageInfoList.size(); i++) {
                PackageInfo packageInfo = packageInfoList.get(i);
                boolean isSystemPkg = (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                if (type == 0 || type == 1 && isSystemPkg || type == 2 && !isSystemPkg) {
                    ApkInfo apkInfo = new ApkInfo();
                    apkInfo.packageName = packageInfo.packageName;
                    apkInfo.versionName = packageInfo.versionName;
                    apkInfo.versionCode = packageInfo.versionCode;
                    try {
                        apkInfo.appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                    } catch (Exception e) {
                        Log.d(TAG, "getApplicationLabel fail, ignore");
                    }
                    apkInfoList.add(apkInfo);
                    Log.e(TAG, "flags:" + packageInfo.applicationInfo.flags + ",allPackage:" + apkInfo);
                }
            }
        }
        return apkInfoList;
    }

    public static class ApkInfo {
        public String appName;
        public String packageName;
        public String versionName;
        public int versionCode;

        public ApkInfo() {
        }

        public ApkInfo(String appName, String packageName, String versionName, int versionCode) {
            this.appName = appName;
            this.packageName = packageName;
            this.versionName = versionName;
            this.versionCode = versionCode;
        }

        @Override
        public String toString() {
            return "ApkInfo{" +
                    "appName='" + appName + '\'' +
                    ", packageName='" + packageName + '\'' +
                    ", versionName='" + versionName + '\'' +
                    ", versionCode=" + versionCode +
                    '}';
        }
    }
}
