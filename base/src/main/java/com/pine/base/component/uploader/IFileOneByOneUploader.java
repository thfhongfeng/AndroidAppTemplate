package com.pine.base.component.uploader;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.pine.base.component.uploader.bean.FileUploadBean;

import java.util.List;

/**
 * Created by tanghongfeng on 2019/9/19.
 */

public interface IFileOneByOneUploader extends IFileUploaderConfig {
    void init(@NonNull Activity activity,
              @NonNull FileUploadComponent.OneByOneUploadAdapter adapter, int requestCodeSelectFile);

    int getUploadFileType();

    int getValidFileCount();

    void onFileUploadPrepare(List<FileUploadBean> uploadBeanList);

    void onFileUploadProgress(FileUploadBean uploadBean);

    void onFileUploadCancel(FileUploadBean uploadBean);

    void onFileUploadFail(FileUploadBean uploadBean, Exception exception);

    void onFileUploadSuccess(FileUploadBean uploadBean);
}
