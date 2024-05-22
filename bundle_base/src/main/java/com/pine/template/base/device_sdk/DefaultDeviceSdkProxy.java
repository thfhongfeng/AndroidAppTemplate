package com.pine.template.base.device_sdk;

import com.pine.template.base.device_sdk.entity.ApkPackageInfo;
import com.pine.template.base.device_sdk.entity.AutoPowerPlan;
import com.pine.template.base.device_sdk.entity.WatchDogAction;

import java.util.List;

public abstract class DefaultDeviceSdkProxy implements IDeviceSdkManager {

    @Override
    public String getProperty(String key, String defaultValue) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public boolean setProperty(String key, String value) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public String getSettingsValue(int type, String key) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public boolean putSettingsValue(int type, String key, String value) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public String getAllConfigState() throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public String getProductVersionNumber() throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public String getSystemRotation() throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public void setSystemRotation(String angle, boolean reboot) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public String getDeviceDpi() throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public void setDeviceDpi(String dpi, boolean reboot) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int execCmd(String cmd, boolean root, String[] resultMsgArr) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public void powerOff(boolean confirm, String reason, boolean wait) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public void reboot(boolean confirm, String reason, boolean wait) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int openSerialPort(String path, int baudRate, int dataBits, int stopBits, char parity) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int readSerialPort(String path, int maxSize, byte[] data) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int writeSerialPort(String path, byte[] data) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int setSerialPortMode(String path, int mode) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int closeSerialPort(String path) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int openGpioPort(String port) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int closeGpioPort(String port) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int writeGpioStatus(String port, int status) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int readGpioStatus(String port) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int writeGpioDirect(String port, int direct) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int readGpioDirect(String port) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int getTetheringStatus(int type) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public void startTethering(int type, boolean showProvisioningUi, IDeviceSdkCallback callback) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public void stopTethering(int type) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int setupLcdParams(int lcdIndex, String params, boolean reboot) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public String getLcdParams(int lcdIndex) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public void customizedFullScreen(boolean neverDropDownBox, boolean neverNavigation, boolean neverStatusBar) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public void setSystemUIState(boolean enableDropDownBoxNormal, boolean showNavigation, boolean showStatusBar) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public void enableDropDownBoxNormal(boolean enable) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public void showNavigation(boolean show) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public void showStatusBar(boolean show) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public boolean sendLinuxMsg(int msgType, String msg) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public String getLinuxMsg(int msgType) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public boolean isWhiteListGrantEnable(int type) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int enableWhiteListGrant(int type, boolean enable) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public boolean checkWhiteListGrant(int type, String[] packageNames, boolean ignoreSpecial) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public List<ApkPackageInfo> getWhiteList(int type, int persistentType) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int addWhiteList(int type, List<ApkPackageInfo> packageInfoList, boolean persistent) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int removeWhiteList(int type, List<ApkPackageInfo> packageInfoList, boolean persistent) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int checkGrantPwd(int type, String pwd) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int resetGrantPwd(int type, String pwd) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int editGrantPwd(int type, String oldPwd, String newPwd) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int grantActionPermission(int type, String pwd, long effectiveDuration) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public int unGrantActionPermission(int type, String pwd) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public boolean checkAutoPowerEnable() throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public List<AutoPowerPlan> getAllAutoPowerOffPlans() throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public List<AutoPowerPlan> getAllAutoPowerOnPlans() throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public boolean autoPowerOffPlans(List<AutoPowerPlan> planList) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public boolean autoPowerOnPlans(List<AutoPowerPlan> planList) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public boolean setAutoPowerOffEnable(boolean enable) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public boolean setAutoPowerOnEnable(boolean enable) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public boolean saveAutoPowerOffPlans(List<AutoPowerPlan> planList) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public boolean saveAutoPowerOnPlans(List<AutoPowerPlan> planList) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public boolean setWatchDogIntervalTime(int type, int intervalTime) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public boolean openWatchDogActions(List<WatchDogAction> action, boolean save) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public boolean closeWatchDogActions(List<WatchDogAction> action, boolean delete) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public List<WatchDogAction> getWatchDogActions(int type, String packageName) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public boolean forbidWatchDogActions(List<WatchDogAction> action, boolean forbid) throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }

    @Override
    public List<WatchDogAction> getForbiddenWatchDogActions() throws DeviceSdkException {
        throw new DeviceSdkException("not impl method");
    }
}
