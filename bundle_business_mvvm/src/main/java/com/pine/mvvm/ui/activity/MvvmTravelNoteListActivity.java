package com.pine.mvvm.ui.activity;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pine.base.architecture.mvvm.ui.activity.BaseMvvmActionBarCustomMenuActivity;
import com.pine.config.ConfigKey;
import com.pine.config.switcher.ConfigSwitcherServer;
import com.pine.mvvm.R;
import com.pine.mvvm.adapter.MvvmTravelNoteListPaginationAdapter;
import com.pine.mvvm.bean.MvvmTravelNoteItemEntity;
import com.pine.mvvm.databinding.MvvmTravelListActivityBinding;
import com.pine.mvvm.vm.MvvmTravelListVm;

import java.util.ArrayList;

/**
 * Created by tanghongfeng on 2018/10/22
 */

public class MvvmTravelNoteListActivity extends
        BaseMvvmActionBarCustomMenuActivity<MvvmTravelListActivityBinding, MvvmTravelListVm>
        implements SwipeRefreshLayout.OnRefreshListener {
    private MvvmTravelNoteListPaginationAdapter mMvvmTravelListItemAdapter;

    @Override
    protected int getMenuBarLayoutResId() {
        return R.layout.mvvm_travel_note_list_menu;
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.mvvm_activity_travel_note_list;
    }

    @Override
    protected void init() {
        initBindingAndVm();
        initView();
    }

    private void initBindingAndVm() {
        mViewModel.getTravelListData().observe(this,
                new Observer<ArrayList<MvvmTravelNoteItemEntity>>() {
                    @Override
                    public void onChanged(@Nullable ArrayList<MvvmTravelNoteItemEntity> travelNoteList) {
                        if (mViewModel.getTravelListData().getCustomData()) {
                            mMvvmTravelListItemAdapter.setData(travelNoteList);
                        } else {
                            mMvvmTravelListItemAdapter.addData(travelNoteList);
                        }
                    }
                });
    }

    private void initView() {
        mBinding.swipeRefreshLayout.setOnRefreshListener(this);
        mBinding.swipeRefreshLayout.setColorSchemeResources(
                R.color.red,
                R.color.yellow,
                R.color.green
        );
        mBinding.swipeRefreshLayout.setDistanceToTriggerSync(250);
        if (mBinding.swipeRefreshLayout != null) {
            mBinding.swipeRefreshLayout.setRefreshing(true);
        }
        mBinding.swipeRefreshLayout.setEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.recycleView.setLayoutManager(linearLayoutManager);
        mBinding.recycleView.setHasFixedSize(true);
        mMvvmTravelListItemAdapter = new MvvmTravelNoteListPaginationAdapter(
                MvvmTravelNoteListPaginationAdapter.TRAVEL_NOTE_VIEW_HOLDER);
        View headView = LayoutInflater.from(this).inflate(R.layout.mvvm_item_travle_note_list_head, null);
        mMvvmTravelListItemAdapter.setHeadView(headView);
        mBinding.recycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mMvvmTravelListItemAdapter.isLastViewMoreView(recyclerView)) {
                    onLoadingMore();
                }
            }
        });
        mBinding.recycleView.setAdapter(mMvvmTravelListItemAdapter);

        mBinding.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mBinding.swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    @Override
    public void onSyncLiveDataInit(int liveDataObjTag) {

    }

    @Override
    protected void setupActionBar(ImageView goBackIv, TextView titleTv, View menuContainer) {
        titleTv.setText(R.string.mvvm_travel_note_list_title);
        if (ConfigSwitcherServer.getInstance().isEnable(ConfigKey.FUN_ADD_TRAVEL_NOTE_KEY)) {
            menuContainer.findViewById(R.id.menu_iv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MvvmTravelNoteListActivity.this, MvvmTravelNoteReleaseActivity.class);
                    intent.putExtra("id", mViewModel.mId);
                    startActivity(intent);
                }
            });
            menuContainer.findViewById(R.id.menu_iv).setVisibility(View.VISIBLE);
        } else {
            menuContainer.findViewById(R.id.menu_iv).setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        mViewModel.loadTravelNoteListData(true, 1,
                mMvvmTravelListItemAdapter.getPageSize());
    }

    public void onLoadingMore() {
        mViewModel.loadTravelNoteListData(false,
                mMvvmTravelListItemAdapter.getNextPageNo(), mMvvmTravelListItemAdapter.getPageSize());
    }

    @Override
    public void setLoadingUiVisibility(boolean processing) {
        if (mBinding.swipeRefreshLayout == null) {
            return;
        }
        mBinding.swipeRefreshLayout.setRefreshing(processing);
    }
}
