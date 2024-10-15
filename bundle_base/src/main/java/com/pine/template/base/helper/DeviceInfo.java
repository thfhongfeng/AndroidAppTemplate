package com.pine.template.base.helper;

public class DeviceInfo {
    // 应用渠道名
    private String sdkType;
    // 应用包名
    private String apkPkgName;
    // 应用版本号
    private String apkVersionName;
    // 应用版本序号
    private String apkVersionCode;

    // 设备系统版本
    private String systemVersion;
    // 设备系统版本详细信息
    private String systemDisplayId;
    // 设备系统客制代号
    private String deviceModel;
    // 设备ID
    private String deviceId;
    // 国际化标识
    private String local;
    // 设备客户标识
    private String productCustomer;

    public String getSdkType() {
        return sdkType;
    }

    public void setSdkType(String sdkType) {
        this.sdkType = sdkType;
    }

    public String getApkPkgName() {
        return apkPkgName;
    }

    public void setApkPkgName(String apkPkgName) {
        this.apkPkgName = apkPkgName;
    }

    public String getApkVersionName() {
        return apkVersionName;
    }

    public void setApkVersionName(String apkVersionName) {
        this.apkVersionName = apkVersionName;
    }

    public String getApkVersionCode() {
        return apkVersionCode;
    }

    public void setApkVersionCode(String apkVersionCode) {
        this.apkVersionCode = apkVersionCode;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public String getSystemDisplayId() {
        return systemDisplayId;
    }

    public void setSystemDisplayId(String systemDisplayId) {
        this.systemDisplayId = systemDisplayId;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getProductCustomer() {
        return productCustomer;
    }

    public void setProductCustomer(String productCustomer) {
        this.productCustomer = productCustomer;
    }

    public DeviceInfo copy() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setSdkType(getSdkType());
        deviceInfo.setApkPkgName(getApkPkgName());
        deviceInfo.setApkVersionCode(getApkVersionCode());
        deviceInfo.setApkVersionName(getApkVersionName());

        deviceInfo.setDeviceId(getDeviceId());
        deviceInfo.setDeviceModel(getDeviceModel());
        deviceInfo.setSystemVersion(getSystemVersion());
        deviceInfo.setSystemDisplayId(getSystemDisplayId());
        deviceInfo.setLocal(getLocal());
        deviceInfo.setProductCustomer(getProductCustomer());
        return deviceInfo;
    }
}
