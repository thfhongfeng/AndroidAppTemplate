package com.pine.mvvm.ui.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pine.base.component.map.ILocationListener;
import com.pine.base.component.map.LocationInfo;
import com.pine.base.component.map.LocationActionType;
import com.pine.base.component.map.MapSdkManager;
import com.pine.base.recycle_view.adapter.BaseListAdapter;
import com.pine.mvvm.R;
import com.pine.mvvm.adapter.MvvmShopListPaginationAdapter;
import com.pine.mvvm.bean.MvvmShopItemEntity;
import com.pine.mvvm.databinding.MvvmShopPaginationListFragmentBinding;
import com.pine.mvvm.vm.MvvmShopPaginationListVm;
import com.pine.tool.architecture.mvvm.ui.MvvmFragment;

import java.util.ArrayList;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvvmShopPaginationListFragment extends
        MvvmFragment<MvvmShopPaginationListFragmentBinding, MvvmShopPaginationListVm>
        implements SwipeRefreshLayout.OnRefreshListener {
    private MvvmShopListPaginationAdapter mMvvmHomeItemAdapter;

    @Override
    public void observeInitLiveData(Bundle savedInstanceState) {
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

    @Override
    protected int getFragmentLayoutResId() {
        return R.layout.mvvm_fragment_shop_pagination_list;
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
        mBinding.swipeRefreshLayout.setEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.recycleView.setLayoutManager(linearLayoutManager);
        mBinding.recycleView.setHasFixedSize(true);
        mMvvmHomeItemAdapter = new MvvmShopListPaginationAdapter(
                MvvmShopListPaginationAdapter.SHOP_VIEW_HOLDER);
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
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    @Override
    public void onResume() {
        super.onResume();
        MapSdkManager.registerLocationListener(mLocationListener, LocationActionType.ONCE);
    }

    @Override
    public void onStop() {
        MapSdkManager.unregisterLocationListener(mLocationListener);
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
