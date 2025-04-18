package com.pine.template.base.component.uploader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.pine.template.base.R;
import com.pine.template.base.component.media_selector.ImageViewer;
import com.pine.template.base.component.media_selector.MediaSelector;
import com.pine.template.base.component.media_selector.bean.MediaBean;
import com.pine.template.base.component.uploader.bean.FileUploadBean;
import com.pine.template.base.component.uploader.bean.FileUploadState;
import com.pine.template.base.component.uploader.bean.RemoteUploadFileInfo;
import com.pine.tool.exception.MessageException;
import com.pine.tool.util.FileUtils;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.PathUtils;
import com.pine.tool.util.UriUtils;
import com.pine.tool.widget.ILifeCircleView;
import com.pine.tool.widget.ILifeCircleViewContainer;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghongfeng on 2019/9/19.
 */

public class FileUploadHelper implements ILifeCircleView {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    // 每次选择文件最大允数
    private final int MAX_PER_UPLOAD_FILE_COUNT = 10;
    private Activity mActivity;
    private View mView;
    private IFileUploaderConfig mFileUploaderConfig;
    private IFileOneByOneUploader mFileOneByOneUploader;
    private IFileTogetherUploader mFileTogetherUploader;
    // 图片需要压缩时（图片大小大于允许上传大小时）的输出宽度和高度
    private int mCompressImageWidth = 1440;
    private int mCompressImageHeight = 2550;
    // 每张图片裁剪宽高
    private int mCropWidth = 360;
    private int mCropHeight = 360;
    // 最大允许上传文件数
    private int mMaxFileCount = 30;
    // 最大允许上传文件大小（单位K）
    private int mMaxFileSize = 1024;
    // 文件上传组件
    private FileUploadComponent mFileUploadComponent;
    private int mRequestCodeCrop = 90100;
    private int mRequestCodeSelectFile = 90101;
    // 是否支持图片裁剪
    private boolean mEnableCrop;
    private String mCurCropPhotoPath;
    private List<String> mCropPathList = new ArrayList<>();
    // 文件上传线程
    private HandlerThread mHandlerThread;
    // 文件上传线程的Handler
    private Handler mThreadHandler;
    // 主线程Handler
    private Handler mMainHandler;
    private boolean mIsInit;
    private FileUploadComponent.OneByOneUploadAdapter mOneByOneUploadAdapter;
    private FileUploadComponent.TogetherUploadAdapter mTogetherUploadAdapter;
    private boolean mTogetherUploadMode;

    private boolean mIsReUpload = false;
    private boolean mEnableImageScale = false; // 是否开启图片缩放功能
    private boolean mEnableImageTranslate = false; // 是否开启图片平移功能
    private boolean mEnableImageRotate = false; // 是否开启图片旋转功能

    public FileUploadHelper(@NonNull View view) {
        mFileUploaderConfig = (IFileUploaderConfig) view;
        if ((view instanceof IFileOneByOneUploader)) {
            mFileOneByOneUploader = (IFileOneByOneUploader) view;
        }
        if ((view instanceof IFileTogetherUploader)) {
            mFileTogetherUploader = (IFileTogetherUploader) view;
        }
        if (mFileOneByOneUploader == null && mFileTogetherUploader == null) {
            throw new IllegalStateException("view must be a instance of IFileOneByOneUploader or a instance of IFileTogetherUploader");
        }
        mView = view;
    }

    public void init(@NonNull Activity activity,
                     @NonNull FileUploadComponent.OneByOneUploadAdapter adapter, int requestCodeSelectFile) {
        mActivity = activity;
        if (mActivity == null || !(mActivity instanceof ILifeCircleViewContainer)) {
            throw new IllegalStateException("Activity should not be empty and must be a instance of ILifeCircleViewContainer");
        }
        ((ILifeCircleViewContainer) activity).attachCircleView(this);
        if (adapter != null) {
            if (!(mView instanceof IFileOneByOneUploader)) {
                throw new IllegalStateException("view must be a instance of IFileOneByOneUploader");
            }
            mTogetherUploadMode = false;
            if (TextUtils.isEmpty(adapter.getUploadUrl())) {
                throw new IllegalStateException("Upload url should not be empty");
            }
            mOneByOneUploadAdapter = adapter;
            mRequestCodeSelectFile = requestCodeSelectFile;
            if (mMainHandler == null) {
                mMainHandler = new Handler(Looper.getMainLooper());
            }
        }
        mIsInit = true;
    }

    public void init(@NonNull Activity activity,
                     @NonNull FileUploadComponent.TogetherUploadAdapter adapter, int requestCodeSelectFile) {
        mActivity = activity;
        if (mActivity == null || !(mActivity instanceof ILifeCircleViewContainer)) {
            throw new IllegalStateException("Activity should not be empty and must be a instance of ILifeCircleViewContainer");
        }
        ((ILifeCircleViewContainer) activity).attachCircleView(this);
        if (adapter != null) {
            if (!(mView instanceof IFileTogetherUploader)) {
                throw new IllegalStateException("view must be a instance of IFileTogetherUploader");
            }
            mTogetherUploadMode = true;
            if (TextUtils.isEmpty(adapter.getUploadUrl())) {
                throw new IllegalStateException("Upload url should not be empty");
            }
            mTogetherUploadAdapter = adapter;
            mRequestCodeSelectFile = requestCodeSelectFile;
            if (mMainHandler == null) {
                mMainHandler = new Handler(Looper.getMainLooper());
            }
        }
        mIsInit = true;
    }

    public void setCropEnable(int cropRequestCode) {
        mEnableCrop = mFileUploaderConfig.getUploadFileType() == FileUploadComponent.TYPE_IMAGE;
        if (mEnableCrop) {
            mRequestCodeCrop = cropRequestCode;
        }
    }

    public void setCropEnable(int cropRequestCode, int cropWidth, int cropHeight) {
        if (mFileUploaderConfig.getUploadFileType() == FileUploadComponent.TYPE_IMAGE) {
            mEnableCrop = true;
            mCropWidth = cropWidth;
            mCropHeight = cropHeight;
            mRequestCodeCrop = cropRequestCode;
        } else {
            mEnableCrop = false;
        }
    }

    public FileUploadComponent getFileUploadComponent() {
        return mFileUploadComponent;
    }

    public void setMaxFileCount(int maxFileCount) {
        mMaxFileCount = maxFileCount;
    }

    public int getMaxFileCount() {
        return mMaxFileCount;
    }

    public void setMaxFileSize(int maxFileSize) {
        mMaxFileSize = maxFileSize;
    }

    public int getMaxFileSize() {
        return mMaxFileSize;
    }

    public void enableImageScale(boolean enable) {
        mEnableImageScale = enable;
    }

    public void enableImageTranslate(boolean enable) {
        mEnableImageTranslate = enable;
    }

    public void enableImageRotate(boolean enable) {
        mEnableImageRotate = enable;
    }

    /**
     * 打开系统裁剪功能
     */
    protected void startCropImage(String filePath) {
        if (mActivity == null) {
            return;
        }
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        // 系统裁剪工具只能读取公共目录，使用公共目录
        String targetFilePath = PathUtils.getExternalPublicPath(Environment.DIRECTORY_PICTURES) +
                File.separator + "crop_" + fileName;
        File targetFile = new File(targetFilePath);
        if (targetFile.exists()) {
            targetFile.delete();
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(new File(filePath)), "image/*");
        intent.putExtra("crop", true);
        if (mCropWidth >= mCropHeight) {
            // 设置x,y的比例，截图方框就按照这个比例来截 若设置为0,0，或者不设置 则自由比例截图
            intent.putExtra("aspectX", mCropWidth / mCropHeight);
            intent.putExtra("aspectY", 1);
        } else {
            // 设置x,y的比例，截图方框就按照这个比例来截 若设置为0,0，或者不设置 则自由比例截图
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", mCropHeight / mCropWidth);
        }
        // 裁剪区的宽和高 其实就是裁剪后的显示区域 若裁剪的比例不是显示的比例，
        // 则自动压缩图片填满显示区域。若设置为0,0 就不显示。若不设置，则按原始大小显示
        intent.putExtra("outputX", mCropWidth);
        intent.putExtra("outputY", mCropHeight);
        intent.putExtra("scale", true);
        // true的话直接返回bitmap，可能会很占内存 不建议
        intent.putExtra("return-data", false);
        // 上面设为false的时候将MediaStore.EXTRA_OUTPUT即"output"关联一个Uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(targetFile));
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        mCurCropPhotoPath = targetFilePath;
        mCropPathList.add(targetFilePath);
        LogUtils.d(TAG, "startCropPhoto :" + intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT));
        mActivity.startActivityForResult(intent, mRequestCodeCrop);
    }

    public void selectUploadObjects() {
        if (mFileUploaderConfig.getUploadFileType() == FileUploadComponent.TYPE_IMAGE) {
            selectImages(false);
        } else {
            selectFiles(false);
        }
    }

    public void reSelectUploadObjects() {
        if (mFileUploaderConfig.getUploadFileType() == FileUploadComponent.TYPE_IMAGE) {
            selectImages(true);
        } else {
            selectFiles(true);
        }
    }

    /**
     * 打开相册或者照相机选择图片，最多mMaxFileCount张
     */
    private void selectImages(boolean reSelect) {
        int validCount = mFileUploaderConfig.getValidFileCount();
        validCount = reSelect ? 0 : validCount;
        if (validCount >= mMaxFileCount) {
            Toast.makeText(mActivity, mActivity.getResources().getString(R.string.base_upload_image_count_exceeding_msg,
                    mMaxFileCount), Toast.LENGTH_SHORT).show();
            return;
        }
        int allowCount = (mMaxFileCount - validCount > MAX_PER_UPLOAD_FILE_COUNT ?
                MAX_PER_UPLOAD_FILE_COUNT : mMaxFileCount - validCount);
        LogUtils.d(TAG, "selectImages mEnableCrop:" + mEnableCrop +
                ", allowCount:" + allowCount);
        MediaSelector.createImageSelector()
                .count(mEnableCrop ? 1 : allowCount)
                .addCameraTakePic()
                .start(mActivity, mRequestCodeSelectFile);
    }

    /**
     * 打开文件管理选择文件，最多mMaxFileCount张
     */
    private void selectFiles(boolean reSelect) {
        int validCount = mFileUploaderConfig.getValidFileCount();
        validCount = reSelect ? 0 : validCount;
        if (validCount >= mMaxFileCount) {
            Toast.makeText(mActivity, mActivity.getResources().getString(R.string.base_upload_file_count_exceeding_msg,
                    mMaxFileCount), Toast.LENGTH_SHORT).show();
            return;
        }
        int allowCount = (mMaxFileCount - validCount > MAX_PER_UPLOAD_FILE_COUNT ?
                MAX_PER_UPLOAD_FILE_COUNT : mMaxFileCount - validCount);
        LogUtils.d(TAG, "selectFiles allowCount:" + allowCount);
        //调用系统文件管理器打开指定路径目录
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        mActivity.startActivityForResult(intent, mRequestCodeSelectFile);
    }

    public void displayUploadObject(ArrayList<String> displayList, int position) {
        if (mFileUploaderConfig.getUploadFileType() == FileUploadComponent.TYPE_IMAGE) {
            displayBigImages(displayList, position);
        } else {
            displayFiles(displayList, position);
        }
    }

    public void displayBigImages(ArrayList<String> displayList, int position) {
        ImageViewer.create()
                .origin(displayList)
                .position(position < 0 ? 0 : position)
                .enableImageScale(mEnableImageScale)
                .enableImageTranslate(mEnableImageTranslate)
                .enableImageRotate(mEnableImageRotate)
                .start(mActivity);
    }

    public void displayFiles(ArrayList<String> displayList, int position) {
        if (position < 0) {
            return;
        }
        File file = new File(displayList.get(position));
        FileUtils.openFile(mActivity, file, mActivity.getResources().getString(R.string.base_file_open_not_support_type));
    }

    public void release() {
        if (mHandlerThread != null) {
            mFileUploadComponent.cancelAll();
            mThreadHandler.removeCallbacksAndMessages(null);
            mThreadHandler = null;
            mHandlerThread.quit();
            mHandlerThread = null;
        }
        if (mMainHandler != null) {
            mMainHandler.removeCallbacksAndMessages(null);
            mMainHandler = null;
        }
        if (mCropPathList.size() > 0) {
            for (String path : mCropPathList) {
                FileUtils.deleteFile(path);
            }
        }
        mActivity = null;
    }

    // 另一种方式将图片变成媒体文件，否则它是文件，需要文件权限（MANAGE_EXTERNAL_STORAGE，该权限只能用户手动授权，比较麻烦）
    public void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getPath())));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == mRequestCodeCrop) {
            if (resultCode == Activity.RESULT_OK) {
                if (mActivity == null) {
                    return;
                }
                final Uri uri = Uri.fromFile(new File(mCurCropPhotoPath));
                // 发送广播是将图片变成媒体文件，否则它是文件，需要文件权限（MANAGE_EXTERNAL_STORAGE，该权限只能用户手动授权，比较麻烦）
                mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<MediaBean> newSelectList = new ArrayList<>();
                        newSelectList.add(MediaBean.buildImageBean(mCurCropPhotoPath));
                        LogUtils.d(TAG, "onActivityResult REQUEST_CODE_CROP" +
                                " mCurCropPhotoPath:" + mCurCropPhotoPath + ", return uri:" + uri);
                        uploadFileOneByOne(newSelectList);
                    }
                }, 500);
            }
        } else if (requestCode == mRequestCodeSelectFile) {
            if (resultCode == Activity.RESULT_OK) {
                if (mFileUploaderConfig.getUploadFileType() == FileUploadComponent.TYPE_IMAGE) {
                    List<MediaBean> newSelectList = MediaSelector.getIntentResultData(data);
                    mIsReUpload = data.getBooleanExtra(MediaSelector.INTENT_IS_RESELECT, false);
                    if (newSelectList.size() < 1) {
                        return;
                    }
                    LogUtils.d(TAG, "onActivityResult REQUEST_CODE_SELECT_IMAGE" +
                            " mEnableCrop:" + mEnableCrop + ", mTogetherUploadMode:" + mTogetherUploadMode +
                            ", newSelectList.size():" + newSelectList.size());
                    if (mEnableCrop) {
                        startCropImage(newSelectList.get(0).getUrl());
                    } else {
                        if (mTogetherUploadMode) {
                            uploadFileList(newSelectList);
                        } else {
                            uploadFileOneByOne(newSelectList);
                        }
                    }
                } else {
                    Uri uri = data.getData();
                    String path;
                    if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                        path = uri.getPath();
                    } else {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                            path = UriUtils.getPath(mActivity, uri);
                        } else {//4.4以下下系统调用方法
                            path = UriUtils.getRealPathFromURI(mActivity, uri);
                        }
                    }
                    LogUtils.d(TAG, "onActivityResult REQUEST_CODE_SELECT_IMAGE" +
                            " mTogetherUploadMode:" + mTogetherUploadMode +
                            ", path:" + path);
                    List<MediaBean> list = new ArrayList<>();
                    list.add(MediaBean.buildImageBean(path));
                    if (mTogetherUploadMode) {
                        uploadFileList(list);
                    } else {
                        uploadFileOneByOne(list);
                    }
                }
            }
        }
    }

    private void uploadFileOneByOne(final List<MediaBean> list) {
        if (!mIsInit) {
            throw new IllegalStateException("You should call init() method before use this view");
        }
        if (list == null || list.size() < 1 || mOneByOneUploadAdapter == null) {
            return;
        }
        LogUtils.d(TAG, "uploadFileOneByOne list.size():" + list.size());
        if (mHandlerThread == null) {
            mHandlerThread = new HandlerThread("UploadFileRecyclerView");
            mHandlerThread.start();
            mThreadHandler = new Handler(mHandlerThread.getLooper());
        }
        if (mMainHandler == null) {
            mMainHandler = new Handler(Looper.getMainLooper());
        }
        final List<FileUploadBean> uploadBeanList = new ArrayList<>();
        FileUploadBean fileUploadBean = null;
        for (int i = 0; i < list.size(); i++) {
            fileUploadBean = new FileUploadBean();
            String filePath = list.get(i).getUrl();
            fileUploadBean.setFileKey(mOneByOneUploadAdapter.getFileKey(fileUploadBean));
            fileUploadBean.setFileType(mFileUploaderConfig.getUploadFileType());
            fileUploadBean.setLocalFilePath(filePath);
            fileUploadBean.setFileName(filePath.substring(filePath.lastIndexOf(File.separator) + 1));
            fileUploadBean.setParams(mOneByOneUploadAdapter.getUploadParam(fileUploadBean));
            fileUploadBean.setOrderIndex(i);
            fileUploadBean.setRequestUrl(mOneByOneUploadAdapter.getUploadUrl());
            fileUploadBean.setUploadState(FileUploadState.UPLOAD_STATE_PREPARING);
            fileUploadBean.setReUpload(mIsReUpload);
            uploadBeanList.add(fileUploadBean);
        }
        if (uploadBeanList.size() < 1) {
            return;
        }
        mFileOneByOneUploader.onFileUploadPrepare(uploadBeanList);
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mView == null || !mView.isAttachedToWindow()) {
                    return;
                }
                mFileUploadComponent = new FileUploadComponent(mActivity, mMaxFileSize,
                        mCompressImageWidth, mCompressImageHeight);
                mFileUploadComponent.startOneByOne(uploadBeanList,
                        new FileUploadComponent.OneByOneUploadCallback() {

                            @Override
                            public void onImageCompressProgress(final FileUploadBean fileBean, final int compressPercentage) {
                                if (mMainHandler == null) {
                                    return;
                                }
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        fileBean.setUploadState(FileUploadState.UPLOAD_STATE_IMAGE_COMPRESS);
                                        mFileOneByOneUploader.onImageCompressProgress(fileBean, compressPercentage);
                                    }
                                });
                            }

                            @Override
                            public void onStart(final FileUploadBean fileBean) {
                                if (mMainHandler == null) {
                                    return;
                                }
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        fileBean.setUploadState(FileUploadState.UPLOAD_STATE_START);
                                        mFileOneByOneUploader.onFileUploadStart(fileBean);
                                    }
                                });
                            }

                            @Override
                            public void onProgress(final FileUploadBean fileBean, final int progress) {
                                if (mMainHandler == null) {
                                    return;
                                }
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        fileBean.setUploadState(FileUploadState.UPLOAD_STATE_UPLOADING);
                                        fileBean.setUploadProgress(progress);
                                        mFileOneByOneUploader.onFileUploadProgress(fileBean);
                                    }
                                });
                            }

                            @Override
                            public void onCancel(final FileUploadBean fileBean) {
                                if (mMainHandler == null) {
                                    return;
                                }
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        fileBean.setUploadState(FileUploadState.UPLOAD_STATE_CANCEL);
                                        mFileOneByOneUploader.onFileUploadCancel(fileBean);
                                    }
                                });
                            }

                            @Override
                            public void onFailed(final FileUploadBean fileBean, final Exception exception) {
                                if (mMainHandler == null) {
                                    return;
                                }
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        fileBean.setUploadState(FileUploadState.UPLOAD_STATE_FAIL);
                                        mFileOneByOneUploader.onFileUploadFail(fileBean, exception);
                                    }
                                });
                            }

                            @Override
                            public void onSuccess(final FileUploadBean fileBean, final JSONObject response) {
                                if (mMainHandler == null) {
                                    return;
                                }
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        fileBean.setResponseData(response.toString());
                                        RemoteUploadFileInfo fileInfo = mOneByOneUploadAdapter
                                                .getRemoteFileInfoFromResponse(fileBean, response);
                                        if (fileInfo == null || TextUtils.isEmpty(fileInfo.getUrl())) {
                                            onFailed(fileBean, new MessageException(mActivity.getResources().getString(R.string.base_json_data_err)));
                                            return;
                                        }
                                        fileBean.setRemoteFilePath(fileInfo.getUrl());
                                        fileBean.setUploadState(FileUploadState.UPLOAD_STATE_SUCCESS);
                                        mFileOneByOneUploader.onFileUploadSuccess(fileBean);
                                    }
                                });
                            }
                        });
            }
        });
    }

    private void uploadFileList(final List<MediaBean> list) {
        if (!mIsInit) {
            throw new IllegalStateException("You should call init() method before use this view");
        }
        if (list == null || list.size() < 1 || mTogetherUploadAdapter == null) {
            return;
        }
        LogUtils.d(TAG, "uploadFileList list.size():" + list.size());
        if (mHandlerThread == null) {
            mHandlerThread = new HandlerThread("FileUploadView");
            mHandlerThread.start();
            mThreadHandler = new Handler(mHandlerThread.getLooper());
        }
        if (mMainHandler == null) {
            mMainHandler = new Handler(Looper.getMainLooper());
        }
        final List<FileUploadBean> uploadBeanList = new ArrayList<>();
        FileUploadBean fileUploadBean = null;
        for (int i = 0; i < list.size(); i++) {
            fileUploadBean = new FileUploadBean();
            String filePath = list.get(i).getUrl();
            fileUploadBean.setFileKey(mTogetherUploadAdapter.getFileKey(fileUploadBean));
            fileUploadBean.setFileType(mFileUploaderConfig.getUploadFileType());
            fileUploadBean.setLocalFilePath(filePath);
            fileUploadBean.setFileName(filePath.substring(filePath.lastIndexOf(File.separator) + 1));
            fileUploadBean.setOrderIndex(i);
            fileUploadBean.setRequestUrl(mTogetherUploadAdapter.getUploadUrl());
            fileUploadBean.setUploadState(FileUploadState.UPLOAD_STATE_PREPARING);
            fileUploadBean.setReUpload(mIsReUpload);
            uploadBeanList.add(fileUploadBean);
        }
        if (uploadBeanList.size() < 1) {
            return;
        }
        mFileTogetherUploader.onFileUploadPrepare(uploadBeanList);
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mView == null || !mView.isAttachedToWindow()) {
                    return;
                }
                mFileUploadComponent = new FileUploadComponent(mActivity, mMaxFileSize,
                        mCompressImageWidth, mCompressImageHeight);
                mFileUploadComponent.startTogether(mTogetherUploadAdapter.getUploadUrl(),
                        mTogetherUploadAdapter.getUploadParam(uploadBeanList),
                        mTogetherUploadAdapter.getFilesKey(uploadBeanList),
                        uploadBeanList, new FileUploadComponent.SimpleTogetherUploadListCallback() {

                            @Override
                            public void onImageCompressProgress(final List<FileUploadBean> fileBeanList) {
                                if (mMainHandler == null) {
                                    return;
                                }
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (FileUploadBean fileBean : fileBeanList) {
                                            fileBean.setUploadState(FileUploadState.UPLOAD_STATE_IMAGE_COMPRESS);
                                        }
                                        mFileTogetherUploader.onImageCompressProgress(fileBeanList);
                                    }
                                });
                            }

                            @Override
                            public void onStart(final List<FileUploadBean> fileBeanList) {
                                if (mMainHandler == null) {
                                    return;
                                }
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (FileUploadBean fileBean : fileBeanList) {
                                            fileBean.setUploadState(FileUploadState.UPLOAD_STATE_START);
                                        }
                                        mFileTogetherUploader.onFileUploadProgress(fileBeanList);
                                    }
                                });
                            }

                            @Override
                            public void onProgress(final List<FileUploadBean> fileBeanList, final int progress) {
                                if (mMainHandler == null) {
                                    return;
                                }
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (FileUploadBean fileBean : fileBeanList) {
                                            fileBean.setUploadState(FileUploadState.UPLOAD_STATE_UPLOADING);
                                            fileBean.setUploadProgress(progress);
                                        }
                                        mFileTogetherUploader.onFileUploadProgress(fileBeanList);
                                    }
                                });
                            }

                            @Override
                            public void onCancel(final List<FileUploadBean> fileBeanList) {
                                if (mMainHandler == null) {
                                    return;
                                }
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (FileUploadBean fileBean : fileBeanList) {
                                            fileBean.setUploadState(FileUploadState.UPLOAD_STATE_CANCEL);
                                            mFileTogetherUploader.onFileUploadCancel(fileBean);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFailed(final List<FileUploadBean> fileBeanList, final Exception exception) {
                                if (mMainHandler == null) {
                                    return;
                                }
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (FileUploadBean fileBean : fileBeanList) {
                                            fileBean.setUploadState(FileUploadState.UPLOAD_STATE_FAIL);
                                        }
                                        mFileTogetherUploader.onFileUploadFail(fileBeanList, exception);
                                    }
                                });
                            }

                            @Override
                            public void onSuccess(final List<FileUploadBean> fileBeanList, final JSONObject response) {
                                if (mMainHandler == null) {
                                    return;
                                }
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        List<RemoteUploadFileInfo> fileInfoList = mTogetherUploadAdapter
                                                .getRemoteFileInfoListFromResponse(fileBeanList, response);
                                        if (fileInfoList == null || fileInfoList.size() < 1 ||
                                                fileInfoList.size() != fileBeanList.size()) {
                                            onFailed(fileBeanList, new MessageException(mActivity.getResources().getString(R.string.base_json_data_err)));
                                            return;
                                        }
                                        for (int i = 0; i < fileBeanList.size(); i++) {
                                            FileUploadBean fileBean = fileBeanList.get(i);
                                            fileBean.setResponseData(response.toString());
                                            fileBean.setRemoteFilePath(fileInfoList.get(i) == null ? "" : fileInfoList.get(i).getUrl());
                                            fileBean.setUploadState(FileUploadState.UPLOAD_STATE_SUCCESS);
                                        }
                                        mFileTogetherUploader.onFileUploadSuccess(fileBeanList);
                                    }
                                });
                            }
                        });
            }
        });
    }
}
