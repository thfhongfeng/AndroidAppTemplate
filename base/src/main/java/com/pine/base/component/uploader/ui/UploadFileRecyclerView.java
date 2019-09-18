package com.pine.base.component.uploader.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Toast;

import com.pine.base.R;
import com.pine.base.component.image_selector.ImageSelector;
import com.pine.base.component.image_selector.ImageViewer;
import com.pine.base.component.uploader.FileUploadComponent;
import com.pine.base.component.uploader.bean.FileUploadBean;
import com.pine.base.component.uploader.bean.FileUploadState;
import com.pine.tool.util.FileUtils;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.PathUtils;
import com.pine.tool.util.UriUtils;
import com.pine.tool.widget.ILifeCircleView;
import com.pine.tool.widget.ILifeCircleViewContainer;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tanghongfeng on 2018/11/13
 */

public abstract class UploadFileRecyclerView extends RecyclerView implements ILifeCircleView {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    // 每次选择文件最大允数
    private final int MAX_PER_UPLOAD_FILE_COUNT = 10;
    protected Activity mActivity;
    // 图片需要压缩时（图片大小大于允许上传大小时）的输出宽度和高度
    protected int mCompressImageWidth = 1440;
    protected int mCompressImageHeight = 2550;
    // 每张图片裁剪宽高
    protected int mCropWidth = 360;
    protected int mCropHeight = 360;
    // 最大允许上传文件数
    protected int mMaxFileCount = 30;
    // 最大允许上传文件大小
    protected long mMaxFileSize = 1024 * 1024;
    // 文件上传组件
    protected FileUploadComponent mFileUploadComponent;
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
    private OneByOneUploadAdapter mOneByOneUploadAdapter;
    private TogetherUploadAdapter mTogetherUploadAdapter;
    private boolean mTogetherUploadMode;

    public UploadFileRecyclerView(Context context) {
        super(context);
    }

    public UploadFileRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UploadFileRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initUpload(@NonNull Activity activity) {
        mActivity = activity;
        if (mActivity == null || !(mActivity instanceof ILifeCircleViewContainer)) {
            throw new IllegalStateException("Activity should not be empty and must be a instance of ILifeCircleViewContainer");
        }
        ((ILifeCircleViewContainer) activity).attachCircleView(this);
        mIsInit = true;
    }

    protected void initUpload(@NonNull Activity activity,
                              @NonNull OneByOneUploadAdapter adapter, int requestCodeSelectFile) {
        mActivity = activity;
        if (mActivity == null || !(mActivity instanceof ILifeCircleViewContainer)) {
            throw new IllegalStateException("Activity should not be empty and must be a instance of ILifeCircleViewContainer");
        }
        ((ILifeCircleViewContainer) activity).attachCircleView(this);
        mTogetherUploadMode = false;
        if (TextUtils.isEmpty(adapter.getUploadUrl())) {
            throw new IllegalStateException("Upload url should not be empty");
        }
        mOneByOneUploadAdapter = adapter;
        mRequestCodeSelectFile = requestCodeSelectFile;
        if (mMainHandler == null) {
            mMainHandler = new Handler(Looper.getMainLooper());
        }
        mIsInit = true;
    }

    public void initUpload(@NonNull Activity activity,
                           @NonNull TogetherUploadAdapter adapter, int requestCodeSelectFile) {
        mActivity = activity;
        if (mActivity == null || !(mActivity instanceof ILifeCircleViewContainer)) {
            throw new IllegalStateException("Activity should not be empty and must be a instance of ILifeCircleViewContainer");
        }
        ((ILifeCircleViewContainer) activity).attachCircleView(this);
        mTogetherUploadMode = true;
        if (TextUtils.isEmpty(adapter.getUploadUrl())) {
            throw new IllegalStateException("Upload url should not be empty");
        }
        mTogetherUploadAdapter = adapter;
        mRequestCodeSelectFile = requestCodeSelectFile;
        if (mMainHandler == null) {
            mMainHandler = new Handler(Looper.getMainLooper());
        }
        mIsInit = true;
    }

    public void setCropEnable(int cropRequestCode) {
        mEnableCrop = getUploadFileType() == FileUploadComponent.TYPE_IMAGE;
        if (mEnableCrop) {
            mRequestCodeCrop = cropRequestCode;
        }
    }

    public void setCropEnable(int cropRequestCode, int cropWidth, int cropHeight) {
        if (getUploadFileType() == FileUploadComponent.TYPE_IMAGE) {
            mEnableCrop = true;
            mCropWidth = cropWidth;
            mCropHeight = cropHeight;
            mRequestCodeCrop = cropRequestCode;
        } else {
            mEnableCrop = false;
        }
    }

    public void setMaxFileCount(int maxFileCount) {
        mMaxFileCount = maxFileCount;
    }

    public void setMaxFileSize(int maxFileSize) {
        mMaxFileSize = maxFileSize;
    }

    /**
     * 打开系统裁剪功能
     */
    protected void startCropImage(String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        String targetFilePath = PathUtils.getAppFilePath(Environment.DIRECTORY_PICTURES) +
                File.separator + "crop_" + System.currentTimeMillis() + "_" + fileName;

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
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(targetFilePath)));
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        mCurCropPhotoPath = targetFilePath;
        mCropPathList.add(targetFilePath);
        LogUtils.d(TAG, "startCropPhoto filePath:" + filePath);
        mActivity.startActivityForResult(intent, mRequestCodeCrop);
    }

    protected void selectUploadObjects() {
        if (getUploadFileType() == FileUploadComponent.TYPE_IMAGE) {
            selectImages();
        } else {
            selectFiles();
        }
    }

    /**
     * 打开相册或者照相机选择图片，最多mMaxFileCount张
     */
    private void selectImages() {
        int validCount = getValidFileCount();
        if (validCount >= mMaxFileCount) {
            Toast.makeText(mActivity, getResources().getString(R.string.base_upload_image_count_exceeding_msg,
                    mMaxFileCount), Toast.LENGTH_SHORT).show();
            return;
        }
        int allowCount = (mMaxFileCount - validCount > MAX_PER_UPLOAD_FILE_COUNT ?
                MAX_PER_UPLOAD_FILE_COUNT : mMaxFileCount - validCount);
        LogUtils.d(TAG, "selectImages mEnableCrop:" + mEnableCrop +
                ", allowCount:" + allowCount);
        ImageSelector.create()
                .count(mEnableCrop ? 1 : allowCount)
                .start(mActivity, mRequestCodeSelectFile);
    }

    /**
     * 打开文件管理选择文件，最多mMaxFileCount张
     */
    private void selectFiles() {
        int validCount = getValidFileCount();
        if (validCount >= mMaxFileCount) {
            Toast.makeText(mActivity, getResources().getString(R.string.base_upload_file_count_exceeding_msg,
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

    protected void displayUploadObject(ArrayList<String> displayList, int position) {
        if (getUploadFileType() == FileUploadComponent.TYPE_IMAGE) {
            displayBigImages(displayList, position);
        } else {
            displayFiles(displayList, position);
        }
    }

    private void displayBigImages(ArrayList<String> displayList, int position) {
        ImageViewer.create()
                .origin(displayList)
                .position(position < 0 ? 0 : position)
                .start(mActivity);
    }

    private void displayFiles(ArrayList<String> displayList, int position) {
        if (position < 0) {
            return;
        }
        File file = new File(displayList.get(position));
        FileUtils.openFile(getContext(), file, getResources().getString(R.string.base_file_open_not_support_type));
    }

    @Override
    public void onDetachedFromWindow() {
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
        super.onDetachedFromWindow();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == mRequestCodeCrop) {
            if (resultCode == Activity.RESULT_OK) {
                List<String> newSelectList = new ArrayList<>();
                newSelectList.add(mCurCropPhotoPath);
                LogUtils.d(TAG, "onActivityResult REQUEST_CODE_CROP" +
                        " mCurCropPhotoPath:" + mCurCropPhotoPath);
                uploadFileOneByOne(newSelectList);
            }
        } else if (requestCode == mRequestCodeSelectFile) {
            if (resultCode == Activity.RESULT_OK) {
                if (getUploadFileType() == FileUploadComponent.TYPE_IMAGE) {
                    List<String> newSelectList = data.getStringArrayListExtra(
                            ImageSelector.INTENT_SELECTED_IMAGE_LIST);
                    if (newSelectList.size() < 1) {
                        return;
                    }
                    LogUtils.d(TAG, "onActivityResult REQUEST_CODE_SELECT_IMAGE" +
                            " mEnableCrop:" + mEnableCrop + ", mTogetherUploadMode:" + mTogetherUploadMode +
                            ", newSelectList.size():" + newSelectList.size());
                    if (mEnableCrop) {
                        startCropImage(newSelectList.get(0));
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
                            path = UriUtils.getPath(getContext(), uri);
                        } else {//4.4以下下系统调用方法
                            path = UriUtils.getRealPathFromURI(getContext(), uri);
                        }
                    }
                    LogUtils.d(TAG, "onActivityResult REQUEST_CODE_SELECT_IMAGE" +
                            " mTogetherUploadMode:" + mTogetherUploadMode +
                            ", path:" + path);
                    List<String> list = new ArrayList<>();
                    list.add(path);
                    if (mTogetherUploadMode) {
                        uploadFileList(list);
                    } else {
                        uploadFileOneByOne(list);
                    }
                }
            }
        }
    }

    private void uploadFileOneByOne(final List<String> list) {
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
            String filePath = list.get(i);
            fileUploadBean.setFileKey(mOneByOneUploadAdapter.getFileKey(fileUploadBean));
            fileUploadBean.setFileType(getUploadFileType());
            fileUploadBean.setLocalFilePath(filePath);
            fileUploadBean.setFileName(filePath.substring(filePath.lastIndexOf(File.separator) + 1));
            fileUploadBean.setParams(mOneByOneUploadAdapter.getUploadParam(fileUploadBean));
            fileUploadBean.setOrderIndex(i);
            fileUploadBean.setRequestUrl(mOneByOneUploadAdapter.getUploadUrl());
            fileUploadBean.setUploadState(FileUploadState.UPLOAD_STATE_PREPARING);
            uploadBeanList.add(fileUploadBean);
        }
        if (uploadBeanList.size() < 1) {
            return;
        }
        onFileUploadPrepare(uploadBeanList);
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!isAttachedToWindow()) {
                    return;
                }
                mFileUploadComponent = new FileUploadComponent(mActivity, mMaxFileSize,
                        mCompressImageWidth, mCompressImageHeight);
                mFileUploadComponent.startOneByOne(uploadBeanList,
                        new FileUploadComponent.OneByOneUploadCallback() {

                            @Override
                            public void onStart(final FileUploadBean fileBean) {
                            }

                            @Override
                            public void onProgress(final FileUploadBean fileBean, final int progress) {
                                if (mMainHandler == null) {
                                    return;
                                }
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        fileBean.setUploadProgress(progress);
                                        onFileUploadProgress(fileBean);
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
                                        onFileUploadCancel(fileBean);
                                    }
                                });
                            }

                            @Override
                            public void onFailed(final FileUploadBean fileBean, String message) {
                                if (mMainHandler == null) {
                                    return;
                                }
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        fileBean.setUploadState(FileUploadState.UPLOAD_STATE_FAIL);
                                        onFileUploadFail(fileBean);
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
                                        String url = mOneByOneUploadAdapter
                                                .getRemoteUrlFromResponse(fileBean, response);
                                        if (TextUtils.isEmpty(url)) {
                                            onFailed(fileBean, "");
                                            return;
                                        }
                                        fileBean.setRemoteFilePath(url);
                                        fileBean.setUploadState(FileUploadState.UPLOAD_STATE_SUCCESS);
                                        onFileUploadSuccess(fileBean);
                                    }
                                });
                            }
                        });
            }
        });
    }

    private void uploadFileList(final List<String> list) {
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
            String filePath = list.get(i);
            fileUploadBean.setFileKey(mTogetherUploadAdapter.getFileKey(fileUploadBean));
            fileUploadBean.setFileType(getUploadFileType());
            fileUploadBean.setLocalFilePath(filePath);
            fileUploadBean.setFileName(filePath.substring(filePath.lastIndexOf(File.separator) + 1));
            fileUploadBean.setOrderIndex(i);
            fileUploadBean.setRequestUrl(mTogetherUploadAdapter.getUploadUrl());
            fileUploadBean.setUploadState(FileUploadState.UPLOAD_STATE_PREPARING);
            uploadBeanList.add(fileUploadBean);
        }
        if (uploadBeanList.size() < 1) {
            return;
        }
        onFileUploadPrepare(uploadBeanList);
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (!isAttachedToWindow()) {
                    return;
                }
                mFileUploadComponent = new FileUploadComponent(mActivity, mMaxFileSize,
                        mCompressImageWidth, mCompressImageHeight);
                mFileUploadComponent.startTogether(mTogetherUploadAdapter.getUploadUrl(),
                        mTogetherUploadAdapter.getUploadParam(uploadBeanList),
                        mTogetherUploadAdapter.getFilesKey(uploadBeanList),
                        uploadBeanList, new FileUploadComponent.SimpleTogetherUploadListCallback() {
                            @Override
                            public void onProgress(final List<FileUploadBean> fileBeanList, final int progress) {
                                if (mMainHandler == null) {
                                    return;
                                }
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (FileUploadBean fileBean : fileBeanList) {
                                            fileBean.setUploadProgress(progress);
                                        }
                                        onFileUploadProgress(fileBeanList);
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
                                            onFileUploadCancel(fileBean);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFailed(final List<FileUploadBean> fileBeanList, String message) {
                                if (mMainHandler == null) {
                                    return;
                                }
                                mMainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (FileUploadBean fileBean : fileBeanList) {
                                            fileBean.setUploadState(FileUploadState.UPLOAD_STATE_FAIL);
                                        }
                                        onFileUploadFail(fileBeanList);
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
                                        List<String> urlList = mTogetherUploadAdapter
                                                .getRemoteUrlListFromResponse(fileBeanList, response);
                                        if (urlList == null || urlList.size() < 1 ||
                                                urlList.size() != fileBeanList.size()) {
                                            onFailed(fileBeanList, "");
                                            return;
                                        }
                                        for (int i = 0; i < fileBeanList.size(); i++) {
                                            FileUploadBean fileBean = fileBeanList.get(i);
                                            fileBean.setRemoteFilePath(urlList.get(i));
                                            fileBean.setUploadState(FileUploadState.UPLOAD_STATE_SUCCESS);
                                        }
                                        onFileUploadSuccess(fileBeanList);
                                    }
                                });
                            }
                        });
            }
        });
    }

    public abstract int getUploadFileType();

    public abstract int getValidFileCount();

    public abstract void onFileUploadPrepare(List<FileUploadBean> uploadBeanList);

    public abstract void onFileUploadProgress(FileUploadBean uploadBean);

    public abstract void onFileUploadCancel(FileUploadBean uploadBean);

    public abstract void onFileUploadFail(FileUploadBean uploadBean);

    public abstract void onFileUploadSuccess(FileUploadBean uploadBean);

    public abstract void onFileUploadProgress(List<FileUploadBean> uploadBeanList);

    public abstract void onFileUploadFail(List<FileUploadBean> uploadBeanList);

    public abstract void onFileUploadSuccess(List<FileUploadBean> uploadBeanList);


    public interface OneByOneUploadAdapter {
        // 获取上传文件的服务器地址
        String getUploadUrl();

        String getFileKey(FileUploadBean fileUploadBean);

        Map<String, String> getUploadParam(FileUploadBean fileUploadBean);

        String getRemoteUrlFromResponse(FileUploadBean fileUploadBean, JSONObject response);
    }

    public interface TogetherUploadAdapter {
        // 获取上传文件的服务器地址
        String getUploadUrl();

        String getFileKey(FileUploadBean fileUploadBean);

        String getFilesKey(List<FileUploadBean> fileUploadBeanList);

        Map<String, String> getUploadParam(List<FileUploadBean> fileUploadBeanList);

        List<String> getRemoteUrlListFromResponse(List<FileUploadBean> fileUploadBeanList, JSONObject response);
    }
}
