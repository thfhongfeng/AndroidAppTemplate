package com.pine.template.base.business.track.entity;

public class AppTracksHeader {
    // 应用包名
    private String pkgName;

    // 设备号
    private String deviceId;

    // 设备主板类型
    private String deviceModel;

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    @Override
    public String toString() {
        return "AppTracksHeader{" +
                "pkgName='" + pkgName + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                '}';
    }
}
