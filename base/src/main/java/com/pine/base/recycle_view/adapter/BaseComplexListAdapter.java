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
    public void onBindViewHolder(@NonNull BaseListViewHolder holder, int position) {
        int topSize = mHeadNoPaginationData == null ? 0 : mHeadNoPaginationData.size();
        int bottomSize = mTailPaginationData == null ? 0 : mTailPaginationData.size();
        if (topSize == 0 && bottomSize == 0) {
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
        if (mHeadNoPaginationData != null && position < mHeadNoPaginationData.size()) {
            holder.updateData(mHeadNoPaginationData.get(position).getData(), mHeadNoPaginationData.get(position).getPropertyEntity(), position);
        } else {
            int index = position - mHeadNoPaginationData.size();
            holder.updateData(mTailPaginationData.get(index).getData(), mTailPaginationData.get(index).getPropertyEntity(), index);
        }
    }

    @Override
    public int getItemCount() {
        if (mEnableInitState) {
            return 0;
        }
        int topSize = mHeadNoPaginationData == null ? 0 : mHeadNoPaginationData.size();
        int bottomSize = mTailPaginationData == null ? 0 : mTailPaginationData.size();
        if (showEmptyView() || showErrorAllView()) {
            return 1;
        }
        int actualSize = topSize + bottomSize;
        if (showMoreView() || showCompleteView() || showErrorMoreView()) {
            return actualSize + 1;
        }
        return actualSize;
    }

    @Override
    public int getItemViewType(int position) {
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
        if (mHeadNoPaginationData != null && position < mHeadNoPaginationData.size()) {
            return mHeadNoPaginationData.get(position).getPropertyEntity().getItemViewType();
        } else {
            int index = mHeadNoPaginationData == null ? position : position - mHeadNoPaginationData.size();
            return mTailPaginationData.get(index).getPropertyEntity().getItemViewType();
        }
    }

    public final void setHeadData(List<T> data) {
        onDataSet();
        mEnableInitState = false;
        mHeadNoPaginationData = parseHeadData(data);
        notifyDataSetChanged();
    }

    public final void addTailData(List<B> newData) {
        List<BaseListAdapterItemEntity<B>> parseData = parseTailData(newData);
        if (parseData == null || parseData.size() == 0) {
            mHasMore = false;
            notifyDataSetChanged();
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
        notifyDataSetChanged();
    }

    public final void setTailData(List<B> data) {
        onDataSet();
        mEnableInitState = false;
        mTailPaginationData = parseTailData(data);
        resetAndGetPageNo();
        mHasMore = mTailPaginationData != null && mTailPaginationData.size() >= getPageSize();
        notifyDataSetChanged();
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

    private boolean showErrorMoreView() {
        return isErrorViewState() && mHasMore && mTailPaginationData != null && mTailPaginationData.size() != 0;
    }

    private boolean isEmptyView(int position) {
        return showEmptyView() && position == 0;
    }

    private boolean isMoreView(int position) {
        int topSize = mHeadNoPaginationData == null ? 0 : mHeadNoPaginationData.size();
        int bottomSize = mTailPaginationData == null ? 0 : mTailPaginationData.size();
        return showMoreView() && position != 0 && position == topSize + bottomSize;
    }

    private boolean isCompleteView(int position) {
        int topSize = mHeadNoPaginationData == null ? 0 : mHeadNoPaginationData.size();
        int bottomSize = mTailPaginationData == null ? 0 : mTailPaginationData.size();
        return showCompleteView() && position != 0 && position == topSize + bottomSize;
    }

    private boolean isErrorAllView(int position) {
        return showErrorAllView() && position == 0;
    }

    private boolean isErrorMoreView(int position) {
        int topSize = mHeadNoPaginationData == null ? 0 : mHeadNoPaginationData.size();
        int bottomSize = mTailPaginationData == null ? 0 : mTailPaginationData.size();
        return showErrorMoreView() && position != 0 && position == topSize + bottomSize;
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

    public void setErrorMoreLayoutId(@LayoutRes int layoutResId) {
        super.setErrorMoreLayoutId(layoutResId);
    }

    public BaseListViewHolder<String> getErrorMoreViewHolder(ViewGroup parent) {
        return super.getErrorMoreViewHolder(parent);
    }
}
