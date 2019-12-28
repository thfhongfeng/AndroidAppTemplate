package com.pine.base.component.uploader;

import android.app.Activity;

import com.pine.base.component.uploader.bean.FileUploadBean;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by tanghongfeng on 2019/9/19.
 */

public interface IFileOneByOneUploader extends IFileUploaderConfig {
    void init(@NonNull Activity activity,
              @NonNull FileUploadComponent.OneByOneUploadAdapter adapter, int requestCodeSelectFile);

    int getUploadFileType();

    int getValidFileCount();

    void onFileUploadPrepare(List<FileUploadBean> uploadBeanList);

    /**
     * 如果文件是图片，上传前的图片压缩进度回调
     *
     * @param uploadBean
     * @param compressPercentage 压缩百分比，以100为基数。
     */
    void onImageCompressProgress(FileUploadBean uploadBean, int compressPercentage);

    void onFileUploadStart(FileUploadBean uploadBean);

    void onFileUploadProgress(FileUploadBean uploadBean);

    void onFileUploadCancel(FileUploadBean uploadBean);

    void onFileUploadFail(FileUploadBean uploadBean, Exception exception);

    void onFileUploadSuccess(FileUploadBean uploadBean);
}
