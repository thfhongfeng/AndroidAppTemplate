package com.pine.mvp.ui.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pine.base.architecture.mvp.ui.activity.BaseMvpActionBarCustomMenuActivity;
import com.pine.base.recycle_view.adapter.BaseListAdapter;
import com.pine.mvp.R;
import com.pine.mvp.adapter.MvpTravelNoteListPaginationAdapter;
import com.pine.mvp.contract.IMvpTravelNoteListContract;
import com.pine.mvp.presenter.MvpTravelNoteListPresenter;

/**
 * Created by tanghongfeng on 2018/10/22
 */

public class MvpTravelNoteListActivity extends
        BaseMvpActionBarCustomMenuActivity<IMvpTravelNoteListContract.Ui, MvpTravelNoteListPresenter>
        implements IMvpTravelNoteListContract.Ui, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipe_refresh_layout;
    private RecyclerView recycle_view;

    @Override
    protected int getMenuBarLayoutResId() {
        return R.layout.mvp_travel_note_list_menu;
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.mvp_activity_travel_note_list;
    }

    @Override
    protected void findViewOnCreate() {
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        recycle_view = findViewById(R.id.recycle_view);
    }

    @Override
    protected void init() {
        swipe_refresh_layout.setOnRefreshListener(this);
        swipe_refresh_layout.setColorSchemeResources(
                R.color.red,
                R.color.yellow,
                R.color.green
        );
        swipe_refresh_layout.setDistanceToTriggerSync(250);
        if (swipe_refresh_layout != null) {
            swipe_refresh_layout.setRefreshing(true);
        }
        swipe_refresh_layout.setEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle_view.setLayoutManager(linearLayoutManager);
        recycle_view.setHasFixedSize(true);

        MvpTravelNoteListPaginationAdapter adapter = mPresenter.getListAdapter();
        View headView = LayoutInflater.from(this).inflate(R.layout.mvp_item_travle_note_list_head, null);
        adapter.setHeadView(headView);
        adapter.setOnScrollListener(recycle_view,
                new BaseListAdapter.IOnScrollListener() {
                    @Override
                    public void onLoadMore() {
                        onLoadingMore();
                    }
                });
        recycle_view.setAdapter(adapter);

        swipe_refresh_layout.post(new Runnable() {
            @Override
            public void run() {
                swipe_refresh_layout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    @Override
    protected void setupActionBar(ImageView goBackIv, TextView titleTv, View menuContainer) {
        titleTv.setText(R.string.mvp_travel_note_list_title);
        menuContainer.findViewById(R.id.menu_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.goToAddTravelNoteActivity();
            }
        });
    }

    @Override
    public void onRefresh() {
        mPresenter.loadTravelNoteListData(true);
    }

    public void onLoadingMore() {
        mPresenter.loadTravelNoteListData(false);
    }

    @Override
    public void setLoadingUiVisibility(boolean processing) {
        if (swipe_refresh_layout == null) {
            return;
        }
        swipe_refresh_layout.setRefreshing(processing);
    }
}
