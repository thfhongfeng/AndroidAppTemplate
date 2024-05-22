package com.pine.template.base.device_sdk.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class WatchDogAction implements Parcelable {
    // 应用前台运行检查
    public static final int TOP_APP_ALWAYS = 0;
    // 应用运行检查
    public static final int RUNNING_APP_ALWAYS = 1;

    // 监听目标为应用
    public static final int TARGET_APP = 0;
    // 监听目标为服务
    public static final int TARGET_SERVICE = 1;

    // 看门狗类型。0-应用前台运行检查；1-应用运行检查
    public int type;
    // 应用名
    @NonNull
    public String appName;
    // 应用包名
    @NonNull
    public String packageName;
    public String extraData;
    // 上次检查时间（自动生成，不需要设置）
    public long lastCheckTime = 0;
    // 是否由系统启动看门狗监视的app（0-由app执行相关动作;1-系统启动）
    public int handleBySys = 0;

    // 自处理的接收者广播类
    public String receiver;
    // 监听的对象的类别；0-应用；1-服务
    public int targetType;
    // 监听的对象的名称 （如果为空则会以packageName为判断依据）
    // 1. 应用是进程名
    // 2. 服务是对应服务类名（全限定名）
    public String targetClass;

    public WatchDogAction(int type, @NonNull String appName,
                          @NonNull String packageName) {
        this.type = type;
        this.appName = appName;
        this.packageName = packageName;
    }

    protected WatchDogAction(Parcel in) {
        this.type = in.readInt();
        this.appName = in.readString();
        this.packageName = in.readString();
        this.extraData = in.readString();
        this.lastCheckTime = in.readLong();
        this.handleBySys = in.readInt();
        this.receiver = in.readString();
        this.targetType = in.readInt();
        this.targetClass = in.readString();
    }

    public static final Creator<WatchDogAction> CREATOR = new Creator<WatchDogAction>() {
        @Override
        public WatchDogAction createFromParcel(Parcel in) {
            return new WatchDogAction(in);
        }

        @Override
        public WatchDogAction[] newArray(int size) {
            return new WatchDogAction[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.appName);
        dest.writeString(this.packageName);
        dest.writeString(this.extraData);
        dest.writeLong(this.lastCheckTime);
        dest.writeInt(this.handleBySys);
        dest.writeString(this.receiver);
        dest.writeInt(this.targetType);
        dest.writeString(this.targetClass);
    }

    @Override
    public String toString() {
        return "WatchDogAction{" +
                "type=" + type +
                ", appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", extraData='" + extraData + '\'' +
                ", lastCheckTime=" + lastCheckTime +
                ", handleBySys=" + handleBySys +
                ", receiver='" + receiver + '\'' +
                ", targetType=" + targetType +
                ", targetClass='" + targetClass + '\'' +
                '}';
    }

    public String toSimpleString() {
        return "{" + type + "_" + packageName + "}";
    }
}
