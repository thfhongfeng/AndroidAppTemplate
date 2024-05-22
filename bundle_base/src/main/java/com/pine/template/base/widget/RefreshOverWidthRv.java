package com.pine.template.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pine.template.base.recycle_view.adapter.BaseListAdapter;
import com.pine.template.bundle_base.R;
import com.pine.tool.ui.Activity;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;

public class RefreshOverWidthRv extends LinearLayout {
    private final String TAG = this.getClass().getSimpleName();

    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;

    public RefreshOverWidthRv(Context context) {
        super(context);
        init();
    }

    public RefreshOverWidthRv(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        resolveAttrs(context, attrs);
    }

    public RefreshOverWidthRv(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resolveAttrs(context, attrs);
    }

    private void resolveAttrs(Context context, @Nullable AttributeSet attrs) {
        init();
    }

    private void init() {
        refreshLayout = new SwipeRefreshLayout(getContext());
        recyclerView = new RecyclerView(getContext());
        setOrientation(VERTICAL);
    }

    public void setup(Activity activity, View headView,
                      @NonNull RecyclerView.LayoutManager layoutManager,
                      @NonNull BaseListAdapter adapter,
                      IRecycleViewListener listener) {
        setup(activity, headView, headView, layoutManager, adapter, listener);
    }

    public void setup(Activity activity, View testWidthView, View headView,
                      @NonNull RecyclerView.LayoutManager layoutManager,
                      @NonNull BaseListAdapter adapter,
                      IRecycleViewListener listener) {
        removeAllViews();
        int calWidth = getWidthForView(activity, testWidthView);
        boolean needHorizontalScroll = calWidth > AppUtils.getScreenWidth(activity);
        LogUtils.d(TAG, "calWidth:" + calWidth + ", needHorizontalScroll:" + needHorizontalScroll);
        refreshLayout.addView(recyclerView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (needHorizontalScroll) {
            HorizontalScrollView scrollView = new HorizontalScrollView(getContext());
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(VERTICAL);
            if (headView != null) {
                layout.addView(headView, new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            layout.addView(refreshLayout, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            scrollView.addView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            addView(scrollView, new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            if (headView != null) {
                addView(headView, new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            addView(refreshLayout, new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        }
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        adapter.setOnScrollListener(recyclerView, new BaseListAdapter.IOnScrollListener() {
            @Override
            public void onLoadMore() {
                if (listener != null) {
                    listener.onLoadingMore();
                }
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (listener != null) {
                    listener.onRefresh();
                }
            }
        });
        refreshLayout.setColorSchemeResources(
                R.color.red,
                R.color.yellow,
                R.color.green
        );
        refreshLayout.setDistanceToTriggerSync(250);
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(true);
        }
        refreshLayout.setEnabled(true);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
                if (listener != null) {
                    listener.onRefresh();
                }
            }
        });
    }

    public RecyclerView getRecycleView() {
        return recyclerView;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return refreshLayout;
    }

    public int getWidthForView(Activity activity, View v) {
        if (v == null) {
            return 0;
        }
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;
        int height = metric.heightPixels;

        /**
         * UNSPECIFIED:会测出实际宽高（不受限于屏幕宽高）；
         * EXACTLY：固定为屏幕宽高
         * AT_MOST：会测出实际宽高（但受限于屏幕宽高，最大值为屏幕宽高）
         *
         * 因此使用UNSPECIFIED模式
         */
        int measuredWidth = MeasureSpec.makeMeasureSpec(width, MeasureSpec.UNSPECIFIED);
        int measuredHeight = MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED);

        v.measure(measuredWidth, measuredHeight);
        return v.getMeasuredWidth();
    }

    public interface IRecycleViewListener {
        void onRefresh();

        void onLoadingMore();
    }
}
