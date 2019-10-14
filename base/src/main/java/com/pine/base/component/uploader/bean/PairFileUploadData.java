package com.pine.base.component.uploader.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class PairFileUploadData implements Parcelable {
    private int index;
    private FileUploadItemData leftData;
    private FileUploadItemData rightData;

    public PairFileUploadData() {

    }

    protected PairFileUploadData(Parcel in) {
        index = in.readInt();
        leftData = in.readParcelable(FileUploadItemData.class.getClassLoader());
        rightData = in.readParcelable(FileUploadItemData.class.getClassLoader());
    }

    public static final Creator<PairFileUploadData> CREATOR = new Creator<PairFileUploadData>() {
        @Override
        public PairFileUploadData createFromParcel(Parcel in) {
            return new PairFileUploadData(in);
        }

        @Override
        public PairFileUploadData[] newArray(int size) {
            return new PairFileUploadData[size];
        }
    };

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public FileUploadItemData getLeftData() {
        return leftData;
    }

    public void setLeftData(FileUploadItemData leftData) {
        this.leftData = leftData;
    }

    public FileUploadItemData getRightData() {
        return rightData;
    }

    public void setRightData(FileUploadItemData rightData) {
        this.rightData = rightData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
        dest.writeParcelable(leftData, flags);
        dest.writeParcelable(rightData, flags);
    }
}
