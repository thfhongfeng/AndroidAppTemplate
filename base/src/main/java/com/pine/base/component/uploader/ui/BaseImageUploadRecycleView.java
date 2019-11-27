package com.pine.base.component.uploader.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.pine.base.R;
import com.pine.base.component.uploader.FileUploadComponent;
import com.pine.tool.util.LogUtils;

import java.util.List;

/**
 * Created by tanghongfeng on 2018/11/1
 */

public class BaseImageUploadRecycleView extends BaseFileUploadRecycleView {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    public BaseImageUploadRecycleView(Context context) {
        super(context);
    }

    public BaseImageUploadRecycleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseImageUploadRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseImageUploadRecycleView);
        boolean enableImageScale = typedArray.getBoolean(
                R.styleable.BaseImageUploadRecycleView_base_enableImageScale, false);
        boolean enableImageTranslate = typedArray.getBoolean(
                R.styleable.BaseImageUploadRecycleView_base_enableImageTranslate, false);
        boolean enableImageRotate = typedArray.getBoolean(
                R.styleable.BaseImageUploadRecycleView_base_enableImageRotate, false);
        mHelper.enableImageScale(enableImageScale);
        mHelper.enableImageTranslate(enableImageTranslate);
        mHelper.enableImageRotate(enableImageRotate);
    }

    public void enableImageScale(boolean enable) {
        mHelper.enableImageScale(enable);
    }

    public void enableImageTranslate(boolean enable) {
        mHelper.enableImageTranslate(enable);
    }

    public void enableImageRotate(boolean enable) {
        mHelper.enableImageRotate(enable);
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
