package com.pine.base.recycle_view.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.pine.base.recycle_view.BaseListViewHolder;
import com.pine.base.recycle_view.bean.BaseListAdapterItemEntity;
import com.pine.base.recycle_view.bean.BaseListAdapterItemProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tanghongfeng on 2018/10/22
 */

public abstract class BaseComplexListAdapter<T, B> extends BaseListAdapter {
    // 1: 表示第一页（计数从1开始）
    protected AtomicInteger mPageNo = new AtomicInteger(1);
    protected AtomicInteger mPageSize = new AtomicInteger(10);
    protected Boolean mHasMore = true;
    protected List<BaseListAdapterItemEntity<T>> mHeadNoPaginationData = new ArrayList<>();
    protected List<BaseListAdapterItemEntity<B>> mTailPaginationData = new ArrayList<>();

    public BaseComplexListAdapter() {

    }

    public BaseComplexListAdapter(int defaultItemViewType) {
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

    public final void enableTailEmpty(boolean enableTailEmptyView) {
        super.enableTailEmpty(enableTailEmptyView);
    }

    public final void enableEmptyMoreComplete(boolean enableEmptyView, boolean enableMoreView,
                                              boolean enableCompleteView, boolean enableErrorView) {
        super.enableEmptyMoreComplete(enableEmptyView, enableMoreView,
                enableCompleteView, enableErrorView);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseListViewHolder holder, int position) {
        if (isHeadView(position)) {
            holder.updateData("", new BaseListAdapterItemProperty(), position);
            return;
        }
        if (isEmptyView(position) || isMoreView(position) || isCompleteView(position) ||
                isTailEmptyView(position)) {
            holder.updateData("", new BaseListAdapterItemProperty(), 0);
            return;
        }
        int dataIndex = position - getHeadViewCount();
        if (mHeadNoPaginationData != null && dataIndex < mHeadNoPaginationData.size()) {
            holder.updateData(mHeadNoPaginationData.get(dataIndex).getData(), mHeadNoPaginationData.get(dataIndex).getPropertyEntity(), dataIndex);
        } else {
            int index = dataIndex - mHeadNoPaginationData.size();
            holder.updateData(mTailPaginationData.get(index).getData(), mTailPaginationData.get(index).getPropertyEntity(), index);
        }
    }

    @Override
    public int getItemCount() {
        int headOffset = getHeadViewCount();
        if (mEnableInitState) {
            return 0 + headOffset;
        }
        int topSize = mHeadNoPaginationData == null ? 0 : mHeadNoPaginationData.size();
        int bottomSize = mTailPaginationData == null ? 0 : mTailPaginationData.size();
        if (showEmptyView() || showErrorAllView()) {
            return 1 + headOffset;
        }
        int actualSize = topSize + bottomSize;
        if (showMoreView() || showCompleteView() || showErrorMoreView() || showTailEmptyView()) {
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
        if (isTailEmptyView(position)) {
            return TAIL_EMPTY_VIEW_HOLDER;
        }
        int dataIndex = position - getHeadViewCount();
        if (mHeadNoPaginationData != null && dataIndex < mHeadNoPaginationData.size()) {
            return mHeadNoPaginationData.get(dataIndex).getPropertyEntity().getItemViewType();
        } else {
            int index = mHeadNoPaginationData == null ? dataIndex : dataIndex - mHeadNoPaginationData.size();
            return mTailPaginationData.get(index).getPropertyEntity().getItemViewType();
        }
    }

    public final void setHeadData(List<T> data) {
        onDataSet();
        mEnableInitState = false;
        mHeadNoPaginationData = parseHeadData(data);
        notifyDataSetChangedSafely();
    }

    public final void addTailData(List<B> newData) {
        List<BaseListAdapterItemEntity<B>> parseData = parseTailData(newData);
        if (parseData == null || parseData.size() == 0) {
            mHasMore = false;
            notifyDataSetChangedSafely();
            return;
        }
        if (mTailPaginationData == null) {
            mEnableInitState = false;
            mTailPaginationData = parseData;
            resetAndGetPageNo();
        } else {
            for (int i = 0; i < parseData.size(); i++) {
                mTailPaginationData.add(parseData.get(i));
            }
            mPageNo.incrementAndGet();
        }
        mHasMore = parseData.size() >= getPageSize();
        notifyDataSetChangedSafely();
    }

    public final void setTailData(List<B> data) {
        onDataSet();
        mEnableInitState = false;
        mTailPaginationData = parseTailData(data);
        resetAndGetPageNo();
        mHasMore = mTailPaginationData != null && mTailPaginationData.size() >= getPageSize();
        notifyDataSetChangedSafely();
    }

    private boolean showEmptyView() {
        return !isErrorViewState() && isEmptyViewEnabled() &&
                (mHeadNoPaginationData == null || mHeadNoPaginationData.size() == 0) &&
                (mTailPaginationData == null || mTailPaginationData.size() == 0);
    }

    private boolean showMoreView() {
        return !isErrorViewState() && isMoreViewEnabled() && mHasMore &&
                mTailPaginationData != null && mTailPaginationData.size() != 0;
    }

    private boolean showCompleteView() {
        return !isErrorViewState() && isCompleteViewEnabled() && !mHasMore &&
                mTailPaginationData != null && mTailPaginationData.size() != 0;
    }

    private boolean showErrorAllView() {
        return isErrorViewState() && (mHeadNoPaginationData == null || mHeadNoPaginationData.size() == 0) &&
                (mTailPaginationData == null || mTailPaginationData.size() == 0);
    }

    private boolean showTailEmptyView() {
        return !isErrorViewState() && isTailEmptyViewEnabled() &&
                (mTailPaginationData == null || mTailPaginationData.size() == 0);
    }

    private boolean showErrorMoreView() {
        return isErrorViewState() && mHasMore && mTailPaginationData != null && mTailPaginationData.size() != 0;
    }

    private boolean isEmptyView(int position) {
        return showEmptyView() && position == (0 + getHeadViewCount());
    }

    private boolean isMoreView(int position) {
        int topSize = mHeadNoPaginationData == null ? 0 : mHeadNoPaginationData.size();
        int bottomSize = mTailPaginationData == null ? 0 : mTailPaginationData.size();
        return showMoreView() && position != 0 && position == (topSize + bottomSize + getHeadViewCount());
    }

    private boolean isCompleteView(int position) {
        int topSize = mHeadNoPaginationData == null ? 0 : mHeadNoPaginationData.size();
        int bottomSize = mTailPaginationData == null ? 0 : mTailPaginationData.size();
        return showCompleteView() && position != 0 && position == (topSize + bottomSize + getHeadViewCount());
    }

    private boolean isTailEmptyView(int position) {
        int topSize = mHeadNoPaginationData == null ? 0 : mHeadNoPaginationData.size();
        return showTailEmptyView() && position != 0 && position == (topSize + getHeadViewCount());
    }

    private boolean isErrorAllView(int position) {
        return showErrorAllView() && position == (0 + getHeadViewCount());
    }

    private boolean isErrorMoreView(int position) {
        int topSize = mHeadNoPaginationData == null ? 0 : mHeadNoPaginationData.size();
        int bottomSize = mTailPaginationData == null ? 0 : mTailPaginationData.size();
        return showErrorMoreView() && position != 0 && position == (topSize + bottomSize + getHeadViewCount());
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

    public abstract List<BaseListAdapterItemEntity<T>> parseHeadData(List<T> data);

    public abstract List<BaseListAdapterItemEntity<B>> parseTailData(List<B> data);

    public boolean isMoreViewEnabled() {
        return super.isMoreViewEnabled();
    }

    public boolean isTailEmptyViewEnabled() {
        return super.isTailEmptyViewEnabled();
    }

    public void setErrorMoreLayoutId(@LayoutRes int layoutResId) {
        super.setErrorMoreLayoutId(layoutResId);
    }

    public BaseListViewHolder<String> getErrorMoreViewHolder(ViewGroup parent) {
        return super.getErrorMoreViewHolder(parent);
    }

    public void setTailEmptyLayoutId(@LayoutRes int layoutResId) {
        super.setTailEmptyLayoutId(layoutResId);
    }
}
