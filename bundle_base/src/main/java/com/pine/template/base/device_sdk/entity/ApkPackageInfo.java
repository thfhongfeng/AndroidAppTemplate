package com.pine.template.base.device_sdk.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ApkPackageInfo implements Parcelable {
    public String appName;
    public String packageName;
    public int flags;
    public String versionName;
    public int versionCode;
    public int sortNum;

    public ApkPackageInfo() {
    }

    public ApkPackageInfo(String appName, String packageName, int flags,
                          String versionName, int versionCode) {
        this.appName = appName;
        this.packageName = packageName;
        this.flags = flags;
        this.versionName = versionName;
        this.versionCode = versionCode;
    }

    @Override
    public String toString() {
        return "ApkInfo{" +
                "appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", flags='" + flags + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                ", sortNum=" + sortNum +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appName);
        dest.writeString(this.packageName);
        dest.writeInt(this.flags);
        dest.writeString(this.versionName);
        dest.writeInt(this.versionCode);
        dest.writeInt(this.sortNum);
    }

    protected ApkPackageInfo(Parcel in) {
        this.appName = in.readString();
        this.packageName = in.readString();
        this.flags = in.readInt();
        this.versionName = in.readString();
        this.versionCode = in.readInt();
        this.sortNum = in.readInt();
    }

    public static final Creator<ApkPackageInfo> CREATOR = new Creator<ApkPackageInfo>() {
        @Override
        public ApkPackageInfo createFromParcel(Parcel source) {
            return new ApkPackageInfo(source);
        }

        @Override
        public ApkPackageInfo[] newArray(int size) {
            return new ApkPackageInfo[size];
        }
    };
}
