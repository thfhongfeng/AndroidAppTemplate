package com.pine.base.list.adapter;

import android.support.annotation.NonNull;

import com.pine.base.list.BaseListViewHolder;
import com.pine.base.list.bean.BaseListAdapterItemEntity;
import com.pine.base.list.bean.BaseListAdapterItemProperty;

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
    protected List<BaseListAdapterItemEntity<T>> mHeadNoPaginationData = null;
    protected List<BaseListAdapterItemEntity<B>> mTailPaginationData = null;
    private boolean mIsInitState = true;

    public BaseComplexListAdapter() {
        super(EMPTY_BACKGROUND_VIEW_HOLDER);
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
        if (mIsInitState()) {
            return 0;
        }
        int topSize = mHeadNoPaginationData == null ? 0 : mHeadNoPaginationData.size();
        int bottomSize = mTailPaginationData == null ? 0 : mTailPaginationData.size();
        if (topSize == 0 && bottomSize == 0 && isCompleteViewSetup()) {
            return 1;
        }
        int actualSize = topSize + bottomSize;
        if (hasMoreView() || hasCompleteView()) {
            return actualSize + 1;
        }
        return actualSize;
    }

    @Override
    public int getItemViewType(int position) {
        int topSize = mHeadNoPaginationData == null ? 0 : mHeadNoPaginationData.size();
        int bottomSize = mTailPaginationData == null ? 0 : mTailPaginationData.size();
        if (topSize == 0 && bottomSize == 0) {
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
            int index = position - mHeadNoPaginationData.size();
            return mTailPaginationData.get(index).getPropertyEntity().getItemViewType();
        }
    }

    public final void setHeadData(List<T> data) {
        mIsInitState = false;
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
            mIsInitState = false;
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
        mIsInitState = false;
        mTailPaginationData = parseTailData(data);
        resetAndGetPageNo();
        mHasMore = mTailPaginationData != null && mTailPaginationData.size() >= getPageSize();
        notifyDataSetChanged();
    }

    private boolean hasMoreView() {
        return isMoreViewSetup() && mHasMore && mTailPaginationData != null && mTailPaginationData.size() != 0;
    }

    private boolean hasCompleteView() {
        return isCompleteViewSetup() && !mHasMore && mTailPaginationData != null && mTailPaginationData.size() != 0;
    }

    private boolean isMoreView(int position) {
        int topSize = mHeadNoPaginationData == null ? 0 : mHeadNoPaginationData.size();
        int bottomSize = mTailPaginationData == null ? 0 : mTailPaginationData.size();
        return isMoreViewSetup() && mHasMore && position != 0 && position == topSize + bottomSize;
    }

    private boolean isCompleteView(int position) {
        int topSize = mHeadNoPaginationData == null ? 0 : mHeadNoPaginationData.size();
        int bottomSize = mTailPaginationData == null ? 0 : mTailPaginationData.size();
        return isCompleteViewSetup() && !mHasMore && position != 0 && position == topSize + bottomSize;
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

    public abstract List<BaseListAdapterItemEntity<T>> parseHeadData(List<T> data);

    public abstract List<BaseListAdapterItemEntity<B>> parseTailData(List<B> data);
}
