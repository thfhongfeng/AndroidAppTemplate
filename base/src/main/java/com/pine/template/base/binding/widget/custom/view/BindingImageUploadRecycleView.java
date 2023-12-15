package com.pine.template.base.binding.widget.custom.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import com.pine.template.base.component.uploader.ui.BaseImageUploadRecycleView;

public class BindingImageUploadRecycleView extends BaseImageUploadRecycleView {
    private InverseBindingListener bindingDataListener;
    private String imgUrlsJoinStr = ",";

    public BindingImageUploadRecycleView(Context context) {
        super(context);
    }

    public BindingImageUploadRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BindingImageUploadRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
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
    public static void setImgUrlsJoinStr(BindingImageUploadRecycleView view, String imgUrlsJoinStr) {
        view.imgUrlsJoinStr = imgUrlsJoinStr;
    }

    @BindingAdapter(value = {"imgUrls"})
    public static void setImgUrls(BindingImageUploadRecycleView view, String imgUrls) {
        view.setRemoteImages(imgUrls, view.imgUrlsJoinStr);
    }

    @InverseBindingAdapter(attribute = "imgUrls", event = "imgUrlsAttrChanged")
    public static Object getImgUrls(BindingImageUploadRecycleView view) {
        return view.getRemoteImages(view.imgUrlsJoinStr);
    }

    @BindingAdapter(value = {"imgUrlsAttrChanged"}, requireAll = false)
    public static void setImgUrlsAttrChanged(BindingImageUploadRecycleView view, InverseBindingListener listener) {
        view.bindingDataListener = listener;
    }
}
