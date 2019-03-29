package com.pine.base.list.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;

import com.pine.base.R;
import com.pine.base.list.BaseListViewHolder;
import com.pine.base.list.bean.BaseListAdapterItemProperty;

public abstract class BaseListAdapter extends RecyclerView.Adapter<BaseListViewHolder> {
    protected final static int EMPTY_BACKGROUND_VIEW_HOLDER = -10000;
    protected final static int MORE_VIEW_HOLDER = -10001;
    protected final static int COMPLETE_VIEW_HOLDER = -10002;
    private boolean mShowEmpty = true;
    private boolean mShowMore = true;
    private boolean mShowComplete = true;
    private int mCompleteLayoutId = R.layout.base_item_complete;
    private int mMoreLayoutId = R.layout.base_item_more;
    private int mEmptyLayoutId = R.layout.base_item_empty_background;
    private int mDefaultItemViewType = EMPTY_BACKGROUND_VIEW_HOLDER;

    protected RecyclerView mRecyclerView;

    public BaseListAdapter(int defaultItemViewType) {
        mDefaultItemViewType = defaultItemViewType;
    }

    @NonNull
    @Override
    public BaseListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        BaseListViewHolder viewHolder = null;
        switch (viewType) {
            case EMPTY_BACKGROUND_VIEW_HOLDER:
                viewHolder = getEmptyBackgroundViewHolder(viewGroup);
                break;
            case MORE_VIEW_HOLDER:
                viewHolder = getMoreViewHolder(viewGroup);
                break;
            case COMPLETE_VIEW_HOLDER:
                viewHolder = getCompleteViewHolder(viewGroup);
                break;
            default:
                viewHolder = getViewHolder(viewGroup, viewType);
                break;
        }
        return viewHolder;
    }

    public static boolean isLastViewMoreView(RecyclerView recyclerView) {
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        return recyclerView.getAdapter().getItemViewType(manager.findLastVisibleItemPosition()) == MORE_VIEW_HOLDER;
    }

    public void showEmptyMoreComplete(boolean showEmptyView, boolean showMoreView, boolean showCompleteView) {
        mShowEmpty = showEmptyView;
        mShowMore = showMoreView;
        mShowComplete = showCompleteView;
    }

    public void setMoreLayoutId(@LayoutRes int layoutResId) {
        mMoreLayoutId = layoutResId;
    }

    public void setEmptyLayoutId(@LayoutRes int layoutResId) {
        mEmptyLayoutId = layoutResId;
    }

    public void setCompleteLayoutId(@LayoutRes int layoutResId) {
        mCompleteLayoutId = layoutResId;
    }

    public boolean isEmptyViewSetup() {
        return mShowEmpty;
    }

    public boolean isMoreViewSetup() {
        return mShowMore;
    }

    public boolean isCompleteViewSetup() {
        return mShowComplete;
    }

    public BaseListViewHolder<String> getMoreViewHolder(ViewGroup parent) {
        return new MoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(mMoreLayoutId, parent, false));
    }

    public BaseListViewHolder<String> getEmptyBackgroundViewHolder(ViewGroup parent) {
        return new EmptyBackgroundViewHolder(parent.getContext(),
                LayoutInflater.from(parent.getContext()).inflate(mEmptyLayoutId, parent, false));
    }

    public BaseListViewHolder<String> getCompleteViewHolder(ViewGroup parent) {
        return new CompleteViewHolder(LayoutInflater.from(parent.getContext()).inflate(mCompleteLayoutId, parent, false));
    }

    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = null;
    }


    public int getDefaultItemViewType() {
        return mDefaultItemViewType;
    }

    public abstract BaseListViewHolder getViewHolder(ViewGroup parent, int viewType);

    /**
     * 空背景
     */
    public class EmptyBackgroundViewHolder extends BaseListViewHolder<String> {
        private View container;

        public EmptyBackgroundViewHolder(Context context, View itemView) {
            super(itemView);
            container = itemView.getRootView();
        }

        @Override
        public void updateData(String tipsValue, BaseListAdapterItemProperty propertyEntity, int position) {
            if (container != null) {
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT);
                container.setLayoutParams(params);
            }
        }
    }

    /**
     * 加载更多holder
     *
     * @param
     */
    public class MoreViewHolder extends BaseListViewHolder<String> {

        public MoreViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void updateData(String content, BaseListAdapterItemProperty propertyEntity, int position) {

        }
    }

    /**
     * 全部加载完成holder
     *
     * @param
     */
    public class CompleteViewHolder extends BaseListViewHolder<String> {

        public CompleteViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void updateData(String content, BaseListAdapterItemProperty propertyEntity, int position) {

        }
    }
}
