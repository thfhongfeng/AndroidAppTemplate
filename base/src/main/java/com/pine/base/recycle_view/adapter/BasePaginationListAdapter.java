package com.pine.base.recycle_view.adapter;

import android.support.annotation.LayoutRes;
import android.view.ViewGroup;

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
    protected List<BaseListAdapterItemEntity<T>> mData = new ArrayList<>();

    public BasePaginationListAdapter() {

    }

    public BasePaginationListAdapter(int defaultItemViewType) {
        super(defaultItemViewType);
    }

    public final void setPage(int startPageNo, int pageSize) {
        mPageNo = new AtomicInteger(startPageNo);
        mPageSize = new AtomicInteger(pageSize);
    }

    public final void enableEmptyMoreComplete(boolean enableEmptyView, boolean enableMoreView,
                                              boolean enableCompleteView) {
        super.enableEmptyMoreComplete(enableEmptyView, enableMoreView,
                enableCompleteView, false);
    }

    public final void enableEmptyMoreComplete(boolean enableEmptyView, boolean enableMoreView,
                                              boolean enableCompleteView, boolean enableErrorView) {
        super.enableEmptyMoreComplete(enableEmptyView, enableMoreView,
                enableCompleteView, enableErrorView);
    }

    @Override
    public void onBindViewHolder(BaseListViewHolder holder, int position) {
        if (isHeadView(position)) {
            holder.updateData("", new BaseListAdapterItemProperty(), position);
            return;
        }
        if (isErrorAllView(position) || isErrorMoreView(position) || isEmptyView(position) ||
                isMoreView(position) || isCompleteView(position)) {
            holder.updateData("", new BaseListAdapterItemProperty(), 0);
            return;
        }
        int dataIndex = position - getHeadViewCount();
        holder.updateData(mData.get(dataIndex).getData(), mData.get(dataIndex).getPropertyEntity(), dataIndex);
    }

    @Override
    public int getItemCount() {
        onNoDataItemState(false);
        int headOffset = getHeadViewCount();
        if (mEnableInitState) {
            return 0 + headOffset;
        }
        if (showEmptyView() || showErrorAllView()) {
            onNoDataItemState(true);
            return 1 + headOffset;
        }
        int actualSize = mData == null ? 0 : mData.size();
        if (showMoreView() || showCompleteView() || showErrorMoreView()) {
            return actualSize + 1 + headOffset;
        }
        return actualSize + headOffset;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeadView(position)) {
            return HEAD_VIEW_HOLDER;
        }
        if (isErrorAllView(position)) {
            return ERROR_ALL_VIEW_HOLDER;
        }
        if (isErrorMoreView(position)) {
            return ERROR_MORE_VIEW_HOLDER;
        }
        if (isEmptyView(position)) {
            return EMPTY_BACKGROUND_VIEW_HOLDER;
        }
        if (isMoreView(position)) {
            return MORE_VIEW_HOLDER;
        }
        if (isCompleteView(position)) {
            return COMPLETE_VIEW_HOLDER;
        }
        int dataIndex = position - getHeadViewCount();
        BaseListAdapterItemEntity itemEntity = mData.get(dataIndex);
        return itemEntity != null && itemEntity.getPropertyEntity().getItemViewType() != DEFAULT_VIEW_HOLDER ?
                itemEntity.getPropertyEntity().getItemViewType() : getDefaultItemViewType();
    }

    private boolean showEmptyView() {
        return !isErrorViewState() && isEmptyViewEnabled() && (mData == null || mData.size() == 0);
    }

    private boolean showMoreView() {
        return !isErrorViewState() && isMoreViewEnabled() && mHasMore && mData != null && mData.size() != 0;
    }

    private boolean showCompleteView() {
        return !isErrorViewState() && isCompleteViewEnabled() && !mHasMore && mData != null && mData.size() != 0;
    }

    private boolean showErrorMoreView() {
        return isErrorViewState() && mHasMore && mData != null && mData.size() != 0;
    }

    private boolean showErrorAllView() {
        return isErrorViewState() && (mData == null || mData.size() == 0);
    }

    private boolean isEmptyView(int position) {
        return showEmptyView() && position == (0 + getHeadViewCount());
    }

    private boolean isMoreView(int position) {
        return showMoreView() && position != 0 && position == (mData.size() + getHeadViewCount());
    }

    private boolean isCompleteView(int position) {
        return showCompleteView() && position != 0 && position == (mData.size() + getHeadViewCount());
    }

    private boolean isErrorAllView(int position) {
        return showErrorAllView() && position == (0 + getHeadViewCount());
    }

    private boolean isErrorMoreView(int position) {
        return showErrorMoreView() && position != 0 && position == (mData.size() + getHeadViewCount());
    }

    public final void addData(List<T> newData) {
        onDataAdd();
        mIsErrorState = false;
        List<BaseListAdapterItemEntity<T>> parseData = parseData(newData, false);
        if (parseData == null || parseData.size() == 0) {
            mHasMore = false;
            notifyDataSetChangedSafely();
            return;
        }
        if (mData == null) {
            mEnableInitState = false;
            mData = parseData;
            resetAndGetPageNo();
        } else {
            for (int i = 0; i < parseData.size(); i++) {
                mData.add(parseData.get(i));
            }
            mPageNo.incrementAndGet();
        }
        mHasMore = parseData.size() >= getPageSize();
        notifyDataSetChangedSafely();
    }

    public final void setData(List<T> data) {
        onDataSet();
        mIsErrorState = false;
        mEnableInitState = false;
        mData = parseData(data, true);
        resetAndGetPageNo();
        mHasMore = mData != null && mData.size() >= getPageSize();
        notifyDataSetChangedSafely();
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

    public boolean isMoreViewEnabled() {
        return super.isMoreViewEnabled();
    }

    public void setErrorMoreLayoutId(@LayoutRes int layoutResId) {
        super.setErrorMoreLayoutId(layoutResId);
    }

    public BaseListViewHolder<String> getErrorMoreViewHolder(ViewGroup parent) {
        return super.getErrorMoreViewHolder(parent);
    }
}
