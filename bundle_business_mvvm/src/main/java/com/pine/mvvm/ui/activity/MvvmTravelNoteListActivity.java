package com.pine.mvvm.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pine.base.architecture.mvvm.ui.activity.BaseMvvmActionBarCustomMenuActivity;
import com.pine.base.recycle_view.adapter.BaseListAdapter;
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
    public void observeInitLiveData(Bundle savedInstanceState) {
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

    @Override
    protected int getMenuBarLayoutResId() {
        return R.layout.mvvm_travel_note_list_menu;
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.mvvm_activity_travel_note_list;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();
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
        mMvvmTravelListItemAdapter.setOnScrollListener(mBinding.recycleView,
                new BaseListAdapter.IOnScrollListener() {
                    @Override
                    public void onLoadMore() {
                        onLoadingMore();
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
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    @Override
    protected void setupActionBar(ImageView goBackIv, TextView titleTv, View menuContainer) {
        titleTv.setText(R.string.mvvm_travel_note_list_title);
        menuContainer.findViewById(R.id.menu_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MvvmTravelNoteListActivity.this, MvvmTravelNoteReleaseActivity.class);
                intent.putExtra("id", mViewModel.mId);
                startActivity(intent);
            }
        });
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
