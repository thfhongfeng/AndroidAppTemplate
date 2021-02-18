package com.pine.template.base.component.editor.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pine.template.base.R;
import com.pine.template.base.component.editor.bean.TextImageEditorItemData;
import com.pine.template.base.component.editor.bean.TextImageEntity;
import com.pine.template.base.component.editor.bean.TextImageItemEntity;
import com.pine.template.base.component.image_loader.ImageLoaderManager;
import com.pine.template.base.component.uploader.FileUploadComponent;
import com.pine.template.base.component.uploader.FileUploadComponent.OneByOneUploadAdapter;
import com.pine.template.base.component.uploader.IFileOneByOneUploader;
import com.pine.template.base.component.uploader.bean.FileUploadBean;
import com.pine.template.base.component.uploader.bean.FileUploadState;
import com.pine.template.base.component.uploader.ui.UploadLinearLayout;
import com.pine.template.base.util.DialogUtils;
import com.pine.template.base.util.ExceptionUtils;
import com.pine.tool.util.KeyboardUtils;
import com.pine.tool.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.pine.template.base.component.editor.bean.TextImageItemEntity.TYPE_IMAGE;
import static com.pine.template.base.component.editor.bean.TextImageItemEntity.TYPE_TEXT;

/**
 * Created by tanghongfeng on 2018/11/13
 */

public class BaseTextImageEditorView extends UploadLinearLayout implements IFileOneByOneUploader {
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    // 编辑器索引
    private int mIndex;
    // 编辑器标题
    private String mTitle;
    // 子编辑条目之前的子View的个数（用于显示顶部标题等的非编辑型View，获取的编辑数据从这些View之后开始）
    private int mInitChildViewCount;
    // 顶部标题View
    private View mTopTitleView;
    // 当前子编辑条目View
    private View mCurAddNoteView;
    // 最大允许上传文件数
    protected int mMaxFileCount = 30;
    // 最大允许上传文件大小（单位K）
    protected int mMaxFileSize = 1024;

    public BaseTextImageEditorView(Context context) {
        super(context);
    }

    public BaseTextImageEditorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseTextImageEditorView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseTextImageEditorView);
        mMaxFileCount = typedArray.getInt(R.styleable.BaseTextImageEditorView_base_maxFileCount, mMaxFileCount);
        mMaxFileSize = typedArray.getInt(R.styleable.BaseTextImageEditorView_base_maxFileSize, mMaxFileSize);
        mHelper.setMaxFileCount(mMaxFileCount);
        mHelper.setMaxFileSize(mMaxFileSize);

        boolean enableImageScale = typedArray.getBoolean(
                R.styleable.BaseTextImageEditorView_base_enableImageScale, false);
        boolean enableImageTranslate = typedArray.getBoolean(
                R.styleable.BaseTextImageEditorView_base_enableImageTranslate, false);
        boolean enableImageRotate = typedArray.getBoolean(
                R.styleable.BaseTextImageEditorView_base_enableImageRotate, false);
        mHelper.enableImageScale(enableImageScale);
        mHelper.enableImageTranslate(enableImageTranslate);
        mHelper.enableImageRotate(enableImageRotate);
    }

    @Override
    public void init(@NonNull Activity activity,
                     @NonNull OneByOneUploadAdapter adapter, int requestCodeSelectFile) {
        mHelper.init(activity, adapter, requestCodeSelectFile);
    }

    public void init(@NonNull Activity activity, int index, String title,
                     @NonNull OneByOneUploadAdapter adapter, int requestCodeSelectImage) {
        init(activity, adapter, requestCodeSelectImage);
        initView(index, title);
    }

    public void initView(int index, String title) {
        setOrientation(VERTICAL);
        mIndex = index;
        mTitle = title;

        mInitChildViewCount = 0;
        mTopTitleView = LayoutInflater.from(getContext()).inflate(R.layout.base_text_image_editor_top, null);
        ((TextView) mTopTitleView.findViewById(R.id.title_tv)).setText(mTitle);
        mTopTitleView.setVisibility(TextUtils.isEmpty(mTitle) ? GONE : VISIBLE);
        addView(mTopTitleView);
        mInitChildViewCount++;

        mInitChildViewCount++;
        addText(mInitChildViewCount - 1, new TextImageEditorItemData(TYPE_TEXT), false);
    }

    private void addText(int position, @NonNull TextImageEditorItemData data, boolean needFocus) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.base_text_image_editor_item_text, null);

        setupEditorView(position, view);
        if (position == mInitChildViewCount - 1) {
            view.findViewById(R.id.text_rl).setVisibility(GONE);
        }

        addView(view, position);
        view.setTag(data);

        EditText text_et = view.findViewById(R.id.text_et);
        text_et.setText(data.getText());
        if (needFocus) {
            text_et.requestFocus();
            KeyboardUtils.openSoftKeyboard(getContext(), text_et);
        }
    }

    private void addImage(final int position, @NonNull TextImageEditorItemData data) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.base_text_image_editor_item_image, null);

        setupEditorView(position, view);

        addView(view, position);
        view.setTag(data);

        ImageView image_iv = view.findViewById(R.id.image_iv);
        EditText text_et = view.findViewById(R.id.text_et);
        String imageUrl = "";
        if (data != null) {
            if (!TextUtils.isEmpty(data.getLocalFilePath())) {
                imageUrl = "file://" + data.getLocalFilePath();
            } else if (!TextUtils.isEmpty(data.getRemoteFilePath())) {
                imageUrl = data.getRemoteFilePath();
            }
        }
        final String url = imageUrl;
        ImageLoaderManager.getInstance().loadImage(getContext(), imageUrl, image_iv);
        image_iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> urlList = new ArrayList<>();
                urlList.add(url);
                mHelper.displayUploadObject(urlList, 0);
            }
        });
        text_et.setText(data.getText());
        refreshImageState(view, data.getUploadState(), 0);
    }

    private void setupEditorView(int position, final View view) {
        if (position > mInitChildViewCount - 1) {
            view.findViewById(R.id.delete_iv).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtils.showConfirmDialog(getContext(),
                            getContext().getString(R.string.base_delete_content_confirm_msg),
                            new DialogUtils.IActionListener() {
                                @Override
                                public boolean onLeftBtnClick() {
                                    return false;
                                }

                                @Override
                                public boolean onRightBtnClick() {
                                    removeView(view);
                                    return false;
                                }
                            });
                }
            });
        }
        view.findViewById(R.id.add_text_btn_tv).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurAddNoteView = view;
                addText(indexOfChild(view) + 1, new TextImageEditorItemData(TYPE_TEXT), true);
            }
        });
        view.findViewById(R.id.add_image_btn_tv).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurAddNoteView = view;
                mHelper.selectUploadObjects();
            }
        });
    }

    private void refreshImageState(View view, FileUploadState state, int progress) {
        TextView loading_tv = view.findViewById(R.id.loading_tv);
        View result_tv = view.findViewById(R.id.result_tv);
        View state_rl = view.findViewById(R.id.state_rl);
        switch (state) {
            case UPLOAD_STATE_PREPARING:
                loading_tv.setText(getContext().getString(R.string.base_upload_preparing));
                loading_tv.setVisibility(VISIBLE);
                result_tv.setVisibility(GONE);
                state_rl.setVisibility(VISIBLE);
                break;
            case UPLOAD_STATE_IMAGE_COMPRESS:
                loading_tv.setText(getContext().getString(R.string.base_compressing));
                loading_tv.setVisibility(VISIBLE);
                result_tv.setVisibility(GONE);
                state_rl.setVisibility(VISIBLE);
                break;
            case UPLOAD_STATE_START:
            case UPLOAD_STATE_UPLOADING:
                loading_tv.setText(progress + "%");
                loading_tv.setVisibility(VISIBLE);
                result_tv.setVisibility(GONE);
                state_rl.setVisibility(VISIBLE);
                break;
            case UPLOAD_STATE_CANCEL:
            case UPLOAD_STATE_FAIL:
                loading_tv.setVisibility(GONE);
                result_tv.setVisibility(VISIBLE);
                state_rl.setVisibility(VISIBLE);
                break;
            case UPLOAD_STATE_SUCCESS:
            default:
                state_rl.setVisibility(GONE);
                break;
        }
    }

    public void setTitle(String title) {
        mTitle = title;
        ((TextView) mTopTitleView.findViewById(R.id.title_tv)).setText(mTitle);
        mTopTitleView.setVisibility(TextUtils.isEmpty(mTitle) ? GONE : VISIBLE);
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

    public List<TextImageEditorItemData> getValidImageDataList() {
        List<TextImageEditorItemData> imageItemUrlList = new ArrayList<>();
        if (getChildCount() > mInitChildViewCount) {
            for (int i = mInitChildViewCount; i < getChildCount(); i++) {
                TextImageEditorItemData data = (TextImageEditorItemData) getChildAt(i).getTag();
                if (data != null && TYPE_IMAGE.equals(data.getType()) &&
                        (data.getUploadState() != FileUploadState.UPLOAD_STATE_UPLOADING ||
                                data.getUploadState() == FileUploadState.UPLOAD_STATE_SUCCESS)) {
                    imageItemUrlList.add(data);
                }
            }
        }
        return imageItemUrlList;
    }

    public TextImageEntity getData() {
        TextImageEntity entity = new TextImageEntity();
        List<TextImageItemEntity> list = new ArrayList<>();
        int childCount = getChildCount();
        for (int i = mInitChildViewCount; i < childCount; i++) {
            View view = getChildAt(i);
            TextImageEditorItemData itemData = (TextImageEditorItemData) view.getTag();
            if (itemData != null) {
                EditText editText = view.findViewById(R.id.text_et);
                itemData.setText(editText.getText().toString());
                list.add(itemData);
            }
        }
        entity.setTitle(mTitle);
        entity.setItemList(list);
        return entity;
    }

    public void setData(@NonNull TextImageEntity data) {
        List<TextImageItemEntity> list = data.getItemList();
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            TextImageItemEntity itemData = list.get(i);
            if (itemData != null && itemData instanceof TextImageEditorItemData) {
                if (TYPE_TEXT.equals(itemData.getType())) {
                    addText(i + mInitChildViewCount, (TextImageEditorItemData) itemData, false);
                } else if (TYPE_IMAGE.equals(itemData.getType())) {
                    addImage(i + mInitChildViewCount, (TextImageEditorItemData) itemData);
                }
            }
        }
        invalidate();
    }

    @Override
    public int getUploadFileType() {
        return FileUploadComponent.TYPE_IMAGE;
    }

    @Override
    public int getValidFileCount() {
        List<TextImageEditorItemData> imageItemUrlList = getValidImageDataList();
        return imageItemUrlList == null ? 0 : imageItemUrlList.size();
    }

    @Override
    public void onFileUploadPrepare(List<FileUploadBean> uploadBeanList) {
        for (int i = 0; i < uploadBeanList.size(); i++) {
            FileUploadBean bean = uploadBeanList.get(i);
            TextImageEditorItemData data = new TextImageEditorItemData(TYPE_IMAGE);
            data.setUploadState(bean.getUploadState());
            data.setUploadProgress(bean.getUploadProgress());
            data.setLocalFilePath(bean.getLocalFilePath());
            data.setRemoteFilePath(bean.getRemoteFilePath());
            int viewIndex = indexOfChild(mCurAddNoteView) + 1 + i;
            addImage(viewIndex, data);
            bean.setAttachView(getChildAt(viewIndex));
        }
    }

    @Override
    public void onImageCompressProgress(FileUploadBean uploadBean, int compressPercentage) {
        if (uploadBean != null && uploadBean.getAttachView() != null) {
            refreshImageState(uploadBean.getAttachView(), uploadBean.getUploadState(),
                    compressPercentage);
        }
    }

    @Override
    public void onFileUploadStart(FileUploadBean uploadBean) {
        if (uploadBean != null && uploadBean.getAttachView() != null) {
            copyUploadData(uploadBean);
            refreshImageState(uploadBean.getAttachView(), uploadBean.getUploadState(),
                    uploadBean.getUploadProgress());
        }
    }

    @Override
    public void onFileUploadProgress(FileUploadBean uploadBean) {
        if (uploadBean != null && uploadBean.getAttachView() != null) {
            copyUploadData(uploadBean);
            refreshImageState(uploadBean.getAttachView(), uploadBean.getUploadState(),
                    uploadBean.getUploadProgress());
        }
    }

    @Override
    public void onFileUploadCancel(FileUploadBean uploadBean) {
        if (uploadBean != null && uploadBean.getAttachView() != null) {
            copyUploadData(uploadBean);
            refreshImageState(uploadBean.getAttachView(), uploadBean.getUploadState(),
                    uploadBean.getUploadProgress());
        }
    }

    @Override
    public void onFileUploadFail(FileUploadBean uploadBean, Exception exception) {
        if (uploadBean != null && uploadBean.getAttachView() != null) {
            copyUploadData(uploadBean);
            refreshImageState(uploadBean.getAttachView(), uploadBean.getUploadState(),
                    uploadBean.getUploadProgress());
        }
        String failMsg = ExceptionUtils.parseException(exception);
        if (!TextUtils.isEmpty(failMsg)) {
            Toast.makeText(getContext(), failMsg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFileUploadSuccess(FileUploadBean uploadBean) {
        if (uploadBean != null && uploadBean.getAttachView() != null) {
            copyUploadData(uploadBean);
            refreshImageState(uploadBean.getAttachView(), uploadBean.getUploadState(),
                    uploadBean.getUploadProgress());
        }
    }

    private void copyUploadData(FileUploadBean uploadBean) {
        Object obj = uploadBean.getAttachView().getTag();
        if (obj != null && obj instanceof TextImageEditorItemData) {
            TextImageEditorItemData data = (TextImageEditorItemData) obj;
            data.setUploadState(uploadBean.getUploadState());
            data.setUploadProgress(uploadBean.getUploadProgress());
            data.setLocalFilePath(uploadBean.getLocalFilePath());
            data.setRemoteFilePath(uploadBean.getRemoteFilePath());
        }
    }
}
