package com.pine.base.component.uploader;

import android.app.Activity;

import com.pine.base.component.uploader.bean.FileUploadBean;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by tanghongfeng on 2019/9/19.
 */

public interface IFileTogetherUploader extends IFileUploaderConfig {
    void init(@NonNull Activity activity,
              @NonNull FileUploadComponent.TogetherUploadAdapter adapter, int requestCodeSelectFile);

    void onFileUploadPrepare(List<FileUploadBean> uploadBeanList);

    void onFileUploadCancel(FileUploadBean uploadBean);

    // 如果文件是图片，上传前的图片压缩进度回调
    void onImageCompressProgress(List<FileUploadBean> uploadBeanList);

    void onFileUploadStart(List<FileUploadBean> uploadBeanList);

    void onFileUploadProgress(List<FileUploadBean> uploadBeanList);

    void onFileUploadFail(List<FileUploadBean> uploadBeanList, Exception exception);

    void onFileUploadSuccess(List<FileUploadBean> uploadBeanList);
}
