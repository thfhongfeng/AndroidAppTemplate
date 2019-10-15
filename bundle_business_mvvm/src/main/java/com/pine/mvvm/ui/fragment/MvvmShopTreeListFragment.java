package com.pine.mvvm.ui.fragment;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.pine.base.component.map.ILocationListener;
import com.pine.base.component.map.LocationInfo;
import com.pine.base.component.map.MapSdkManager;
import com.pine.base.recycle_view.adapter.BaseListAdapter;
import com.pine.mvvm.R;
import com.pine.mvvm.adapter.MvvmShopListPaginationTreeAdapter;
import com.pine.mvvm.bean.MvvmShopAndProductEntity;
import com.pine.mvvm.databinding.MvvmShopTreeListFragmentBinding;
import com.pine.mvvm.vm.MvvmShopTreeListVm;
import com.pine.tool.architecture.mvvm.ui.MvvmFragment;

import java.util.ArrayList;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvvmShopTreeListFragment extends MvvmFragment<MvvmShopTreeListFragmentBinding, MvvmShopTreeListVm>
        implements SwipeRefreshLayout.OnRefreshListener {
    private MvvmShopListPaginationTreeAdapter mMvvmHomeItemAdapter;

    private ILocationListener mLocationListener = new ILocationListener() {
        @Override
        public void onReceiveLocation(LocationInfo locationInfo) {
            onRefresh();
        }

        @Override
        public void onReceiveFail() {

        }
    };

    @Override
    protected int getFragmentLayoutResId() {
        return R.layout.mvvm_fragment_shop_tree_list;
    }

    @Override
    protected void init() {
        initBindingAndVm();
        initView();
    }

    private void initBindingAndVm() {
        mViewModel.getShopListData().observe(this, new Observer<ArrayList<MvvmShopAndProductEntity>>() {
            @Override
            public void onChanged(@Nullable ArrayList<MvvmShopAndProductEntity> mvvmShopAndProductEntity) {
                if (mViewModel.getShopListData().getCustomData()) {
                    mMvvmHomeItemAdapter.setData(mvvmShopAndProductEntity);
                } else {
                    mMvvmHomeItemAdapter.addData(mvvmShopAndProductEntity);
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
        mBinding.swipeRefreshLayout.setEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.recycleView.setLayoutManager(linearLayoutManager);
        mBinding.recycleView.setHasFixedSize(true);
        mMvvmHomeItemAdapter = new MvvmShopListPaginationTreeAdapter();
        mMvvmHomeItemAdapter.setOnScrollListener(mBinding.recycleView,
                new BaseListAdapter.IOnScrollListener() {
                    @Override
                    public void onLoadMore() {
                        onLoadingMore();
                    }
                });
        mBinding.recycleView.setAdapter(mMvvmHomeItemAdapter);

        mBinding.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        });
    }

    @Override
    public void onSyncLiveDataInit(int liveDataObjTag) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (MapSdkManager.getInstance().getLocation() == null) {
            MapSdkManager.getInstance().registerLocationListener(mLocationListener);
            MapSdkManager.getInstance().startLocation();
        }
    }

    @Override
    public void onStop() {
        MapSdkManager.getInstance().unregisterLocationListener(mLocationListener);
        MapSdkManager.getInstance().stopLocation();
        super.onStop();
    }

    @Override
    public void onRefresh() {
        mViewModel.loadShopTreeListData(true, 1, mMvvmHomeItemAdapter.getPageSize());
    }

    public void onLoadingMore() {
        mViewModel.loadShopTreeListData(false,
                mMvvmHomeItemAdapter.getNextPageNo(), mMvvmHomeItemAdapter.getPageSize());
    }

    @Override
    public void setLoadingUiVisibility(boolean visibility) {
        hideSoftInputFromWindow();
        mBinding.swipeRefreshLayout.setRefreshing(visibility);
    }
}
