package com.pine.base.recycle_view.adapter;

import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;

import com.pine.base.R;
import com.pine.base.recycle_view.BaseListViewHolder;
import com.pine.base.recycle_view.bean.BaseListAdapterItemProperty;

public abstract class BaseListAdapter extends RecyclerView.Adapter<BaseListViewHolder> {
    protected final static int DEFAULT_VIEW_HOLDER = -10000;
    protected final static int HEAD_VIEW_HOLDER = -100000;
    protected final static int EMPTY_BACKGROUND_VIEW_HOLDER = -10001;
    protected final static int MORE_VIEW_HOLDER = -10002;
    protected final static int COMPLETE_VIEW_HOLDER = -10003;
    protected final static int ERROR_ALL_VIEW_HOLDER = -10004;
    protected final static int ERROR_MORE_VIEW_HOLDER = -10005;
    protected final static int TAIL_EMPTY_VIEW_HOLDER = -10006;
    protected boolean mEnableInitState = false;
    protected boolean mIsErrorState = false;
    private boolean mEnableEmpty = true;
    private boolean mEnableMore = true;
    private boolean mEnableComplete = true;
    private boolean mEnableTailEmpty = true;
    private boolean mEnableError = false;
    private int mCompleteLayoutId = R.layout.base_item_complete;
    private int mMoreLayoutId = R.layout.base_item_more;
    private int mEmptyLayoutId = R.layout.base_item_empty_background;
    private int mTailEmptyLayoutId = R.layout.base_item_empty_background;
    private int mErrorAllLayoutId = R.layout.base_item_error;
    private int mErrorMoreLayoutId = R.layout.base_item_error_more;
    private int mDefaultItemViewType = DEFAULT_VIEW_HOLDER;
    private View mHeadView = null;

    protected RecyclerView mRecyclerView;

    private RecyclerView.LayoutManager mLayoutManager;
    private boolean mIsNoDataItemState;
    private int mGridSpanCount;

    public BaseListAdapter() {

    }

    public BaseListAdapter(int defaultItemViewType) {
        mDefaultItemViewType = defaultItemViewType;
    }

    @NonNull
    @Override
    public BaseListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        BaseListViewHolder viewHolder = null;
        switch (viewType) {
            case HEAD_VIEW_HOLDER:
                viewHolder = getHeadViewHolder(viewGroup);
                break;
            case EMPTY_BACKGROUND_VIEW_HOLDER:
                viewHolder = getEmptyBackgroundViewHolder(viewGroup);
                break;
            case MORE_VIEW_HOLDER:
                viewHolder = getMoreViewHolder(viewGroup);
                break;
            case COMPLETE_VIEW_HOLDER:
                viewHolder = getCompleteViewHolder(viewGroup);
                break;
            case ERROR_ALL_VIEW_HOLDER:
                viewHolder = getErrorAllViewHolder(viewGroup);
                break;
            case ERROR_MORE_VIEW_HOLDER:
                viewHolder = getErrorMoreViewHolder(viewGroup);
                break;
            case TAIL_EMPTY_VIEW_HOLDER:
                viewHolder = getTailEmptyBackgroundViewHolder(viewGroup);
                break;
            default:
                viewHolder = getViewHolder(viewGroup, viewType);
                break;
        }
        return viewHolder;
    }

    public boolean isLastViewMoreView(RecyclerView recyclerView) {
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int index = manager.findLastCompletelyVisibleItemPosition();
        return index >= 0 && getItemViewType(index) == MORE_VIEW_HOLDER;
    }

    public boolean isLastViewMoreView(RecyclerView recyclerView, NestedScrollView scrollView) {
        if (scrollView.getChildCount() == 0) {
            return isLastViewMoreView(recyclerView);
        } else {
            View lastView = scrollView.getChildAt(scrollView.getChildCount() - 1);
            int bottom = lastView.getBottom();
            int offset = bottom - (scrollView.getHeight() + scrollView.getScrollY());
            if (offset <= 3) {
                return isLastViewMoreView(recyclerView);
            }
        }
        return false;
    }

    public void setOnScrollListener(@NonNull RecyclerView recyclerView, final IOnScrollListener listener) {
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                if (isLastViewMoreView(recyclerView)) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onLoadMore();
                            }
                        }
                    });
                }
            }
        });
    }

    private int lastOnLoadMoreScrollX, lastOnLoadMoreScrollY;

    protected void onDataAdd() {

    }

    protected void onDataSet() {
        lastOnLoadMoreScrollX = 0;
        lastOnLoadMoreScrollY = 0;
    }

    protected void onNoDataItemState(boolean isNoDataState) {
        mIsNoDataItemState = isNoDataState;
    }

    public void setOnScrollListener(@NonNull final RecyclerView recyclerView,
                                    final @NonNull NestedScrollView scrollView,
                                    final IOnScrollListener listener) {
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, final int scrollX, final int scrollY,
                                       int oldScrollX, int oldScrollY) {
                if (isLastViewMoreView(recyclerView, scrollView)) {
                    if (getRecycleViewOrientation() == RecyclerView.HORIZONTAL) {
                        if (lastOnLoadMoreScrollX == 0 || scrollX - lastOnLoadMoreScrollX > 5) {
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null) {
                                        listener.onLoadMore();
                                        lastOnLoadMoreScrollX = scrollX;
                                    }
                                }
                            });
                        }
                    } else {
                        if (lastOnLoadMoreScrollY == 0 || scrollY - lastOnLoadMoreScrollY > 5) {
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null) {
                                        listener.onLoadMore();
                                        lastOnLoadMoreScrollY = scrollY;
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public int getRecycleViewOrientation() {
        return ((LinearLayoutManager) mRecyclerView.getLayoutManager()).getOrientation();
    }

    public void setHeadView(@NonNull View view) {
        mHeadView = view;
    }

    public boolean isHeadViewEnabled() {
        return mHeadView != null;
    }

    public int getHeadViewCount() {
        return isHeadViewEnabled() ? 1 : 0;
    }

    protected boolean isHeadView(int position) {
        return isHeadViewEnabled() && position == 0;
    }

    public BaseListViewHolder<String> getHeadViewHolder(ViewGroup parent) {
        ViewGroup.LayoutParams layoutParams = mHeadView.getLayoutParams();
        if (mHeadView.getLayoutParams() == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        mHeadView.setLayoutParams(layoutParams);
        HeadViewHolder viewHolder = new HeadViewHolder(mHeadView);
        return viewHolder;
    }

    protected void enableEmptyMoreComplete(boolean enableEmptyView, boolean enableMoreView,
                                           boolean enableCompleteView, boolean enableErrorView) {
        mEnableEmpty = enableEmptyView;
        mEnableMore = enableMoreView;
        mEnableComplete = enableCompleteView;
        mEnableError = enableErrorView;
    }

    protected void enableTailEmpty(boolean enableTailEmptyView) {
        mEnableTailEmpty = enableTailEmptyView;
    }

    protected void setMoreLayoutId(@LayoutRes int layoutResId) {
        mMoreLayoutId = layoutResId;
    }

    public void setEmptyLayoutId(@LayoutRes int layoutResId) {
        mEmptyLayoutId = layoutResId;
    }

    public void setCompleteLayoutId(@LayoutRes int layoutResId) {
        mCompleteLayoutId = layoutResId;
    }

    public void setErrorAllLayoutId(@LayoutRes int layoutResId) {
        mErrorAllLayoutId = layoutResId;
    }

    protected void setErrorMoreLayoutId(@LayoutRes int layoutResId) {
        mErrorMoreLayoutId = layoutResId;
    }

    protected void setTailEmptyLayoutId(@LayoutRes int layoutResId) {
        mTailEmptyLayoutId = layoutResId;
    }

    public boolean isEmptyViewEnabled() {
        return mEnableEmpty;
    }

    protected boolean isMoreViewEnabled() {
        return mEnableMore;
    }

    public boolean isCompleteViewEnabled() {
        return mEnableComplete;
    }

    public boolean isTailEmptyViewEnabled() {
        return mEnableTailEmpty;
    }

    public boolean isErrorViewEnabled() {
        return mEnableError;
    }

    public boolean isErrorViewState() {
        return isErrorViewEnabled() && mIsErrorState;
    }

    public BaseListViewHolder<String> getMoreViewHolder(ViewGroup parent) {
        return new MoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(mMoreLayoutId, parent, false));
    }

    public BaseListViewHolder<String> getEmptyBackgroundViewHolder(ViewGroup parent) {
        return new EmptyBackgroundViewHolder(LayoutInflater.from(parent.getContext()).inflate(mEmptyLayoutId, parent, false));
    }

    public BaseListViewHolder<String> getCompleteViewHolder(ViewGroup parent) {
        return new CompleteViewHolder(LayoutInflater.from(parent.getContext()).inflate(mCompleteLayoutId, parent, false));
    }

    public BaseListViewHolder<String> getErrorAllViewHolder(ViewGroup parent) {
        return new ErrorAllViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(mErrorAllLayoutId, parent, false));
    }

    protected BaseListViewHolder<String> getErrorMoreViewHolder(ViewGroup parent) {
        return new ErrorMoreViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(mErrorMoreLayoutId, parent, false));
    }

    protected BaseListViewHolder<String> getTailEmptyBackgroundViewHolder(ViewGroup parent) {
        return new TailEmptyBackgroundViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(mTailEmptyLayoutId, parent, false));
    }

    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mLayoutManager = mRecyclerView.getLayoutManager();
        if (mLayoutManager instanceof GridLayoutManager) {
            mGridSpanCount = ((GridLayoutManager) mLayoutManager).getSpanCount();
            ((GridLayoutManager) mLayoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return position == 0 && mIsNoDataItemState ? mGridSpanCount : 1;
                }
            });
        }
    }

    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = null;
        mLayoutManager = null;
    }

    public int getDefaultItemViewType() {
        return mDefaultItemViewType;
    }

    public final void enableInitState(boolean enabled) {
        mEnableInitState = enabled;
    }

    public final void setErrorState() {
        mIsErrorState = true;
    }

    public final void notifyDataSetChangedSafely() {
        if (mRecyclerView != null && mRecyclerView.isComputingLayout()) {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        } else {
            notifyDataSetChanged();
        }
    }

    public final void notifyItemChangedSafely(final int position, final @Nullable Object payload) {
        notifyItemRangeChangedSafely(position, 1, payload);
    }

    public final void notifyItemRangeChangedSafely(final int positionStart, final int itemCount,
                                                   final @Nullable Object payload) {
        if (mRecyclerView != null && mRecyclerView.isComputingLayout()) {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemRangeChanged(positionStart, itemCount, payload);
                }
            });
        } else {
            notifyItemRangeChanged(positionStart, itemCount, payload);
        }
    }

    public final void notifyItemChangedSafely(int position) {
        notifyItemRangeChangedSafely(position, 1);
    }

    public final void notifyItemRangeChangedSafely(final int positionStart, final int itemCount) {
        if (mRecyclerView != null && mRecyclerView.isComputingLayout()) {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemRangeChanged(positionStart, itemCount);
                }
            });
        } else {
            notifyItemRangeChanged(positionStart, itemCount);
        }
    }

    public final void notifyItemMovedSafely(final int fromPosition, final int toPosition) {
        if (mRecyclerView != null && mRecyclerView.isComputingLayout()) {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemMoved(fromPosition, toPosition);
                }
            });
        } else {
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    public final void notifyItemInsertedSafely(int position) {
        notifyItemRangeInsertedSafely(position, 1);
    }

    public final void notifyItemRangeInsertedSafely(final int positionStart, final int itemCount) {
        if (mRecyclerView != null && mRecyclerView.isComputingLayout()) {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemRangeInserted(positionStart, itemCount);
                }
            });
        } else {
            notifyItemRangeInserted(positionStart, itemCount);
        }
    }

    public final void notifyItemRemovedSafely(int position) {
        notifyItemRangeRemovedSafely(position, 1);
    }

    public final void notifyItemRangeRemovedSafely(final int positionStart, final int itemCount) {
        if (mRecyclerView != null && mRecyclerView.isComputingLayout()) {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    notifyItemRangeRemoved(positionStart, itemCount);
                }
            });
        } else {
            notifyItemRangeRemoved(positionStart, itemCount);
        }
    }

    public abstract BaseListViewHolder getViewHolder(ViewGroup parent, int viewType);

    /**
     * Head ViewHolder
     *
     * @param
     */
    public class HeadViewHolder extends BaseListViewHolder<String> {

        public HeadViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void updateData(String content, BaseListAdapterItemProperty propertyEntity, int position) {

        }
    }

    /**
     * 空背景
     */
    public class EmptyBackgroundViewHolder extends BaseListViewHolder<String> {
        private View container;

        public EmptyBackgroundViewHolder(View itemView) {
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

    /**
     * 尾部空背景
     */
    public class TailEmptyBackgroundViewHolder extends BaseListViewHolder<String> {
        private View container;

        public TailEmptyBackgroundViewHolder(View itemView) {
            super(itemView);
            container = itemView.getRootView();
        }

        @Override
        public void updateData(String tipsValue, BaseListAdapterItemProperty propertyEntity, int position) {
            if (container != null) {
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT);
                container.setLayoutParams(params);
            }
        }
    }

    /**
     * 刷新加载时的错误holder
     *
     * @param
     */
    public class ErrorAllViewHolder extends BaseListViewHolder<String> {
        private View container;

        public ErrorAllViewHolder(View itemView) {
            super(itemView);
            container = itemView.getRootView();
        }

        @Override
        public void updateData(String content, BaseListAdapterItemProperty propertyEntity, int position) {
            if (container != null) {
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT);
                container.setLayoutParams(params);
            }
        }
    }

    /**
     * 加载更多时的错误holder
     *
     * @param
     */
    public class ErrorMoreViewHolder extends BaseListViewHolder<String> {

        public ErrorMoreViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void updateData(String content, BaseListAdapterItemProperty propertyEntity, int position) {

        }
    }

    public interface IOnScrollListener {
        void onLoadMore();
    }
}
