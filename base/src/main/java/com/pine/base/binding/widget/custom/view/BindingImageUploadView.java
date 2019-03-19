package com.pine.base.binding.widget.custom.view;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.pine.base.component.uploader.ui.ImageUploadView;

public class BindingImageUploadView extends ImageUploadView {
    private InverseBindingListener bindingDataListener;
    private String imgUrlsJoinStr = ",";

    public BindingImageUploadView(Context context) {
        super(context);
    }

    public BindingImageUploadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BindingImageUploadView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void notifyAdapterDataChanged() {
        super.notifyAdapterDataChanged();
        if (bindingDataListener != null) {
            bindingDataListener.onChange();
        }
    }

    protected void notifyAdapterItemChanged(int index) {
        super.notifyAdapterItemChanged(index);
        if (bindingDataListener != null) {
            bindingDataListener.onChange();
        }
    }

    protected void notifyAdapterItemRangeChanged(int start, int end) {
        super.notifyAdapterItemRangeChanged(start, end);
        if (bindingDataListener != null) {
            bindingDataListener.onChange();
        }
    }

    @BindingAdapter(value = {"imgUrlsJoinStr"})
    public static void setImgUrlsJoinStr(BindingImageUploadView view, String imgUrlsJoinStr) {
        view.imgUrlsJoinStr = imgUrlsJoinStr;
    }

    @BindingAdapter(value = {"newAddImgUrls"})
    public static void setNewAddImgUrls(BindingImageUploadView view, String newAddImgUrls) {

    }

    @InverseBindingAdapter(attribute = "newAddImgUrls", event = "newAddImgUrlsAttrChanged")
    public static Object getNewAddImgUrls(BindingImageUploadView view) {
        return view.getNewUploadImageRemoteString(view.imgUrlsJoinStr);
    }

    @BindingAdapter(value = {"newAddImgUrlsAttrChanged"}, requireAll = false)
    public static void setNewAddImgUrlsAttrChanged(BindingImageUploadView view, InverseBindingListener listener) {
        view.bindingDataListener = listener;
    }

    @BindingAdapter(value = {"initImgUrls"})
    public static void setInitImgUrls(BindingImageUploadView view, String initImgUrls) {
        view.setRemoteImages(initImgUrls, view.imgUrlsJoinStr);
    }
}
