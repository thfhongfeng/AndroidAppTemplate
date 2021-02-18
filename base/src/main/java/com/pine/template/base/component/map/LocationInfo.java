package com.pine.template.base.component.map;

/**
 * Created by tanghongfeng on 2018/10/31
 */

public class LocationInfo {
    // 定位类型
    private float locationType;
    // 定位精度
    private float radius;
    // 方向
    private float direction;
    // Gcj_02坐标系
    private double latitude;
    // Gcj_02坐标系
    private double longitude;

    public float getLocationType() {
        return locationType;
    }

    public void setLocationType(float locationType) {
        this.locationType = locationType;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
