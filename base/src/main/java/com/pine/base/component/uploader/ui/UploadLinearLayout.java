package com.pine.base.component.uploader.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.pine.base.component.uploader.FileUploadHelper;
import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2018/11/13
 */

public abstract class UploadLinearLayout extends LinearLayout {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    public FileUploadHelper mHelper;

    public UploadLinearLayout(Context context) {
        super(context);
        mHelper = new FileUploadHelper(this);
    }

    public UploadLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mHelper = new FileUploadHelper(this);
    }

    public UploadLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mHelper = new FileUploadHelper(this);
    }

    protected FileUploadHelper getHelper() {
        return mHelper;
    }

    protected void setMaxFileCount(int maxFileCount) {
        mHelper.setMaxFileCount(maxFileCount);
    }

    public int getMaxFileCount() {
        return mHelper.getMaxFileCount();
    }

    protected void setMaxFileSize(int maxFileSize) {
        mHelper.setMaxFileSize(maxFileSize);
    }

    public int getMaxFileSize() {
        return mHelper.getMaxFileSize();
    }

    public void setCropEnable(int cropRequestCode) {
        mHelper.setCropEnable(cropRequestCode);
    }

    public void setCropEnable(int cropRequestCode, int cropWidth, int cropHeight) {
        mHelper.setCropEnable(cropRequestCode, cropWidth, cropHeight);
    }

    @Override
    public void onDetachedFromWindow() {
        mHelper.release();
        mHelper = null;
        super.onDetachedFromWindow();
    }
}