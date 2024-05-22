package com.pine.template.base.device_sdk.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class AutoPowerPlan implements Parcelable {
    // 是否开启
    public String enable = "true";
    // 计划名称
    public String name;
    // 开机关机类型：off-关机；on-开机
    public String type;
    // 间隔类型。0-一次；1-每天；2-每周；3-间隔分钟
    public int intervalType;
    /**
     * 数组元素为开启时间点，格式：HH:mm:ss
     * (元素为多时间段则按从小到大顺序用分号间隔，如：HH:mm:ss;HH:mm:ss...）。
     * intervalType==1，长度为1的数组，每天开启的时间点
     * intervalType==2，为周对应的长度为7的数组
     * 从周日开始，数组元素值为null-对应的周元素不开启，正常时间点-对应的周元素开启。
     * intervalType==3，长度为1的数组，间隔时间（分钟），如：3
     */
    public String[] intervalFlags;
    // 第一次开启的时间，格式：yyyy-MM-dd HH:mm:ss
    public String firstPendingDateTime;

    public AutoPowerPlan(int intervalType) {
        this.intervalType = intervalType;
    }

    public AutoPowerPlan(int intervalType, String[] intervalFlags,
                         String firstPendingDateTime) {
        this.intervalType = intervalType;
        this.intervalFlags = intervalFlags;
        this.firstPendingDateTime = firstPendingDateTime;
    }

    @Override
    public String toString() {
        String intervalFlagStr = "";
        if (intervalFlags != null) {
            intervalFlagStr = "['";
            for (String item : intervalFlags) {
                intervalFlagStr += item + "','";
            }
            if (intervalFlags.length > 0) {
                intervalFlagStr = intervalFlagStr.substring(0, intervalFlagStr.length() - 3);
            }
            intervalFlagStr += "']";
        }
        return "AutoPowerPlan{" +
                "enable='" + enable + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", intervalType=" + intervalType +
                ", intervalFlags=" + intervalFlagStr +
                ", firstPendingDateTime='" + firstPendingDateTime + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.enable);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeInt(this.intervalType);
        dest.writeStringArray(this.intervalFlags);
        dest.writeString(this.firstPendingDateTime);
    }

    protected AutoPowerPlan(Parcel in) {
        this.enable = in.readString();
        this.name = in.readString();
        this.type = in.readString();
        this.intervalType = in.readInt();
        this.intervalFlags = in.createStringArray();
        this.firstPendingDateTime = in.readString();
    }

    public static final Creator<AutoPowerPlan> CREATOR = new Creator<AutoPowerPlan>() {
        @Override
        public AutoPowerPlan createFromParcel(Parcel source) {
            return new AutoPowerPlan(source);
        }

        @Override
        public AutoPowerPlan[] newArray(int size) {
            return new AutoPowerPlan[size];
        }
    };
}