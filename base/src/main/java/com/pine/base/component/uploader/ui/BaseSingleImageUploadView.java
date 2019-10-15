package com.pine.base.component.uploader.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pine.base.R;
import com.pine.base.component.image_loader.ImageLoaderManager;
import com.pine.base.component.uploader.FileUploadComponent;
import com.pine.base.component.uploader.IFileOneByOneUploader;
import com.pine.base.component.uploader.bean.FileUploadBean;
import com.pine.base.component.uploader.bean.FileUploadItemData;
import com.pine.base.component.uploader.bean.FileUploadState;
import com.pine.base.util.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghongfeng on 2019/9/1
 */

public class BaseSingleImageUploadView extends UploadLinearLayout implements IFileOneByOneUploader {
    private RelativeLayout top_rl;
    private ImageView image_iv;
    private RelativeLayout state_rl;
    private TextView result_tv;
    private LinearLayout loading_ll;
    private ProgressBar progress_bar;
    private TextView progress_tv;
    private LinearLayout upload_action_ll;
    private TextView spanned_tv;
    private LinearLayout bottom_ll;
    private TextView re_upload_tv;

    // 最大允许上传文件大小
    private long mMaxFileSize = 1024 * 1024;
    private int mImageContainerWidth;
    private int mImageContainerHeight;

    private boolean mCanEdit = false;
    private boolean mShowUpdateState = true;
    private Spanned mItemSpanned;

    private FileUploadItemData mData;

    public BaseSingleImageUploadView(Context context) {
        super(context);
    }

    public BaseSingleImageUploadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        resolveAttrs(context, attrs);
    }

    public BaseSingleImageUploadView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        resolveAttrs(context, attrs);
    }

    private void resolveAttrs(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseSingleImageUploadView);
        mMaxFileSize = typedArray.getInt(R.styleable.BaseSingleImageUploadView_baseMaxFileSize, 1024 * 1024);
        mImageContainerWidth = typedArray.getDimensionPixelOffset(R.styleable.BaseSingleImageUploadView_base_imageWidth, context.getResources().getDimensionPixelOffset(R.dimen.dp_147));
        mImageContainerHeight = typedArray.getDimensionPixelOffset(R.styleable.BaseSingleImageUploadView_base_imageHeight, context.getResources().getDimensionPixelOffset(R.dimen.dp_78));
        boolean enableImageScale = typedArray.getBoolean(
                R.styleable.BaseSingleImageUploadView_base_enableImageScale, false);
        boolean enableImageTranslate = typedArray.getBoolean(
                R.styleable.BaseSingleImageUploadView_base_enableImageTranslate, false);
        boolean enableImageRotate = typedArray.getBoolean(
                R.styleable.BaseSingleImageUploadView_base_enableImageRotate, false);
        mHelper.enableImageScale(enableImageScale);
        mHelper.enableImageTranslate(enableImageTranslate);
        mHelper.enableImageRotate(enableImageRotate);
        String item = typedArray.getString(R.styleable.BaseSingleImageUploadView_base_bsiuv_itemText);
        if (!TextUtils.isEmpty(item)) {
            mItemSpanned = Html.fromHtml(item);
        }
    }

    @Override
    public void init(@NonNull Activity activity,
                     @NonNull FileUploadComponent.OneByOneUploadAdapter adapter, int requestCodeSelectFile) {
        mHelper.init(activity, adapter, requestCodeSelectFile);
        mHelper.setMaxFileCount(1);
        mHelper.setMaxFileSize(mMaxFileSize);
    }

    public void init(@NonNull Activity activity, FileUploadItemData data) {
        init(activity, false, null, data);
    }

    public void init(@NonNull Activity activity, boolean canEdit, Spanned itemSpanned, FileUploadItemData data) {
        mCanEdit = canEdit;
        mItemSpanned = itemSpanned;
        init(activity, null, -1);
        initView(data);
    }

    public void init(@NonNull Activity activity, boolean canEdit,
                     @NonNull FileUploadComponent.OneByOneUploadAdapter adapter, int requestCodeSelectFile,
                     Spanned itemSpanned, FileUploadItemData data) {
        mCanEdit = canEdit;
        mItemSpanned = itemSpanned;
        init(activity, adapter, requestCodeSelectFile);
        initView(data);
    }

    public void init(@NonNull Activity activity, boolean canEdit,
                     @NonNull FileUploadComponent.OneByOneUploadAdapter adapter, int requestCodeSelectFile,
                     Spanned itemSpanned, FileUploadItemData data, long maxFileSize) {
        mMaxFileSize = maxFileSize;
        mCanEdit = canEdit;
        mItemSpanned = itemSpanned;
        init(activity, adapter, requestCodeSelectFile);
        initView(data);
    }

    private void initView(FileUploadItemData data) {
        setOrientation(VERTICAL);
        mData = data;
        if (mData == null) {
            mData = new FileUploadItemData();
        }
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.base_single_file_upload, null);
        top_rl = view.findViewById(R.id.top_rl);
        image_iv = view.findViewById(R.id.image_iv);
        state_rl = view.findViewById(R.id.state_rl);
        result_tv = view.findViewById(R.id.result_tv);
        loading_ll = view.findViewById(R.id.loading_ll);
        progress_bar = view.findViewById(R.id.progress_bar);
        progress_tv = view.findViewById(R.id.progress_tv);
        upload_action_ll = view.findViewById(R.id.upload_action_ll);
        spanned_tv = view.findViewById(R.id.spanned_tv);
        bottom_ll = view.findViewById(R.id.bottom_ll);
        re_upload_tv = view.findViewById(R.id.re_upload_tv);

        setupImageWidthHeight();

        if (mItemSpanned == null) {
            spanned_tv.setVisibility(GONE);
        } else {
            spanned_tv.setText(mItemSpanned);
            spanned_tv.setVisibility(VISIBLE);
        }
        state_rl.setVisibility(mShowUpdateState ? VISIBLE : GONE);
        if (mCanEdit) {
            upload_action_ll.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHelper.selectUploadObjects();
                }
            });
            bottom_ll.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHelper.reSelectUploadObjects();
                }
            });
        }
        image_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage();
            }
        });
        addView(view);
        setupView();
    }

    private void setupImageWidthHeight() {
        LinearLayout.LayoutParams topLayoutParams = (LinearLayout.LayoutParams) top_rl.getLayoutParams();
        topLayoutParams.width = mImageContainerWidth;
        topLayoutParams.height = mImageContainerHeight;
        top_rl.setLayoutParams(topLayoutParams);

        LinearLayout.LayoutParams bottomLayoutParams = (LinearLayout.LayoutParams) bottom_ll.getLayoutParams();
        bottomLayoutParams.width = mImageContainerWidth;
        bottom_ll.setLayoutParams(bottomLayoutParams);
    }

    private void showImage() {
        String imageUrl = "";
        if (!TextUtils.isEmpty(mData.getLocalFilePath())) {
            imageUrl = "file://" + mData.getLocalFilePath();
        } else if (!TextUtils.isEmpty(mData.getRemoteFilePath())) {
            imageUrl = mData.getRemoteFilePath();
        }
        if (TextUtils.isEmpty(imageUrl)) {
            return;
        }
        ArrayList<String> urlList = new ArrayList<>();
        urlList.add(imageUrl);
        mHelper.displayUploadObject(urlList, 0);
    }

    private void setupView() {
        String imageUrl = "";
        if (!TextUtils.isEmpty(mData.getLocalFilePath())) {
            imageUrl = "file://" + mData.getLocalFilePath();
        } else if (!TextUtils.isEmpty(mData.getRemoteFilePath())) {
            imageUrl = mData.getRemoteFilePath();
        }
        if (TextUtils.isEmpty(imageUrl)) {
            image_iv.setImageDrawable(null);
            upload_action_ll.setVisibility(VISIBLE);
            bottom_ll.setVisibility(INVISIBLE);
        } else {
            if (!mShowUpdateState && mData.getUploadState() != FileUploadState.UPLOAD_STATE_SUCCESS) {
                imageUrl = mShowUpdateState ? imageUrl : "";
            }
            ImageLoaderManager.getInstance().loadImage(getContext(), imageUrl, image_iv);
            upload_action_ll.setVisibility(GONE);
            bottom_ll.setVisibility(VISIBLE);
        }
        if (!mCanEdit) {
            upload_action_ll.setVisibility(GONE);
            bottom_ll.setVisibility(VISIBLE);
        }
        refreshState();
    }

    private void refreshState() {
        if (mData != null) {
            if (mShowUpdateState) {
                switch (mData.getUploadState()) {
                    case UPLOAD_STATE_PREPARING:
                    case UPLOAD_STATE_UPLOADING:
                        progress_bar.setProgress(mData.getUploadProgress());
                        progress_tv.setText(mData.getUploadProgress() + "%");
                        loading_ll.setVisibility(VISIBLE);
                        result_tv.setVisibility(GONE);
                        state_rl.setVisibility(VISIBLE);
                        break;
                    case UPLOAD_STATE_CANCEL:
                        loading_ll.setVisibility(GONE);
                        result_tv.setVisibility(VISIBLE);
                        state_rl.setVisibility(VISIBLE);
                        break;
                    case UPLOAD_STATE_FAIL:
                        loading_ll.setVisibility(GONE);
                        result_tv.setVisibility(VISIBLE);
                        state_rl.setVisibility(VISIBLE);
                        break;
                    case UPLOAD_STATE_SUCCESS:
                        state_rl.setVisibility(GONE);
                        break;
                    default:
                        state_rl.setVisibility(GONE);
                        break;
                }
            }
        }
    }

    public void setImageWidthHeight(int width, int height) {
        mImageContainerWidth = width;
        mImageContainerHeight = height;
        setupImageWidthHeight();
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

    public String getUploadedFileRemote() {
        if (mData != null && !TextUtils.isEmpty(mData.getRemoteFilePath()) &&
                (mData.getUploadState() == FileUploadState.UPLOAD_STATE_SUCCESS ||
                        mData.getUploadState() == FileUploadState.UPLOAD_STATE_DEFAULT)) {
            return mData.getRemoteFilePath();
        }
        return "";
    }

    public FileUploadItemData getUploadFileData() {
        return mData;
    }

    public String getValidFile() {
        if (mData != null && (mData.getUploadState() == FileUploadState.UPLOAD_STATE_SUCCESS ||
                mData.getUploadState() == FileUploadState.UPLOAD_STATE_DEFAULT ||
                mData.getUploadState() == FileUploadState.UPLOAD_STATE_UPLOADING)) {
            if (!TextUtils.isEmpty(mData.getLocalFilePath())) {
                return mData.getLocalFilePath();
            } else if (!TextUtils.isEmpty(mData.getRemoteFilePath())) {
                return mData.getRemoteFilePath();
            }
        }
        return "";
    }

    @Override
    public int getUploadFileType() {
        return FileUploadComponent.TYPE_IMAGE;
    }

    @Override
    public int getValidFileCount() {
        String validFile = getValidFile();
        return TextUtils.isEmpty(validFile) ? 0 : 1;
    }

    @Override
    public void onFileUploadPrepare(List<FileUploadBean> uploadBeanList) {
        if (uploadBeanList != null && uploadBeanList.size() == 1) {
            copyUploadData(uploadBeanList.get(0));
            setupView();
        }
    }

    @Override
    public void onFileUploadProgress(FileUploadBean uploadBean) {
        if (uploadBean != null) {
            copyUploadData(uploadBean);
            setupView();
        }
    }

    @Override
    public void onFileUploadCancel(FileUploadBean uploadBean) {
        if (uploadBean != null) {
            copyUploadData(uploadBean);
            setupView();
        }
    }

    @Override
    public void onFileUploadFail(FileUploadBean uploadBean, Exception exception) {
        if (uploadBean != null) {
            copyUploadData(uploadBean);
            setupView();
        }
        String failMsg = ExceptionUtils.parseException(exception);
        if (!TextUtils.isEmpty(failMsg)) {
            Toast.makeText(getContext(), failMsg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFileUploadSuccess(FileUploadBean uploadBean) {
        if (uploadBean != null) {
            copyUploadData(uploadBean);
            setupView();
        }
    }

    private void copyUploadData(FileUploadBean uploadBean) {
        mData.setUploadState(uploadBean.getUploadState());
        mData.setUploadProgress(uploadBean.getUploadProgress());
        mData.setLocalFilePath(uploadBean.getLocalFilePath());
        mData.setRemoteFilePath(uploadBean.getRemoteFilePath());
        mData.setResponseData(uploadBean.getResponseData());
    }
}
