package com.pine.base.component.uploader.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.pine.base.component.uploader.FileUploadComponent;
import com.pine.tool.util.LogUtils;

import java.util.List;

/**
 * Created by tanghongfeng on 2018/11/1
 */

public class ImageUploadRecycleView extends FileUploadRecycleView {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    public ImageUploadRecycleView(Context context) {
        super(context);
    }

    public ImageUploadRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageUploadRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public int getUploadFileType() {
        return FileUploadComponent.TYPE_IMAGE;
    }

    public void setRemoteImages(String remoteImages, String joinStr) {
        super.setRemoteFiles(remoteImages, joinStr);
    }

    public void setRemoteImageList(List<String> remoteImageList) {
        super.setRemoteFileList(remoteImageList);
    }

    public void setRemoteImages(String[] remoteImageArr) {
        super.setRemoteFiles(remoteImageArr);
    }

    public List<String> getNewUploadImageRemoteList() {
        return super.getNewUploadFileRemoteList();
    }

    public String getNewUploadImageRemoteString(String joinStr) {
        return super.getNewUploadFileRemoteString(joinStr);
    }
}
