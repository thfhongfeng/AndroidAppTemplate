package com.pine.app.lib.mqtt.framework;

import java.io.Serializable;

public class BaseParams implements Serializable {
    private String flagId;
    private String appKey;
    private int versionCode;
    private String versionName;

    private String deviceModel;
    private String sdkType;

    private String productCustomer;

    public String getFlagId() {
        return flagId;
    }

    public void setFlagId(String flagId) {
        this.flagId = flagId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getSdkType() {
        return sdkType;
    }

    public void setSdkType(String sdkType) {
        this.sdkType = sdkType;
    }

    public String getProductCustomer() {
        return productCustomer;
    }

    public void setProductCustomer(String productCustomer) {
        this.productCustomer = productCustomer;
    }

    @Override
    public String toString() {
        return "BaseParams{" +
                "flagId='" + flagId + '\'' +
                ", appKey='" + appKey + '\'' +
                ", versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", sdkType='" + sdkType + '\'' +
                ", productCustomer='" + productCustomer + '\'' +
                '}';
    }
}
