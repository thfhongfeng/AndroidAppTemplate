package com.pine.base.recycle_view;

import android.view.View;

import com.pine.base.recycle_view.bean.BaseListAdapterItemProperty;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public abstract class BaseListViewHolder<T> extends RecyclerView.ViewHolder {
    public BaseListViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void updateData(T content, BaseListAdapterItemProperty propertyEntity, int position);
}
