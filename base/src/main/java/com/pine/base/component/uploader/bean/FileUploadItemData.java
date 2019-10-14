package com.pine.base.component.uploader.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tanghongfeng on 2019/9/1
 */

public class FileUploadItemData implements Parcelable {
    private int index;
    private String localFilePath;
    private String remoteFilePath;
    private int uploadProgress;
    private FileUploadState uploadState = FileUploadState.UPLOAD_STATE_DEFAULT;
    private String responseData;

    public FileUploadItemData() {

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public String getRemoteFilePath() {
        return remoteFilePath;
    }

    public void setRemoteFilePath(String remoteFilePath) {
        this.remoteFilePath = remoteFilePath;
    }

    public int getUploadProgress() {
        return uploadProgress;
    }

    public void setUploadProgress(int uploadProgress) {
        this.uploadProgress = uploadProgress;
    }

    public FileUploadState getUploadState() {
        return uploadState;
    }

    public void setUploadState(FileUploadState uploadState) {
        this.uploadState = uploadState;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    protected FileUploadItemData(Parcel in) {
        index = in.readInt();
        localFilePath = in.readString();
        remoteFilePath = in.readString();
        uploadProgress = in.readInt();
        uploadState = in.readParcelable(FileUploadState.class.getClassLoader());
        responseData = in.readString();
    }

    public static final Creator<FileUploadItemData> CREATOR = new Creator<FileUploadItemData>() {
        @Override
        public FileUploadItemData createFromParcel(Parcel in) {
            return new FileUploadItemData(in);
        }

        @Override
        public FileUploadItemData[] newArray(int size) {
            return new FileUploadItemData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
        dest.writeString(localFilePath);
        dest.writeString(remoteFilePath);
        dest.writeInt(uploadProgress);
        dest.writeParcelable(uploadState, flags);
        dest.writeString(responseData);
    }
}
