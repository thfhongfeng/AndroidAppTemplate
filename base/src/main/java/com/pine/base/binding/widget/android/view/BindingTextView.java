package com.pine.base.binding.widget.android.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

public class BindingTextView extends AppCompatTextView {
    private InverseBindingListener bindingDataListener;
    private InverseBindingListener bindingData1Listener,
            bindingData2Listener, bindingData3Listener;
    private Object data, data1, data2, data3;

    public BindingTextView(Context context) {
        super(context);
    }

    public BindingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BindingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setData(final Object data) {
        this.data = data;
        if (bindingDataListener != null) {
            bindingDataListener.onChange();
        }
    }

    public Object getData() {
        return data;
    }

    @BindingAdapter(value = {"data"}, requireAll = false)
    public static void setData(BindingTextView view, Object data) {
        if (data != view.getData()) {
            view.setData(data);
        }
    }

    @InverseBindingAdapter(attribute = "data", event = "dataAttrChanged")
    public static Object getData(BindingTextView view) {
        return view.getData();
    }

    @BindingAdapter(value = {"dataAttrChanged"}, requireAll = false)
    public static void setDataAttrChanged(BindingTextView view, InverseBindingListener listener) {
        view.bindingDataListener = listener;
    }

    public void setData1(final Object data1) {
        this.data1 = data1;
        if (bindingData1Listener != null) {
            bindingData1Listener.onChange();
        }
    }

    public Object getData1() {
        return data1;
    }

    @BindingAdapter(value = {"data1"}, requireAll = false)
    public static void setData1(BindingTextView view, Object data1) {
        if (data1 != view.getData1()) {
            view.setData1(data1);
        }
    }

    @InverseBindingAdapter(attribute = "data1", event = "data1AttrChanged")
    public static Object getData1(BindingTextView view) {
        return view.getData1();
    }

    @BindingAdapter(value = {"data1AttrChanged"}, requireAll = false)
    public static void setData1AttrChanged(BindingTextView view, InverseBindingListener listener) {
        view.bindingData1Listener = listener;
    }

    public void setData2(final Object data2) {
        this.data2 = data2;
        if (bindingData2Listener != null) {
            bindingData2Listener.onChange();
        }
    }

    public Object getData2() {
        return data2;
    }

    @BindingAdapter(value = {"data2"}, requireAll = false)
    public static void setData2(BindingTextView view, Object data2) {
        if (data2 != view.getData2()) {
            view.setData2(data2);
        }
    }

    @InverseBindingAdapter(attribute = "data2", event = "data2AttrChanged")
    public static Object getData2(BindingTextView view) {
        return view.getData2();
    }

    @BindingAdapter(value = {"data2AttrChanged"}, requireAll = false)
    public static void setData2AttrChanged(BindingTextView view, InverseBindingListener listener) {
        view.bindingData2Listener = listener;
    }

    public void setData3(final Object data3) {
        this.data3 = data3;
        if (bindingData3Listener != null) {
            bindingData3Listener.onChange();
        }
    }

    public Object getData3() {
        return data3;
    }

    @BindingAdapter(value = {"data3"}, requireAll = false)
    public static void setData3(BindingTextView view, Object data3) {
        if (data3 != view.getData3()) {
            view.setData3(data3);
        }
    }

    @InverseBindingAdapter(attribute = "data3", event = "data3AttrChanged")
    public static Object getData3(BindingTextView view) {
        return view.getData3();
    }

    @BindingAdapter(value = {"data3AttrChanged"}, requireAll = false)
    public static void setData3AttrChanged(BindingTextView view, InverseBindingListener listener) {
        view.bindingData3Listener = listener;
    }
}
