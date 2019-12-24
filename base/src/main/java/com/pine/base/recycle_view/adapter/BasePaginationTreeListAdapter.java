package com.pine.base.recycle_view.adapter;

import android.view.ViewGroup;

import androidx.annotation.LayoutRes;

import com.pine.base.recycle_view.BaseListViewHolder;
import com.pine.base.recycle_view.bean.BaseListAdapterItemEntity;
import com.pine.base.recycle_view.bean.BaseListAdapterItemProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public abstract class BasePaginationTreeListAdapter<T> extends BaseListAdapter {
    protected List<T> mOriginData;
    protected List<BaseListAdapterItemEntity<T>> mData = new ArrayList<>();
    // 1: 表示第一页（计数从1开始）
    protected AtomicInteger mPageNo = new AtomicInteger(1);
    protected AtomicInteger mPageSize = new AtomicInteger(10);
    protected Boolean mHasMore = true;

    public BasePaginationTreeListAdapter() {

    }

    public final void setPage(int startPageNo, int pageSize) {
        mPageNo = new AtomicInteger(startPageNo);
        mPageSize = new AtomicInteger(pageSize);
    }

    public final void enableEmptyMoreComplete(boolean enableEmptyView, boolean enableMoreView,
                                              boolean enableCompleteView) {
        super.enableEmptyMoreCompleteError(enableEmptyView, enableMoreView,
                enableCompleteView, false);
    }

    @Override
    public void onBindViewHolder(BaseListViewHolder holder, int position) {
        if (isHeadView(position) || isInitLoadingView(position) || isErrorAllView(position) || isErrorMoreView(position) ||
                isEmptyView(position) || isMoreView(position) || isCompleteView(position)) {
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
        setNoDataItemState(false);
        int headOffset = getHeadViewCount();
        if (showInitLoadingView() || showEmptyView() || showErrorAllView()) {
            setNoDataItemState(true);
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
        if (isInitLoadingView(position)) {
            return INIT_LOADING_VIEW_HOLDER;
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
                itemEntity.getPropertyEntity().getItemViewType() : getOriginDataItemViewType(dataIndex);
    }

    private boolean showInitLoadingView() {
        return isInitLoadingViewState() && !isErrorViewState() && (mData == null || mData.size() == 0);
    }

    private boolean showEmptyView() {
        return !isInitLoadingViewState() && !isErrorViewState() && isEmptyViewEnabled() && (mData == null || mData.size() == 0);
    }

    private boolean showMoreView() {
        return !isInitLoadingViewState() && !isErrorViewState() && isMoreViewEnabled() && mHasMore && mData != null && mData.size() != 0;
    }

    private boolean showCompleteView() {
        return !isInitLoadingViewState() && !isErrorViewState() && isCompleteViewEnabled() && !mHasMore && mData != null && mData.size() != 0;
    }

    private boolean showErrorAllView() {
        return !isInitLoadingViewState() && isErrorViewState() && (mData == null || mData.size() == 0);
    }

    private boolean showErrorMoreView() {
        return !isInitLoadingViewState() && isErrorViewState() && mHasMore && mData != null && mData.size() != 0;
    }

    private boolean isInitLoadingView(int position) {
        return showInitLoadingView() && position == (0 + getHeadViewCount());
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

    public final void setData(List<T> data) {
        mOriginData = data;
        mData = parseTreeData(data, true);
        resetAndGetPageNo();
        mHasMore = mData != null && mData.size() >= getPageSize();
        onDataSet();
        notifyDataSetChangedSafely();
    }

    public final void addData(List<T> newData) {
        List<BaseListAdapterItemEntity<T>> parseData = parseTreeData(newData, false);
        if (parseData == null || parseData.size() == 0) {
            mHasMore = false;
            notifyDataSetChangedSafely();
            return;
        }
        if (mOriginData == null) {
            mOriginData = newData;
        } else {
            mOriginData.addAll(newData);
        }
        if (mData == null) {
            mData = parseData;
            resetAndGetPageNo();
        } else {
            for (int i = 0; i < parseData.size(); i++) {
                mData.add(parseData.get(i));
            }
            mPageNo.incrementAndGet();
        }
        mHasMore = parseData.size() >= getPageSize();
        onDataAdd();
        notifyDataSetChangedSafely();
    }

    public List<BaseListAdapterItemEntity<T>> getAdapterData() {
        return mData;
    }

    public List<T> getOriginData() {
        return mOriginData;
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

    public abstract List<BaseListAdapterItemEntity<T>> parseTreeData(List<T> data, boolean reset);

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
