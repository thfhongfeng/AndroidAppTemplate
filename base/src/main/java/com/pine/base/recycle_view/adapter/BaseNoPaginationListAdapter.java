package com.pine.base.recycle_view.adapter;

import com.pine.base.recycle_view.BaseListViewHolder;
import com.pine.base.recycle_view.bean.BaseListAdapterItemEntity;
import com.pine.base.recycle_view.bean.BaseListAdapterItemProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public abstract class BaseNoPaginationListAdapter<T> extends BaseListAdapter {
    protected List<BaseListAdapterItemEntity<T>> mData = new ArrayList<>();

    public BaseNoPaginationListAdapter() {

    }

    public BaseNoPaginationListAdapter(int defaultItemViewType) {
        super(defaultItemViewType);
    }

    public final void enableEmptyComplete(boolean enableEmptyView,
                                          boolean enableCompleteView) {
        super.enableEmptyMoreComplete(enableEmptyView, false,
                enableCompleteView, false);
    }

    public final void enableEmptyComplete(boolean enableEmptyView,
                                          boolean enableCompleteView, boolean enableErrorView) {
        super.enableEmptyMoreComplete(enableEmptyView, false,
                enableCompleteView, enableErrorView);
    }

    @Override
    public void onBindViewHolder(BaseListViewHolder holder, int position) {
        if (isErrorView(position) || isEmptyView(position) || isCompleteView(position)) {
            holder.updateData("", new BaseListAdapterItemProperty(), position);
            return;
        }
        holder.updateData(mData.get(position).getData(), mData.get(position).getPropertyEntity(), position);
    }

    @Override
    public int getItemCount() {
        if (mEnableInitState) {
            return 0;
        }
        if (showEmptyView() || isErrorViewState()) {
            return 1;
        }
        int actualSize = mData == null ? 0 : mData.size();
        if (showCompleteView()) {
            return actualSize + 1;
        }
        return actualSize;
    }

    @Override
    public int getItemViewType(int position) {
        if (isErrorView(position)) {
            return ERROR_ALL_VIEW_HOLDER;
        }
        if (isEmptyView(position)) {
            return EMPTY_BACKGROUND_VIEW_HOLDER;
        }
        if (isCompleteView(position)) {
            return COMPLETE_VIEW_HOLDER;
        }
        BaseListAdapterItemEntity itemEntity = mData.get(position);
        return itemEntity != null && itemEntity.getPropertyEntity().getItemViewType() != DEFAULT_VIEW_HOLDER ?
                itemEntity.getPropertyEntity().getItemViewType() : getDefaultItemViewType();
    }

    private boolean showEmptyView() {
        return !isErrorViewState() && isEmptyViewEnabled() && (mData == null || mData.size() == 0);
    }

    private boolean showCompleteView() {
        return !isErrorViewState() && isCompleteViewEnabled() && mData != null && mData.size() != 0;
    }

    private boolean isEmptyView(int position) {
        return showEmptyView() && position == 0;
    }

    private boolean isCompleteView(int position) {
        return showCompleteView() && position != 0 && position == mData.size();
    }

    private boolean isErrorView(int position) {
        return isErrorViewState() && position == 0;
    }

    public final void setData(List<T> data) {
        mIsErrorState = false;
        mEnableInitState = false;
        mData = parseData(data, true);
        notifyDataSetChanged();
    }

    public final void addData(List<T> newData) {
        mIsErrorState = false;
        List<BaseListAdapterItemEntity<T>> parseData = parseData(newData, false);
        if (parseData == null || parseData.size() == 0) {
            notifyDataSetChanged();
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
}
