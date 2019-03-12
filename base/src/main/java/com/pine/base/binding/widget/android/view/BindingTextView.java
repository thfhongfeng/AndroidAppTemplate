package com.pine.base.binding.widget.android.view;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class BindingTextView extends AppCompatTextView {
    private static InverseBindingListener mBindingDataListener;
    private static InverseBindingListener mBindingData1Listener,
            mBindingData2Listener, mBindingData3Listener;
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
        if (mBindingDataListener != null) {
            mBindingDataListener.onChange();
        }
    }

    public Object getData() {
        return data;
    }

    @BindingAdapter(value = {"bind:data"}, requireAll = false)
    public static void setData(BindingTextView view, Object data) {
        if (data != view.getData()) {
            view.setData(data);
        }
    }

    @InverseBindingAdapter(attribute = "bind:data", event = "bind:dataAttrChanged")
    public static Object getData(BindingTextView view) {
        return view.getData();
    }

    @BindingAdapter(value = {"bind:dataAttrChanged"}, requireAll = false)
    public static void setDataAttrChanged(BindingTextView view, InverseBindingListener listener) {
        mBindingDataListener = listener;
    }

    public void setData1(final Object data1) {
        this.data1 = data1;
        if (mBindingData1Listener != null) {
            mBindingData1Listener.onChange();
        }
    }

    public Object getData1() {
        return data1;
    }

    @BindingAdapter(value = {"bind:data1"}, requireAll = false)
    public static void setData1(BindingTextView view, Object data1) {
        if (data1 != view.getData1()) {
            view.setData1(data1);
        }
    }

    @InverseBindingAdapter(attribute = "bind:data1", event = "bind:data1AttrChanged")
    public static Object getData1(BindingTextView view) {
        return view.getData1();
    }

    @BindingAdapter(value = {"bind:data1AttrChanged"}, requireAll = false)
    public static void setData1AttrChanged(BindingTextView view, InverseBindingListener listener) {
        mBindingData1Listener = listener;
    }

    public void setData2(final Object data2) {
        this.data2 = data2;
        if (mBindingData2Listener != null) {
            mBindingData2Listener.onChange();
        }
    }

    public Object getData2() {
        return data2;
    }

    @BindingAdapter(value = {"bind:data2"}, requireAll = false)
    public static void setData2(BindingTextView view, Object data2) {
        if (data2 != view.getData2()) {
            view.setData2(data2);
        }
    }

    @InverseBindingAdapter(attribute = "bind:data2", event = "bind:data2AttrChanged")
    public static Object getData2(BindingTextView view) {
        return view.getData2();
    }

    @BindingAdapter(value = {"bind:data2AttrChanged"}, requireAll = false)
    public static void setData2AttrChanged(BindingTextView view, InverseBindingListener listener) {
        mBindingData2Listener = listener;
    }

    public void setData3(final Object data3) {
        this.data3 = data3;
        if (mBindingData3Listener != null) {
            mBindingData3Listener.onChange();
        }
    }

    public Object getData3() {
        return data3;
    }

    @BindingAdapter(value = {"bind:data3"}, requireAll = false)
    public static void setData3(BindingTextView view, Object data3) {
        if (data3 != view.getData3()) {
            view.setData3(data3);
        }
    }

    @InverseBindingAdapter(attribute = "bind:data3", event = "bind:data3AttrChanged")
    public static Object getData3(BindingTextView view) {
        return view.getData3();
    }

    @BindingAdapter(value = {"bind:data3AttrChanged"}, requireAll = false)
    public static void setData3AttrChanged(BindingTextView view, InverseBindingListener listener) {
        mBindingData3Listener = listener;
    }
}
