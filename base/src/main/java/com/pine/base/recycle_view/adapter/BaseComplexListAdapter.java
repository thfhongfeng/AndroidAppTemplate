package com.pine.base.recycle_view.adapter;

import android.view.ViewGroup;

import com.pine.base.recycle_view.BaseListViewHolder;
import com.pine.base.recycle_view.bean.BaseListAdapterItemEntity;
import com.pine.base.recycle_view.bean.BaseListAdapterItemProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

/**
 * Created by tanghongfeng on 2018/10/22
 */

public abstract class BaseComplexListAdapter<T, B> extends BaseListAdapter {
    // 1: 表示第一页（计数从1开始）
    protected AtomicInteger mPageNo = new AtomicInteger(1);
    protected AtomicInteger mPageSize = new AtomicInteger(10);
    protected Boolean mHasMore = true;
    protected List<T> mOriginFirstPartNoPaginationData;
    protected List<BaseListAdapterItemEntity<T>> mFirstPartNoPaginationData = new ArrayList<>();
    protected List<B> mOriginSecondPartPaginationData;
    protected List<BaseListAdapterItemEntity<B>> mSecondPartPaginationData = new ArrayList<>();

    public BaseComplexListAdapter() {

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

    public final void enableSecondPartEmpty(boolean enableSecondPartEmptyView) {
        super.enableSecondPartEmpty(enableSecondPartEmptyView);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseListViewHolder holder, int position) {
        if (isHeadView(position) || isInitLoadingView(position) || isEmptyView(position) ||
                isMoreView(position) || isCompleteView(position) || isSecondPartInitLoadingView(position) ||
                isSecondPartEmptyView(position)) {
            holder.updateData("", new BaseListAdapterItemProperty(), position);
            return;
        }
        int dataIndex = position - getHeadViewCount();
        if (mFirstPartNoPaginationData != null && dataIndex < mFirstPartNoPaginationData.size()) {
            holder.updateData(mFirstPartNoPaginationData.get(dataIndex).getData(), mFirstPartNoPaginationData.get(dataIndex).getPropertyEntity(), dataIndex);
        } else {
            int index = dataIndex - mFirstPartNoPaginationData.size();
            holder.updateData(mSecondPartPaginationData.get(index).getData(), mSecondPartPaginationData.get(index).getPropertyEntity(), index);
        }
    }

    @Override
    public int getItemCount() {
        setNoDataItemState(false);
        int headOffset = getHeadViewCount();
        int topSize = mFirstPartNoPaginationData == null ? 0 : mFirstPartNoPaginationData.size();
        int bottomSize = mSecondPartPaginationData == null ? 0 : mSecondPartPaginationData.size();
        if (showInitLoadingView() || showEmptyView() || showErrorAllView()) {
            setNoDataItemState(true);
            return 1 + headOffset;
        }
        int actualSize = topSize + bottomSize;
        if (showMoreView() || showCompleteView() || showErrorMoreView() || showSecondPartInitLoadingView() || showSecondPartEmptyView()) {
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
        if (isSecondPartInitLoadingView(position)) {
            return SECOND_PART_INIT_LOADING_HOLDER;
        }
        if (isSecondPartEmptyView(position)) {
            return SECOND_PART_EMPTY_VIEW_HOLDER;
        }
        int dataIndex = position - getHeadViewCount();
        if (mFirstPartNoPaginationData != null && dataIndex < mFirstPartNoPaginationData.size()) {
            return mFirstPartNoPaginationData.get(dataIndex).getPropertyEntity().getItemViewType();
        } else {
            int index = mFirstPartNoPaginationData == null ? dataIndex : dataIndex - mFirstPartNoPaginationData.size();
            return mSecondPartPaginationData.get(index).getPropertyEntity().getItemViewType();
        }
    }

    private boolean showInitLoadingView() {
        return isInitLoadingViewState() && isSecondPartInitLoadingViewState() && !isErrorViewState() &&
                (mSecondPartPaginationData == null || mSecondPartPaginationData.size() == 0) &&
                (mSecondPartPaginationData == null || mSecondPartPaginationData.size() == 0);
    }

    private boolean showEmptyView() {
        return !isInitLoadingViewState() && !isSecondPartInitLoadingViewState() && !isErrorViewState() && isEmptyViewEnabled() &&
                (mFirstPartNoPaginationData == null || mFirstPartNoPaginationData.size() == 0) &&
                (mSecondPartPaginationData == null || mSecondPartPaginationData.size() == 0);
    }

    private boolean showMoreView() {
        return !isSecondPartInitLoadingViewState() && !isErrorViewState() && isMoreViewEnabled() && mHasMore &&
                mSecondPartPaginationData != null && mSecondPartPaginationData.size() != 0;
    }

    private boolean showCompleteView() {
        return !isSecondPartInitLoadingViewState() && !isErrorViewState() && isCompleteViewEnabled() && !mHasMore &&
                mSecondPartPaginationData != null && mSecondPartPaginationData.size() != 0;
    }

    private boolean showErrorAllView() {
        return !isInitLoadingViewState() && !isSecondPartInitLoadingViewState() && isErrorViewState() &&
                (mFirstPartNoPaginationData == null || mFirstPartNoPaginationData.size() == 0) &&
                (mSecondPartPaginationData == null || mSecondPartPaginationData.size() == 0);
    }

    private boolean showSecondPartInitLoadingView() {
        return isSecondPartInitLoadingViewState() && !isErrorViewState() &&
                (mSecondPartPaginationData == null || mSecondPartPaginationData.size() == 0);
    }

    private boolean showSecondPartEmptyView() {
        return !isSecondPartInitLoadingViewState() && !isErrorViewState() && isSecondPartEmptyViewEnabled() &&
                (mSecondPartPaginationData == null || mSecondPartPaginationData.size() == 0);
    }

    private boolean showErrorMoreView() {
        return !isSecondPartInitLoadingViewState() && isErrorViewState() && mHasMore &&
                mSecondPartPaginationData != null && mSecondPartPaginationData.size() != 0;
    }

    private boolean isInitLoadingView(int position) {
        return showInitLoadingView() && position == (0 + getHeadViewCount());
    }

    private boolean isEmptyView(int position) {
        return showEmptyView() && position == (0 + getHeadViewCount());
    }

    private boolean isMoreView(int position) {
        int topSize = mFirstPartNoPaginationData == null ? 0 : mFirstPartNoPaginationData.size();
        int bottomSize = mSecondPartPaginationData == null ? 0 : mSecondPartPaginationData.size();
        return showMoreView() && position != 0 && position == (topSize + bottomSize + getHeadViewCount());
    }

    private boolean isCompleteView(int position) {
        int topSize = mFirstPartNoPaginationData == null ? 0 : mFirstPartNoPaginationData.size();
        int bottomSize = mSecondPartPaginationData == null ? 0 : mSecondPartPaginationData.size();
        return showCompleteView() && position != 0 && position == (topSize + bottomSize + getHeadViewCount());
    }

    private boolean isSecondPartInitLoadingView(int position) {
        int topSize = mFirstPartNoPaginationData == null ? 0 : mFirstPartNoPaginationData.size();
        return showSecondPartInitLoadingView() && position == (topSize + getHeadViewCount());
    }

    private boolean isSecondPartEmptyView(int position) {
        int topSize = mFirstPartNoPaginationData == null ? 0 : mFirstPartNoPaginationData.size();
        return showSecondPartEmptyView() && position == (topSize + getHeadViewCount());
    }

    private boolean isErrorAllView(int position) {
        return showErrorAllView() && position == (0 + getHeadViewCount());
    }

    private boolean isErrorMoreView(int position) {
        int topSize = mFirstPartNoPaginationData == null ? 0 : mFirstPartNoPaginationData.size();
        int bottomSize = mSecondPartPaginationData == null ? 0 : mSecondPartPaginationData.size();
        return showErrorMoreView() && position != 0 && position == (topSize + bottomSize + getHeadViewCount());
    }

    public final int getOriginDataItemViewType(int originDataPosition) {
        return DEFAULT_VIEW_HOLDER;
    }

    public final void setFirstPartData(List<T> data) {
        mOriginFirstPartNoPaginationData = data;
        mFirstPartNoPaginationData = parseFirstPartData(data);
        onDataSet();
        notifyDataSetChangedSafely();
    }

    public final void setSecondPartData(List<B> data) {
        mOriginSecondPartPaginationData = data;
        mSecondPartPaginationData = parseSecondPartData(data);
        resetAndGetPageNo();
        mHasMore = mSecondPartPaginationData != null && mSecondPartPaginationData.size() >= getPageSize();
        onSecondPartDataSet();
        notifyDataSetChangedSafely();
    }

    public final void addSecondPartData(List<B> newData) {
        List<BaseListAdapterItemEntity<B>> parseData = parseSecondPartData(newData);
        if (parseData == null || parseData.size() == 0) {
            mHasMore = false;
            notifyDataSetChangedSafely();
            return;
        }
        if (mOriginSecondPartPaginationData == null) {
            mOriginSecondPartPaginationData = newData;
        } else {
            mOriginSecondPartPaginationData.addAll(newData);
        }
        if (mSecondPartPaginationData == null) {
            mSecondPartPaginationData = parseData;
            resetAndGetPageNo();
        } else {
            for (int i = 0; i < parseData.size(); i++) {
                mSecondPartPaginationData.add(parseData.get(i));
            }
            mPageNo.incrementAndGet();
        }
        mHasMore = parseData.size() >= getPageSize();
        onDataAdd();
        notifyDataSetChangedSafely();
    }

    public List<BaseListAdapterItemEntity<T>> getFirstPartNoPaginationAdapterData() {
        return mFirstPartNoPaginationData;
    }

    public List<T> getOriginFirstPartNoPaginationData() {
        return mOriginFirstPartNoPaginationData;
    }

    public List<BaseListAdapterItemEntity<B>> getSecondPartNoPaginationAdapterData() {
        return mSecondPartPaginationData;
    }

    public List<B> getOriginSecondPartNoPaginationData() {
        return mOriginSecondPartPaginationData;
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

    public abstract List<BaseListAdapterItemEntity<T>> parseFirstPartData(List<T> data);

    public abstract List<BaseListAdapterItemEntity<B>> parseSecondPartData(List<B> data);

    public boolean isMoreViewEnabled() {
        return super.isMoreViewEnabled();
    }

    public boolean isSecondPartInitLoadingViewEnabled() {
        return super.isSecondPartInitLoadingViewEnabled();
    }

    public boolean isSecondPartEmptyViewEnabled() {
        return super.isSecondPartEmptyViewEnabled();
    }

    public void setErrorMoreLayoutId(@LayoutRes int layoutResId) {
        super.setErrorMoreLayoutId(layoutResId);
    }

    public BaseListViewHolder<String> getErrorMoreViewHolder(ViewGroup parent) {
        return super.getErrorMoreViewHolder(parent);
    }

    public void setSecondPartInitLoadingLayoutId(@LayoutRes int layoutResId) {
        super.setSecondPartInitLoadingLayoutId(layoutResId);
    }

    public void setSecondPartEmptyLayoutId(@LayoutRes int layoutResId) {
        super.setSecondPartEmptyLayoutId(layoutResId);
    }
}
