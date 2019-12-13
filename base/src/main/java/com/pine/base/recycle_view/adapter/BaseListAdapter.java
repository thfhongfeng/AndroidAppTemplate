package com.pine.base.recycle_view.adapter;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pine.base.R;
import com.pine.base.recycle_view.BaseListViewHolder;
import com.pine.base.recycle_view.bean.BaseListAdapterItemProperty;
import com.pine.tool.util.LogUtils;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public abstract class BaseListAdapter extends RecyclerView.Adapter<BaseListViewHolder> {
    private final String TAG = LogUtils.makeLogTag(BaseListAdapter.class);

    // 默认ViewHolder
    public final static int DEFAULT_VIEW_HOLDER = -10000;
    // 表头ViewHolder
    protected final static int HEAD_VIEW_HOLDER = -100000;
    // 初始状态ViewHolder（还未填充数据时展示的View）
    protected final static int INIT_LOADING_VIEW_HOLDER = -100001;
    // 数据为空ViewHolder
    protected final static int EMPTY_BACKGROUND_VIEW_HOLDER = -10002;
    // 加载更多ViewHolder
    protected final static int MORE_VIEW_HOLDER = -10003;
    // 全部数据加载完全ViewHolder
    protected final static int COMPLETE_VIEW_HOLDER = -10004;
    // 数据加载发生错误ViewHolder（返回数据有问题时通过主动设置mEnableError来决定是否加载该ViewHolder）
    protected final static int ERROR_ALL_VIEW_HOLDER = -10005;
    // 加载更多时发生错误ViewHolder（返回数据有问题时通过主动设置mEnableError来决定是否加载该ViewHolder）
    protected final static int ERROR_MORE_VIEW_HOLDER = -10006;
    // 复杂布局的Adapter中，第二部分初始状态ViewHolder
    protected final static int SECOND_PART_INIT_LOADING_HOLDER = -10007;
    // 复杂布局的Adapter中，第二部分为空ViewHolder
    protected final static int SECOND_PART_EMPTY_VIEW_HOLDER = -10008;
    // Adapter的当前数据是否处于错误状态
    protected boolean mIsErrorState = false;
    // Adapter当前是否处于初始状态（还未填充数据）
    protected boolean mIsInitState = true;
    // Adapter当前第二部分是否处于初始状态（还未填充数据）
    protected boolean mIsSecondPartInitState = true;
    // 是否开启初始状态ViewHolder
    protected boolean mEnableInitLoading = false;
    // 是否开启数据为空ViewHolder
    private boolean mEnableEmpty = true;
    // 是否开启加载更多ViewHolder
    private boolean mEnableMore = true;
    // 是否开启全部数据加载完全ViewHolder
    private boolean mEnableComplete = true;
    // 是否开启第二部分初始状态ViewHolder
    private boolean mEnableSecondPartInitLoading = false;
    // 是否开启第二部分为空ViewHolder
    private boolean mEnableSecondPartEmpty = true;
    // 是否开启数据为空ViewHolder
    private boolean mEnableError = false;
    // 初始状态ViewHolder默认布局
    private int mInitLoadingLayoutId = R.layout.base_item_init_loading;
    // 全部数据加载完全ViewHolder默认布局
    private int mCompleteLayoutId = R.layout.base_item_complete;
    // 加载更多ViewHolder默认布局
    private int mMoreLayoutId = R.layout.base_item_more;
    // 数据为空ViewHolder默认布局
    private int mEmptyLayoutId = R.layout.base_item_empty_background;
    // 复杂布局的Adapter中，第二部分初始状态ViewHolder默认布局
    private int mSecondPartInitLoadingLayoutId = R.layout.base_item_init_loading;
    // 复杂布局的Adapter中，第二部分为空ViewHolder默认布局
    private int mSecondPartEmptyLayoutId = R.layout.base_item_empty_background;
    // 数据加载发生错误ViewHolder默认布局
    private int mErrorAllLayoutId = R.layout.base_item_error;
    // 加载更多时发生错误ViewHolder默认布局
    private int mErrorMoreLayoutId = R.layout.base_item_error_more;

    // 表头View
    private View mHeadView = null;

    // Adapter适配的RecyclerView
    protected RecyclerView mRecyclerView;
    // Adapter适配的RecyclerView的LayoutManager
    private RecyclerView.LayoutManager mLayoutManager;
    // Adapter的当前数据是否是空状态
    private boolean mIsNoDataItemState;
    // Adapter适配的RecyclerView的LayoutManager是栅格布局时的列数
    private int mGridSpanCount;

    public BaseListAdapter() {

    }

    @NonNull
    @Override
    public BaseListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        BaseListViewHolder viewHolder = null;
        switch (viewType) {
            case HEAD_VIEW_HOLDER:
                viewHolder = getHeadViewHolder(viewGroup);
                break;
            case INIT_LOADING_VIEW_HOLDER:
                viewHolder = getInitLoadingViewHolder(viewGroup);
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
            case SECOND_PART_INIT_LOADING_HOLDER:
                viewHolder = getSecondPartInitLoadingViewHolder(viewGroup);
                break;
            case SECOND_PART_EMPTY_VIEW_HOLDER:
                viewHolder = getSecondPartEmptyViewHolder(viewGroup);
                break;
            default:
                viewHolder = getViewHolder(viewGroup, viewType);
                break;
        }
        return viewHolder;
    }

    /**
     * 判断最后一个item是否加载更多View
     *
     * @param recyclerView
     * @return
     */
    public boolean isLastViewMoreView(RecyclerView recyclerView) {
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int index = manager.findLastCompletelyVisibleItemPosition();
        return index >= 0 && getItemViewType(index) == MORE_VIEW_HOLDER;
    }

    /**
     * 判断最后一个item是否加载更多View，用于RecyclerView的父View是NestedScrollView的情况
     *
     * @param recyclerView
     * @param scrollView
     * @return
     */
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

    /**
     * 设置RecyclerView的IOnScrollListener
     *
     * @param recyclerView
     * @param listener
     */
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

    // 记录上一次onScrollChange的scrollX，scrollY
    private int lastOnLoadMoreScrollX, lastOnLoadMoreScrollY;

    /**
     * 设置RecyclerView的IOnScrollListener，用于RecyclerView的父View是NestedScrollView的情况
     *
     * @param recyclerView
     * @param scrollView
     * @param listener
     */
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

    public final void enableInitLoading(boolean enabled) {
        mEnableInitLoading = enabled;
    }

    protected void enableEmptyMoreCompleteError(boolean enableEmptyView, boolean enableMoreView,
                                                boolean enableCompleteView, boolean enableErrorView) {
        mEnableEmpty = enableEmptyView;
        mEnableMore = enableMoreView;
        mEnableComplete = enableCompleteView;
        mEnableError = enableErrorView;
    }

    public final void enableSecondPartInitLoading(boolean enabled) {
        mEnableSecondPartInitLoading = enabled;
    }

    protected void enableSecondPartEmpty(boolean enableSecondPartEmptyView) {
        mEnableSecondPartEmpty = enableSecondPartEmptyView;
    }

    public void setInitLoadingLayoutId(@LayoutRes int layoutResId) {
        mInitLoadingLayoutId = layoutResId;
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

    protected void setSecondPartInitLoadingLayoutId(@LayoutRes int layoutResId) {
        mSecondPartInitLoadingLayoutId = layoutResId;
    }

    protected void setSecondPartEmptyLayoutId(@LayoutRes int layoutResId) {
        mSecondPartEmptyLayoutId = layoutResId;
    }

    public boolean isInitLoadingViewEnabled() {
        return mEnableInitLoading;
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

    protected boolean isSecondPartInitLoadingViewEnabled() {
        return mEnableSecondPartInitLoading;
    }

    protected boolean isSecondPartEmptyViewEnabled() {
        return mEnableSecondPartEmpty;
    }

    public boolean isErrorViewEnabled() {
        return mEnableError;
    }

    public boolean isErrorViewState() {
        return isErrorViewEnabled() && mIsErrorState;
    }

    public boolean isInitLoadingViewState() {
        return isInitLoadingViewEnabled() && mIsInitState;
    }

    public boolean isSecondPartInitLoadingViewState() {
        return isSecondPartInitLoadingViewEnabled() && mIsSecondPartInitState;
    }

    public BaseListViewHolder<String> getInitLoadingViewHolder(ViewGroup parent) {
        return new CompleteViewHolder(LayoutInflater.from(parent.getContext()).inflate(mInitLoadingLayoutId, parent, false));
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

    protected BaseListViewHolder<String> getSecondPartInitLoadingViewHolder(ViewGroup parent) {
        return new SecondPartInitLoadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(mSecondPartInitLoadingLayoutId, parent, false));
    }

    protected BaseListViewHolder<String> getSecondPartEmptyViewHolder(ViewGroup parent) {
        return new SecondPartEmptyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(mSecondPartEmptyLayoutId, parent, false));
    }

    protected void setNoDataItemState(boolean isNoDataState) {
        mIsNoDataItemState = isNoDataState;
    }

    /**
     * 当有数据被设置时回调
     */
    protected void onDataSet() {
        mIsErrorState = false;
        mIsInitState = false;
        lastOnLoadMoreScrollX = 0;
        lastOnLoadMoreScrollY = 0;
    }

    /**
     * 当有数据被设置时回调
     */
    protected void onSecondPartDataSet() {
        mIsErrorState = false;
        mIsSecondPartInitState = false;
        lastOnLoadMoreScrollX = 0;
        lastOnLoadMoreScrollY = 0;
    }

    /**
     * 当有数据被追加时回调
     */
    protected void onDataAdd() {
        mIsErrorState = false;
    }

    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        LogUtils.d(TAG, this.getClass() + " onAttachedToRecyclerView");
        mIsInitState = true;
        mIsSecondPartInitState = true;
        mIsErrorState = false;
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
        LogUtils.d(TAG, this.getClass() + " onDetachedFromRecyclerView");
        mRecyclerView = null;
        mLayoutManager = null;
    }

    public int getOriginDataItemViewType(int originDataPosition) {
        return DEFAULT_VIEW_HOLDER;
    }

    // 必须在setData或者addData之后调用（即在数据设置完成之后调用）
    public final void setErrorState() {
        mIsErrorState = true;
    }

    /**
     * 解决RecyclerView的assertNotInLayoutOrScroll引起的IllegalStateException
     */
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

    /**
     * 解决RecyclerView的assertNotInLayoutOrScroll引起的IllegalStateException
     *
     * @param positionStart
     * @param itemCount
     * @param payload
     */
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

    /**
     * 解决RecyclerView的assertNotInLayoutOrScroll引起的IllegalStateException
     *
     * @param positionStart
     * @param itemCount
     */
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

    /**
     * 解决RecyclerView的assertNotInLayoutOrScroll引起的IllegalStateException
     *
     * @param fromPosition
     * @param toPosition
     */
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

    /**
     * 解决RecyclerView的assertNotInLayoutOrScroll引起的IllegalStateException
     *
     * @param positionStart
     * @param itemCount
     */
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

    /**
     * 解决RecyclerView的assertNotInLayoutOrScroll引起的IllegalStateException
     *
     * @param positionStart
     * @param itemCount
     */
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
     * 表头ViewHolder
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
     * 初始化时加载的ViewHolder
     *
     * @param
     */
    public class InitLoadingViewHolder extends BaseListViewHolder<String> {
        private View container;

        public InitLoadingViewHolder(View itemView) {
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
     * 数据为空ViewHolder
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
     * 加载更多ViewHolder
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
     * 全部加载完成ViewHolder
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
     * 第二部分初始化时加载的ViewHolder
     *
     * @param
     */
    public class SecondPartInitLoadingViewHolder extends BaseListViewHolder<String> {
        private View container;

        public SecondPartInitLoadingViewHolder(View itemView) {
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
     * 第二部分空背景ViewHolder
     */
    public class SecondPartEmptyViewHolder extends BaseListViewHolder<String> {
        private View container;

        public SecondPartEmptyViewHolder(View itemView) {
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
     * 刷新加载时的错误ViewHolder
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
     * 加载更多时的错误ViewHolder
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
