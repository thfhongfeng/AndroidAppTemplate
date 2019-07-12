package com.pine.mvvm.ui.fragment;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.pine.base.component.map.ILocationListener;
import com.pine.base.component.map.LocationInfo;
import com.pine.base.component.map.MapSdkManager;
import com.pine.mvvm.R;
import com.pine.mvvm.adapter.MvvmShopListPaginationAdapter;
import com.pine.mvvm.bean.MvvmShopItemEntity;
import com.pine.mvvm.databinding.MvvmShopPaginationListFragmentBinding;
import com.pine.mvvm.vm.MvvmShopPaginationListVm;

import java.util.ArrayList;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvvmShopPaginationListFragment extends
        com.pine.tool.architecture.mvvm.ui.MvvmFragment<MvvmShopPaginationListFragmentBinding, MvvmShopPaginationListVm>
        implements SwipeRefreshLayout.OnRefreshListener {
    private MvvmShopListPaginationAdapter mMvvmHomeItemAdapter;

    @Override
    protected int getFragmentLayoutResId() {
        return R.layout.mvvm_fragment_shop_pagination_list;
    }

    @Override
    protected void init() {
        initBindingAndVm();
        initView();
    }

    private void initBindingAndVm() {
        mViewModel.getShopListData().observe(this, new Observer<ArrayList<MvvmShopItemEntity>>() {
            @Override
            public void onChanged(@Nullable ArrayList<MvvmShopItemEntity> mvvmShopItemEntities) {
                if (mViewModel.getShopListData().getCustomData()) {
                    mMvvmHomeItemAdapter.setData(mvvmShopItemEntities);
                } else {
                    mMvvmHomeItemAdapter.addData(mvvmShopItemEntities);
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
        mMvvmHomeItemAdapter = new MvvmShopListPaginationAdapter(
                MvvmShopListPaginationAdapter.SHOP_VIEW_HOLDER);
        mBinding.recycleView.setAdapter(mMvvmHomeItemAdapter);
        mBinding.recycleView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (mMvvmHomeItemAdapter.isLastViewMoreView(recyclerView)) {
                    onLoadingMore();
                }
            }
        });
        mBinding.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        });
    }

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
        mViewModel.loadShopPaginationListData(true, 1, mMvvmHomeItemAdapter.getPageSize());
    }

    public void onLoadingMore() {
        mViewModel.loadShopPaginationListData(false, mMvvmHomeItemAdapter.getNextPageNo(),
                mMvvmHomeItemAdapter.getPageSize());
    }

    @Override
    public void setLoadingUiVisibility(boolean visibility) {
        hideSoftInputFromWindow();
        mBinding.swipeRefreshLayout.setRefreshing(visibility);
    }
}
