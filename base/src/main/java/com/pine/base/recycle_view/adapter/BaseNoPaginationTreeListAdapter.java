package com.pine.base.recycle_view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.pine.base.recycle_view.BaseListViewHolder;
import com.pine.base.recycle_view.bean.BaseListAdapterItemEntity;
import com.pine.base.recycle_view.bean.BaseListAdapterItemProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public abstract class BaseNoPaginationTreeListAdapter<T> extends BaseListAdapter {
    protected List<BaseListAdapterItemEntity<T>> mData = new ArrayList<>();

    public BaseNoPaginationTreeListAdapter() {

    }

    public BaseNoPaginationTreeListAdapter(int defaultItemViewType) {
        super(defaultItemViewType);
    }

    public final void enableEmptyComplete(boolean enableEmptyView,
                                          boolean enableCompleteView) {
        super.enableEmptyMoreComplete(enableEmptyView, false,
                enableCompleteView, false);
    }

    public void enableEmptyMoreComplete(boolean enableEmptyView,
                                        boolean enableCompleteView, boolean enableErrorView) {
        super.enableEmptyMoreComplete(enableEmptyView, false, enableCompleteView, enableErrorView);
    }

    @Override
    public void onBindViewHolder(BaseListViewHolder holder, int position) {
        if (isHeadView(position)) {
            holder.updateData("", new BaseListAdapterItemProperty(), position);
            return;
        }
        if (isErrorView(position) || isEmptyView(position) || isCompleteView(position)) {
            holder.updateData("", new BaseListAdapterItemProperty(), 0);
            return;
        }
        int dataIndex = position - getHeadViewCount();
        holder.updateData(mData.get(dataIndex).getData(), mData.get(dataIndex).getPropertyEntity(), dataIndex);
    }

    @Override
    public int getItemCount() {
        int headOffset = getHeadViewCount();
        if (mEnableInitState) {
            return 0 + headOffset;
        }
        if (showEmptyView() || isErrorViewState()) {
            return 1 + headOffset;
        }
        int actualSize = mData == null ? 0 : mData.size();
        if (showCompleteView()) {
            return actualSize + 1 + headOffset;
        }
        return actualSize + headOffset;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeadView(position)) {
            return HEAD_VIEW_HOLDER;
        }
        if (isErrorView(position)) {
            return ERROR_ALL_VIEW_HOLDER;
        }
        if (isEmptyView(position)) {
            return EMPTY_BACKGROUND_VIEW_HOLDER;
        }
        if (isCompleteView(position)) {
            return COMPLETE_VIEW_HOLDER;
        }
        int dataIndex = position - getHeadViewCount();
        BaseListAdapterItemEntity itemEntity = mData.get(dataIndex);
        return itemEntity != null && itemEntity.getPropertyEntity().getItemViewType() != DEFAULT_VIEW_HOLDER ?
                itemEntity.getPropertyEntity().getItemViewType() : getDefaultItemViewType();
    }

    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = null;
    }

    private boolean showEmptyView() {
        return !isErrorViewState() && isEmptyViewEnabled() && (mData == null || mData.size() == 0);
    }

    private boolean showCompleteView() {
        return !isErrorViewState() && isCompleteViewEnabled() && mData != null && mData.size() != 0;
    }

    private boolean isEmptyView(int position) {
        return showEmptyView() && position == (0 + getHeadViewCount());
    }

    private boolean isCompleteView(int position) {
        return showCompleteView() && position != 0 && position == (mData.size() + getHeadViewCount());
    }

    private boolean isErrorView(int position) {
        return isErrorViewState() && position == (0 + getHeadViewCount());
    }

    public final void setData(List<T> data) {
        onDataSet();
        mIsErrorState = false;
        mEnableInitState = false;
        mData = parseTreeData(data, true);
        notifyDataSetChangedSafely();
    }

    public final void addData(List<T> newData) {
        mIsErrorState = false;
        List<BaseListAdapterItemEntity<T>> parseData = parseTreeData(newData, false);
        if (parseData == null || parseData.size() == 0) {
            notifyDataSetChangedSafely();
            return;
        }
        if (mData == null) {
            mEnableInitState = false;
            mData = parseData;
        } else {
            for (int i = 0; i < parseData.size(); i++) {
                mData.add(parseData.get(i));
            }
        }
        notifyDataSetChangedSafely();
    }

    public List<BaseListAdapterItemEntity<T>> getAdapterData() {
        return mData;
    }

    public abstract List<BaseListAdapterItemEntity<T>> parseTreeData(List<T> data, boolean reset);
}
