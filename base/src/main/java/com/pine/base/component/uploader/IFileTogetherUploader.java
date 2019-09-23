package com.pine.base.component.uploader;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.pine.base.component.uploader.bean.FileUploadBean;

import java.util.List;

/**
 * Created by tanghongfeng on 2019/9/19.
 */

public interface IFileTogetherUploader extends IFileUploaderConfig {
    void init(@NonNull Activity activity,
              @NonNull FileUploadComponent.TogetherUploadAdapter adapter, int requestCodeSelectFile);

    void onFileUploadPrepare(List<FileUploadBean> uploadBeanList);

    void onFileUploadCancel(FileUploadBean uploadBean);

    void onFileUploadProgress(List<FileUploadBean> uploadBeanList);

    void onFileUploadFail(List<FileUploadBean> uploadBeanList);

    void onFileUploadSuccess(List<FileUploadBean> uploadBeanList);
}
