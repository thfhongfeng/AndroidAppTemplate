package com.pine.template.base.component.media_selector.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class MediaBean implements Parcelable {
    public static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;

    private int mediaType;
    private String storeId;
    // 0-url地址;1-file;2-uri
    private int resType;
    // 根据resType确定文件目录路径的结构
    private String url;
    private String remark;

    public MediaBean(String url, int mediaType) {
        this.url = url;
        this.mediaType = mediaType;
    }

    public static MediaBean buildImageBean(String url) {
        MediaBean bean = new MediaBean(url, TYPE_IMAGE);
        return bean;
    }

    public static MediaBean buildVideoBean(String url) {
        MediaBean bean = new MediaBean(url, TYPE_VIDEO);
        return bean;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getResType() {
        return resType;
    }

    public void setResType(int resType) {
        this.resType = resType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isResTypeUrl() {
        return this.resType == 0;
    }

    public void setResTypeUrl() {
        this.resType = 0;
    }

    public boolean isResTypeFile() {
        return this.resType == 1;
    }

    public void setResTypeFile() {
        this.resType = 1;
    }

    public boolean isResTypeUri() {
        return this.resType == 1;
    }

    public void setResTypeUri() {
        this.resType = 2;
    }

    protected MediaBean(Parcel in) {
        mediaType = in.readInt();
        storeId = in.readString();
        resType = in.readInt();
        url = in.readString();
        remark = in.readString();
    }

    public static final Creator<MediaBean> CREATOR = new Creator<MediaBean>() {
        @Override
        public MediaBean createFromParcel(Parcel in) {
            return new MediaBean(in);
        }

        @Override
        public MediaBean[] newArray(int size) {
            return new MediaBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mediaType);
        dest.writeString(storeId);
        dest.writeInt(resType);
        dest.writeString(url);
        dest.writeString(remark);
    }
}