package com.pine.base.recycle_view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.pine.base.recycle_view.BaseListViewHolder;
import com.pine.base.recycle_view.bean.BaseListAdapterItemEntity;
import com.pine.base.recycle_view.bean.BaseListAdapterItemProperty;

import java.util.List;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public abstract class BaseNoPaginationTreeListAdapter<T> extends BaseListAdapter {
    protected List<BaseListAdapterItemEntity<T>> mData = null;
    private boolean mIsInitState = true;
    private int mTreeListType = -1;

    public BaseNoPaginationTreeListAdapter(int treeListType) {
        super(EMPTY_BACKGROUND_VIEW_HOLDER);
        mTreeListType = treeListType;
    }

    public void showEmptyComplete(boolean showEmptyView, boolean showCompleteView) {
        super.showEmptyMoreComplete(showEmptyView, false, showCompleteView);
    }

    @Override
    public void onBindViewHolder(BaseListViewHolder holder, int position) {
        if (mData == null || mData.size() == 0) {
            holder.updateData("", new BaseListAdapterItemProperty(), position);
            return;
        }
        if (isCompleteView(position)) {
            holder.updateData("", new BaseListAdapterItemProperty(), position);
            return;
        }
        holder.updateData(mData.get(position).getData(), mData.get(position).getPropertyEntity(), position);
    }

    @Override
    public int getItemCount() {
        if (mIsInitState()) {
            return 0;
        }
        if (hasEmptyView()) {
            return 1;
        }
        int actualSize = mData.size();
        if (hasCompleteView()) {
            return actualSize + 1;
        }
        return actualSize;
    }

    @Override
    public int getItemViewType(int position) {
        if (mData == null || mData.size() == 0) {
            return EMPTY_BACKGROUND_VIEW_HOLDER;
        }
        if (isCompleteView(position)) {
            return COMPLETE_VIEW_HOLDER;
        }
        BaseListAdapterItemEntity itemEntity = mData.get(position);
        return itemEntity.getPropertyEntity().getItemViewType();
    }

    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = null;
    }

    private boolean hasEmptyView() {
        return isEmptyViewSetup() && (mData == null || mData.size() == 0);
    }

    private boolean hasCompleteView() {
        return isCompleteViewSetup() && mData != null && mData.size() != 0;
    }

    private boolean isCompleteView(int position) {
        return isCompleteViewSetup() && position != 0 && position == mData.size();
    }

    public final void setData(List<T> data) {
        mIsInitState = false;
        mData = parseTreeData(data, true);
        notifyDataSetChanged();
    }

    public final void addData(List<T> newData) {
        List<BaseListAdapterItemEntity<T>> parseData = parseTreeData(newData, false);
        if (parseData == null || parseData.size() == 0) {
            return;
        }
        if (mData == null) {
            mIsInitState = false;
            mData = parseData;
        } else {
            for (int i = 0; i < parseData.size(); i++) {
                mData.add(parseData.get(i));
            }
        }
        notifyDataSetChanged();
    }

    public List<BaseListAdapterItemEntity<T>> getAdapterData() {
        return mData;
    }

    public int getTreeListType() {
        return mTreeListType;
    }

    public final boolean mIsInitState() {
        return mIsInitState;
    }

    public abstract List<BaseListAdapterItemEntity<T>> parseTreeData(List<T> data, boolean reset);
}
