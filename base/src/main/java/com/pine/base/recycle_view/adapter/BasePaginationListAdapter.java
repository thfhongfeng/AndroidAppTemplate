package com.pine.base.recycle_view.adapter;

import com.pine.base.recycle_view.BaseListViewHolder;
import com.pine.base.recycle_view.bean.BaseListAdapterItemEntity;
import com.pine.base.recycle_view.bean.BaseListAdapterItemProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public abstract class BasePaginationListAdapter<T> extends BaseListAdapter {
    // 1: 表示第一页（计数从1开始）
    protected AtomicInteger mPageNo = new AtomicInteger(1);
    protected AtomicInteger mPageSize = new AtomicInteger(10);
    protected Boolean mHasMore = true;
    protected List<BaseListAdapterItemEntity<T>> mData = null;
    private boolean mIsInitState = true;

    public BasePaginationListAdapter(int defaultItemViewType) {
        super(defaultItemViewType);
    }

    @Override
    public void onBindViewHolder(BaseListViewHolder holder, int position) {
        if (mData == null || mData.size() == 0) {
            holder.updateData("", new BaseListAdapterItemProperty(), position);
            return;
        }
        if (isMoreView(position)) {
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
        if (hasMoreView() || hasCompleteView()) {
            return actualSize + 1;
        }
        return actualSize;
    }

    @Override
    public int getItemViewType(int position) {
        if (mData == null || mData.size() == 0) {
            return EMPTY_BACKGROUND_VIEW_HOLDER;
        }
        if (isMoreView(position)) {
            return MORE_VIEW_HOLDER;
        }
        if (isCompleteView(position)) {
            return COMPLETE_VIEW_HOLDER;
        }
        BaseListAdapterItemEntity itemEntity = mData.get(position);
        return itemEntity != null && itemEntity.getPropertyEntity().getItemViewType() != -10000 ?
                itemEntity.getPropertyEntity().getItemViewType() : getDefaultItemViewType();
    }

    private boolean hasEmptyView() {
        return isEmptyViewSetup() && (mData == null || mData.size() == 0);
    }

    private boolean hasMoreView() {
        return isMoreViewSetup() && mHasMore && mData != null && mData.size() != 0;
    }

    private boolean hasCompleteView() {
        return isCompleteViewSetup() && !mHasMore && mData != null && mData.size() != 0;
    }

    private boolean isMoreView(int position) {
        return isMoreViewSetup() && mHasMore && position != 0 && position == mData.size();
    }

    private boolean isCompleteView(int position) {
        return isCompleteViewSetup() && !mHasMore && position != 0 && position == mData.size();
    }

    public final void addData(List<T> newData) {
        List<BaseListAdapterItemEntity<T>> parseData = parseData(newData, false);
        if (parseData == null || parseData.size() == 0) {
            mHasMore = false;
            notifyDataSetChanged();
            return;
        }
        if (mData == null) {
            mIsInitState = false;
            mData = parseData;
            resetAndGetPageNo();
        } else {
            for (int i = 0; i < parseData.size(); i++) {
                mData.add(parseData.get(i));
            }
            mPageNo.incrementAndGet();
        }
        mHasMore = parseData.size() >= getPageSize();
        notifyDataSetChanged();
    }

    public final void setData(List<T> data) {
        mIsInitState = false;
        mData = parseData(data, true);
        resetAndGetPageNo();
        mHasMore = mData != null && mData.size() >= getPageSize();
        notifyDataSetChanged();
    }

    protected List<BaseListAdapterItemEntity<T>> parseData(List<T> data, boolean reset) {
        List<BaseListAdapterItemEntity<T>> adapterData = new ArrayList<>();
        if (data != null) {
            BaseListAdapterItemEntity adapterEntity;
            for (int i = 0; i < data.size(); i++) {
                adapterEntity = new BaseListAdapterItemEntity();
                adapterEntity.setData(data.get(i));
                adapterEntity.getPropertyEntity().setItemViewType(getDefaultItemViewType());
                adapterData.add(adapterEntity);
            }
        }
        return adapterData;
    }

    public List<BaseListAdapterItemEntity<T>> getAdapterData() {
        return mData;
    }

    public void resetAndGetPageNo() {
        mPageNo.set(1);
    }

    public int getPageNo() {
        return mPageNo.get();
    }

    public int getNextPageNo() {
        return mPageNo.get() + 1;
    }

    public int getPageSize() {
        return mPageSize.get();
    }

    public final boolean mIsInitState() {
        return mIsInitState;
    }
}