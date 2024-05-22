package com.pine.template.base.device_sdk;

import android.content.Context;

import com.pine.template.base.device_sdk.entity.ApkPackageInfo;
import com.pine.template.base.device_sdk.entity.AutoPowerPlan;
import com.pine.template.base.device_sdk.entity.WatchDogAction;

import java.util.List;

public interface IDeviceSdkManager {
    boolean init(Context context) throws DeviceSdkException;

    boolean init(Context context, String authorityKey) throws DeviceSdkException;

    String getProperty(String key, String defaultValue) throws DeviceSdkException;

    boolean setProperty(String key, String value) throws DeviceSdkException;

    String getSettingsValue(int type, String key) throws DeviceSdkException;

    boolean putSettingsValue(int type, String key, String value) throws DeviceSdkException;

    String getAllConfigState() throws DeviceSdkException;

    String getProductVersionNumber() throws DeviceSdkException;

    String getSystemRotation() throws DeviceSdkException;

    void setSystemRotation(String angle, boolean reboot) throws DeviceSdkException;

    String getDeviceDpi() throws DeviceSdkException;

    void setDeviceDpi(String dpi, boolean reboot) throws DeviceSdkException;

    int execCmd(String cmd, boolean root, String[] resultMsgArr) throws DeviceSdkException;

    void powerOff(boolean confirm, String reason, boolean wait) throws DeviceSdkException;

    void reboot(boolean confirm, String reason, boolean wait) throws DeviceSdkException;

    int openSerialPort(String path, int baudRate, int dataBits, int stopBits, char parity) throws DeviceSdkException;

    int readSerialPort(String path, int maxSize, byte[] data) throws DeviceSdkException;

    int writeSerialPort(String path, byte[] data) throws DeviceSdkException;

    int setSerialPortMode(String path, int mode) throws DeviceSdkException;

    int closeSerialPort(String path) throws DeviceSdkException;

    int openGpioPort(String port) throws DeviceSdkException;

    int closeGpioPort(String port) throws DeviceSdkException;

    int writeGpioStatus(String port, int status) throws DeviceSdkException;

    int readGpioStatus(String port) throws DeviceSdkException;

    int writeGpioDirect(String port, int direct) throws DeviceSdkException;

    int readGpioDirect(String port) throws DeviceSdkException;

    int getTetheringStatus(int type) throws DeviceSdkException;

    void startTethering(int type, boolean showProvisioningUi, IDeviceSdkCallback callback) throws DeviceSdkException;

    void stopTethering(int type) throws DeviceSdkException;

    int setupLcdParams(int lcdIndex, String params, boolean reboot) throws DeviceSdkException;

    String getLcdParams(int lcdIndex) throws DeviceSdkException;

    void customizedFullScreen(boolean neverDropDownBox, boolean neverNavigation, boolean neverStatusBar) throws DeviceSdkException;

    void setSystemUIState(boolean enableDropDownBoxNormal, boolean showNavigation, boolean showStatusBar) throws DeviceSdkException;

    void enableDropDownBoxNormal(boolean enable) throws DeviceSdkException;

    void showNavigation(boolean show) throws DeviceSdkException;

    void showStatusBar(boolean show) throws DeviceSdkException;

    boolean sendLinuxMsg(int msgType, String msg) throws DeviceSdkException;

    String getLinuxMsg(int msgType) throws DeviceSdkException;

    boolean isWhiteListGrantEnable(int type) throws DeviceSdkException;

    int enableWhiteListGrant(int type, boolean enable) throws DeviceSdkException;

    boolean checkWhiteListGrant(int type, String[] packageNames, boolean ignoreSpecial) throws DeviceSdkException;

    List<ApkPackageInfo> getWhiteList(int type, int persistentType) throws DeviceSdkException;

    int addWhiteList(int type, List<ApkPackageInfo> packageInfoList, boolean persistent) throws DeviceSdkException;

    int removeWhiteList(int type, List<ApkPackageInfo> packageInfoList, boolean persistent) throws DeviceSdkException;

    int checkGrantPwd(int type, String pwd) throws DeviceSdkException;

    int resetGrantPwd(int type, String pwd) throws DeviceSdkException;

    int editGrantPwd(int type, String oldPwd, String newPwd) throws DeviceSdkException;

    int grantActionPermission(int type, String pwd, long effectiveDuration) throws DeviceSdkException;

    int unGrantActionPermission(int type, String pwd) throws DeviceSdkException;

    boolean checkAutoPowerEnable() throws DeviceSdkException;

    List<AutoPowerPlan> getAllAutoPowerOffPlans() throws DeviceSdkException;

    List<AutoPowerPlan> getAllAutoPowerOnPlans() throws DeviceSdkException;

    boolean autoPowerOffPlans(List<AutoPowerPlan> planList) throws DeviceSdkException;

    boolean autoPowerOnPlans(List<AutoPowerPlan> planList) throws DeviceSdkException;

    boolean setAutoPowerOffEnable(boolean enable) throws DeviceSdkException;

    boolean setAutoPowerOnEnable(boolean enable) throws DeviceSdkException;

    boolean saveAutoPowerOffPlans(List<AutoPowerPlan> planList) throws DeviceSdkException;

    boolean saveAutoPowerOnPlans(List<AutoPowerPlan> planList) throws DeviceSdkException;

    boolean setWatchDogIntervalTime(int type, int intervalTime) throws DeviceSdkException;

    boolean openWatchDogActions(List<WatchDogAction> action, boolean save) throws DeviceSdkException;

    boolean closeWatchDogActions(List<WatchDogAction> action, boolean delete) throws DeviceSdkException;

    List<WatchDogAction> getWatchDogActions(int type, String packageName) throws DeviceSdkException;

    boolean forbidWatchDogActions(List<WatchDogAction> action, boolean forbid) throws DeviceSdkException;

    List<WatchDogAction> getForbiddenWatchDogActions() throws DeviceSdkException;
}
