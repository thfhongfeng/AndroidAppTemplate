package com.pine.base.component.uploader.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pine.base.R;
import com.pine.base.component.uploader.FileUploadComponent;
import com.pine.base.component.uploader.bean.PairFileUploadData;
import com.pine.base.util.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by tanghongfeng on 2019/9/1
 */

public class BasePairImageUploadView extends LinearLayout {
    private Activity mActivity;
    private FileUploadComponent.OneByOneUploadAdapter mAdapter;
    private LinearLayout mRoot;

    // 最大允许上传文件数
    protected int mMaxFileCount = 20;
    // 最大允许上传单个文件大小（单位K）
    protected int mMaxFileSize = 1024;
    private int mImageContainerWidth;
    private int mImageContainerHeight;

    private boolean mCanEdit = false;
    private int mStartRequestCode = 1000;
    private int mCurRequestCode = 1000;
    private Spanned mTipSpanned;
    private Spanned mAddSpanned;
    private Spanned mItemRightSpanned;
    private Spanned mItemLeftSpanned;

    private boolean mEnableImageScale = false; // 是否开启图片缩放功能
    private boolean mEnableImageTranslate = false; // 是否开启图片平移功能
    private boolean mEnableImageRotate = false; // 是否开启图片旋转功能

    public BasePairImageUploadView(Context context) {
        super(context);
    }

    public BasePairImageUploadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        resolveAttrs(context, attrs);
    }

    public BasePairImageUploadView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        resolveAttrs(context, attrs);
    }

    private void resolveAttrs(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BasePairImageUploadView);
        mMaxFileCount = typedArray.getInt(R.styleable.BasePairImageUploadView_base_maxFileCount, mMaxFileCount);
        mMaxFileSize = typedArray.getInt(R.styleable.BasePairImageUploadView_base_maxFileSize, mMaxFileSize);
        mImageContainerWidth = typedArray.getDimensionPixelOffset(R.styleable.BasePairImageUploadView_base_imageWidth, context.getResources().getDimensionPixelOffset(R.dimen.dp_147));
        mImageContainerHeight = typedArray.getDimensionPixelOffset(R.styleable.BasePairImageUploadView_base_imageHeight, context.getResources().getDimensionPixelOffset(R.dimen.dp_78));
        mEnableImageScale = typedArray.getBoolean(
                R.styleable.BasePairImageUploadView_base_enableImageScale, false);
        mEnableImageTranslate = typedArray.getBoolean(
                R.styleable.BasePairImageUploadView_base_enableImageTranslate, false);
        mEnableImageRotate = typedArray.getBoolean(
                R.styleable.BasePairImageUploadView_base_enableImageRotate, false);
        String tip = typedArray.getString(R.styleable.BasePairImageUploadView_base_bpiuv_tipText);
        if (!TextUtils.isEmpty(tip)) {
            mTipSpanned = Html.fromHtml(tip);
        }
        String add = typedArray.getString(R.styleable.BasePairImageUploadView_base_bpiuv_addText);
        if (!TextUtils.isEmpty(add)) {
            mAddSpanned = Html.fromHtml(add);
        } else {
            mAddSpanned = Html.fromHtml("增加");
        }
        String right = typedArray.getString(R.styleable.BasePairImageUploadView_base_bpiuv_rightItemText);
        if (!TextUtils.isEmpty(right)) {
            mItemRightSpanned = Html.fromHtml(right);
        }
        String left = typedArray.getString(R.styleable.BasePairImageUploadView_base_bpiuv_leftItemText);
        if (!TextUtils.isEmpty(left)) {
            mItemLeftSpanned = Html.fromHtml(left);
        }
    }

    public void init(@NonNull Activity activity) {
        init(activity, false, null, null, null, null);
    }

    public void init(@NonNull Activity activity, boolean canEdit) {
        mCanEdit = canEdit;
        initView(activity, null);
    }

    public void init(@NonNull Activity activity, boolean canEdit, Spanned tipSpanned,
                     Spanned addSpanned, Spanned itemLeftSpanned, Spanned itemRightSpanned) {
        mCanEdit = canEdit;
        setupSpanned(tipSpanned, addSpanned, itemLeftSpanned, itemRightSpanned);
        initView(activity, null);
    }

    public void init(@NonNull Activity activity, boolean canEdit,
                     @NonNull FileUploadComponent.OneByOneUploadAdapter adapter, int requestCode) {
        mCanEdit = canEdit;
        mStartRequestCode = requestCode;
        mCurRequestCode = requestCode;
        initView(activity, adapter);
    }

    public void init(@NonNull Activity activity, boolean canEdit,
                     @NonNull FileUploadComponent.OneByOneUploadAdapter adapter, int requestCode, Spanned tipSpanned,
                     Spanned addSpanned, Spanned itemLeftSpanned, Spanned itemRightSpanned) {
        mCanEdit = canEdit;
        mStartRequestCode = requestCode;
        mCurRequestCode = requestCode;
        setupSpanned(tipSpanned, addSpanned, itemLeftSpanned, itemRightSpanned);
        initView(activity, adapter);
    }

    private void setupSpanned(Spanned tipSpanned, Spanned addSpanned, Spanned itemLeftSpanned, Spanned itemRightSpanned) {
        mTipSpanned = tipSpanned;
        mAddSpanned = addSpanned == null ? Html.fromHtml("增加") : addSpanned;
        mItemLeftSpanned = itemLeftSpanned;
        mItemRightSpanned = itemRightSpanned;
    }

    private void initView(@NonNull Activity activity,
                          @NonNull FileUploadComponent.OneByOneUploadAdapter adapter) {
        setOrientation(VERTICAL);

        mActivity = activity;
        mAdapter = adapter;

        LayoutParams rootLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mRoot = new LinearLayout(getContext());
        mRoot.setLayoutParams(rootLayoutParams);
        mRoot.setOrientation(VERTICAL);
        addView(mRoot);

        final View view = LayoutInflater.from(getContext()).inflate(R.layout.base_pair_file_upload_bottom, null);
        TextView tip_tv = view.findViewById(R.id.tip_tv);
        TextView add_btn_tv = view.findViewById(R.id.add_btn_tv);
        if (mTipSpanned != null) {
            tip_tv.setText(mTipSpanned);
            tip_tv.setVisibility(VISIBLE);
        } else {
            tip_tv.setVisibility(GONE);
        }
        if (mAddSpanned != null) {
            add_btn_tv.setText(mAddSpanned);
            add_btn_tv.setVisibility(VISIBLE);
            add_btn_tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRoot.getChildCount() * 2 < mMaxFileCount - 1) {
                        addRow(null);
                    } else {
                        Toast.makeText(getContext(), "已达最大可上传数量", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            add_btn_tv.setVisibility(GONE);
        }
        addView(view);

        addRow(null);
    }

    private void addRow(PairFileUploadData data) {
        if (data == null) {
            data = new PairFileUploadData();
        }
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.base_pair_file_upload, null);
        setupRowView(view, data);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int dp10 = getResources().getDimensionPixelOffset(R.dimen.dp_10);
        layoutParams.setMargins(0, dp10, 0, dp10);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        mRoot.addView(view, layoutParams);
        data.setIndex(getChildCount() - 1);
        view.setTag(data);
    }

    private void setupRowView(final View view, @NonNull PairFileUploadData data) {
        BaseSingleImageUploadView left_sfuv = view.findViewById(R.id.left_sfuv);
        BaseSingleImageUploadView right_sfuv = view.findViewById(R.id.right_sfuv);
        ImageView delete_iv = view.findViewById(R.id.delete_iv);
        LinearLayout delete_ll = view.findViewById(R.id.delete_ll);

        left_sfuv.init(mActivity, mCanEdit, mAdapter, mCurRequestCode++, mItemLeftSpanned, data.getLeftData(), mMaxFileSize);
        left_sfuv.setImageWidthHeight(mImageContainerWidth, mImageContainerHeight);
        left_sfuv.enableImageScale(mEnableImageScale);
        left_sfuv.enableImageTranslate(mEnableImageTranslate);
        left_sfuv.enableImageRotate(mEnableImageRotate);
        right_sfuv.init(mActivity, mCanEdit, mAdapter, mCurRequestCode++, mItemRightSpanned, data.getRightData(), mMaxFileSize);
        right_sfuv.setImageWidthHeight(mImageContainerWidth, mImageContainerHeight);
        right_sfuv.enableImageScale(mEnableImageScale);
        right_sfuv.enableImageTranslate(mEnableImageTranslate);
        right_sfuv.enableImageRotate(mEnableImageRotate);

        LinearLayout.LayoutParams deleteLlLayoutParams = (LinearLayout.LayoutParams) delete_ll.getLayoutParams();
        deleteLlLayoutParams.height = mImageContainerHeight;
        delete_ll.setLayoutParams(deleteLlLayoutParams);

        if (mCanEdit) {
            delete_iv.setVisibility(VISIBLE);
            delete_iv.setOnClickListener(new OnClickListener() {
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
                                    mRoot.removeView(view);
                                    return false;
                                }
                            });
                }
            });
        } else {
            delete_iv.setVisibility(GONE);
        }
    }

    public int getUploadViewCount() {
        return mRoot == null ? 0 : mRoot.getChildCount();
    }

    public boolean checkDataComplete(boolean leftNeed, boolean rightNeed) {
        if (mRoot != null && mRoot.getChildCount() > 0) {
            int count = mRoot.getChildCount();
            for (int i = 0; i < count; i++) {
                BaseSingleImageUploadView left_sfuv = mRoot.getChildAt(i).findViewById(R.id.left_sfuv);
                BaseSingleImageUploadView right_sfuv = mRoot.getChildAt(i).findViewById(R.id.right_sfuv);
                if (left_sfuv != null && right_sfuv != null) {
                    String leftRemoteUrl = left_sfuv.getUploadedFileRemote();
                    String rightRemoteUrl = right_sfuv.getUploadedFileRemote();
                    if (leftNeed && TextUtils.isEmpty(leftRemoteUrl)) {
                        return false;
                    }
                    if (rightNeed && TextUtils.isEmpty(rightRemoteUrl)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public ArrayList<PairFileUploadData> getData() {
        ArrayList<PairFileUploadData> data = new ArrayList<>();
        if (mRoot != null && mRoot.getChildCount() > 0) {
            int count = mRoot.getChildCount();
            for (int i = 0; i < count; i++) {
                BaseSingleImageUploadView left_sfuv = mRoot.getChildAt(i).findViewById(R.id.left_sfuv);
                BaseSingleImageUploadView right_sfuv = mRoot.getChildAt(i).findViewById(R.id.right_sfuv);
                if (left_sfuv != null && right_sfuv != null) {
                    String leftRemoteUrl = left_sfuv.getUploadedFileRemote();
                    String rightRemoteUrl = right_sfuv.getUploadedFileRemote();
                    if (!TextUtils.isEmpty(leftRemoteUrl) || !TextUtils.isEmpty(rightRemoteUrl)) {
                        PairFileUploadData pairFileUploadData = new PairFileUploadData();
                        pairFileUploadData.setLeftData(left_sfuv.getUploadFileData());
                        pairFileUploadData.setRightData(right_sfuv.getUploadFileData());
                        data.add(pairFileUploadData);
                    }
                }
            }
        }
        return data;
    }

    public void setData(List<PairFileUploadData> list) {
        mRoot.removeAllViews();
        mCurRequestCode = mStartRequestCode;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                addRow(list.get(i));
            }
        }
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
}
