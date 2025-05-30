package com.pine.template.base.component.media_selector;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.pine.template.base.component.media_selector.bean.MediaBean;
import com.pine.template.base.component.media_selector.ui.ImageDisplayActivity;

import java.util.ArrayList;

/**
 * Created by tanghongfeng on 2018/11/2
 */

public class ImageViewer {
    public static final String INTENT_CUR_POSITION = "intent_cur_position";
    public static final String INTENT_SELECTED_IMAGE_LIST = "intent_selected_image_list";
    public static final String INTENT_MAX_SELECTED_COUNT = "intent_max_select_count";
    public static final String INTENT_IMAGE_BEAN_LIST = "intent_image_bean_list";
    public static final String INTENT_CAN_SELECT = "intent_can_select";
    public static final String INTENT_RETURN_TYPE = "intent_return_type";
    public static final String INTENT_ENABLE_IMAGE_SCALE = "intent_enable_image_scale";
    public static final String INTENT_ENABLE_IMAGE_TRANSLATE = "intent_enable_image_translate";
    public static final String INTENT_ENABLE_IMAGE_ROTATE = "intent_enable_image_rotate";

    public static final int DEFAULT_MAX_IMAGE_COUNT = 9;
    public static ArrayList<MediaBean> mBigOriginBeanData;
    private int mMaxCount = DEFAULT_MAX_IMAGE_COUNT;
    private int mCurPosition = 0;
    private boolean mCanSelect = false;
    private ArrayList<MediaBean> mOriginBeanData;
    private ArrayList<String> mSelectedData;
    private boolean mEnableImageScale = false; // 是否开启图片缩放功能
    private boolean mEnableImageTranslate = false; // 是否开启图片平移功能
    private boolean mEnableImageRotate = false; // 是否开启图片旋转功能

    private ImageViewer() {
        mBigOriginBeanData = null;
    }

    public static ImageViewer create() {
        return new ImageViewer();
    }

    public ImageViewer canSelect(boolean canSelect) {
        mCanSelect = canSelect;
        return this;
    }

    public ImageViewer position(int position) {
        mCurPosition = position;
        return this;
    }

    public ImageViewer count(int count) {
        mMaxCount = count;
        return this;
    }

    public ImageViewer origin(@NonNull ArrayList<String> images) {
        mOriginBeanData = new ArrayList<>();
        for (String each : images) {
            mOriginBeanData.add(MediaBean.buildImageBean(each));
        }
        return this;
    }

    public ImageViewer originBean(@NonNull ArrayList<MediaBean> beans) {
        mOriginBeanData = beans;
        return this;
    }

    public ImageViewer selected(ArrayList<String> images) {
        mSelectedData = images;
        return this;
    }

    public ImageViewer enableImageScale(boolean enable) {
        mEnableImageScale = enable;
        return this;
    }

    public ImageViewer enableImageTranslate(boolean enable) {
        mEnableImageTranslate = enable;
        return this;
    }

    public ImageViewer enableImageRotate(boolean enable) {
        mEnableImageRotate = enable;
        return this;
    }

    public void start(Activity activity) {
        if (mOriginBeanData != null || mSelectedData != null) {
            Intent intent = new Intent(activity, ImageDisplayActivity.class);
            intent.putExtra(INTENT_CAN_SELECT, mCanSelect);
            if (mCanSelect) {
                intent.putExtra(INTENT_MAX_SELECTED_COUNT, mMaxCount);
                intent.putStringArrayListExtra(INTENT_SELECTED_IMAGE_LIST, mSelectedData);
            }
            if (mOriginBeanData != null && mOriginBeanData.size() < 200) {
                intent.putExtra(INTENT_IMAGE_BEAN_LIST, mOriginBeanData);
                mBigOriginBeanData = null;
            } else {
                mBigOriginBeanData = mOriginBeanData;
                mOriginBeanData = null;
            }
            intent.putExtra(INTENT_CUR_POSITION, mCurPosition);
            intent.putExtra(INTENT_ENABLE_IMAGE_SCALE, mEnableImageScale);
            intent.putExtra(INTENT_ENABLE_IMAGE_TRANSLATE, mEnableImageTranslate);
            intent.putExtra(INTENT_ENABLE_IMAGE_ROTATE, mEnableImageRotate);
            activity.startActivity(intent);
        }
    }

    public void start(Activity activity, int requestCode) {
        if (mOriginBeanData != null || mSelectedData != null) {
            Intent intent = new Intent(activity, ImageDisplayActivity.class);
            intent.putExtra(INTENT_CAN_SELECT, mCanSelect);
            if (mCanSelect) {
                intent.putExtra(INTENT_MAX_SELECTED_COUNT, mMaxCount);
                intent.putStringArrayListExtra(INTENT_SELECTED_IMAGE_LIST, mSelectedData);
            }
            if (mOriginBeanData != null && mOriginBeanData.size() < 200) {
                intent.putExtra(INTENT_IMAGE_BEAN_LIST, mOriginBeanData);
                mBigOriginBeanData = null;
            } else {
                mBigOriginBeanData = mOriginBeanData;
                mOriginBeanData = null;
            }
            intent.putExtra(INTENT_CUR_POSITION, mCurPosition);
            intent.putExtra(INTENT_ENABLE_IMAGE_SCALE, mEnableImageScale);
            intent.putExtra(INTENT_ENABLE_IMAGE_TRANSLATE, mEnableImageTranslate);
            intent.putExtra(INTENT_ENABLE_IMAGE_ROTATE, mEnableImageRotate);
            activity.startActivityForResult(intent, requestCode);
        }
    }
}
