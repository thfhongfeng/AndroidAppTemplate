package com.pine.template.base.component.uploader.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tanghongfeng on 2018/11/14
 */

public enum FileUploadState implements Parcelable {
    UPLOAD_STATE_DEFAULT(0),
    UPLOAD_STATE_PREPARING(1),
    UPLOAD_STATE_IMAGE_COMPRESS(2),
    UPLOAD_STATE_START(3),
    UPLOAD_STATE_UPLOADING(4),
    UPLOAD_STATE_CANCEL(5),
    UPLOAD_STATE_FAIL(6),
    UPLOAD_STATE_SUCCESS(7);

    private int mValue;

    FileUploadState(int value) {
        this.mValue = value;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mValue);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileUploadState> CREATOR = new Creator<FileUploadState>() {
        @Override
        public FileUploadState createFromParcel(Parcel in) {
            return FileUploadState.values()[in.readInt()];
        }

        @Override
        public FileUploadState[] newArray(int size) {
            return new FileUploadState[size];
        }
    };
}

