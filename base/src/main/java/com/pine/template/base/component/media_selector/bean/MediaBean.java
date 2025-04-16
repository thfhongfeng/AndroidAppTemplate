package com.pine.template.base.component.media_selector.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class MediaBean implements Parcelable {
    public static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_VIDEO = 1;

    private int mediaType;
    private String storeId;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    protected MediaBean(Parcel in) {
        mediaType = in.readInt();
        storeId = in.readString();
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
        dest.writeString(url);
        dest.writeString(remark);
    }
}
