package com.pine.template.mvvm.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pine.template.base.component.map.ILocationListener;
import com.pine.template.base.component.map.LocationActionType;
import com.pine.template.base.component.map.LocationInfo;
import com.pine.template.base.component.map.MapSdkManager;
import com.pine.template.mvvm.R;
import com.pine.template.mvvm.adapter.MvvmShopListNoPaginationAdapter;
import com.pine.template.mvvm.bean.MvvmShopItemEntity;
import com.pine.template.mvvm.databinding.MvvmShopNoPaginationListFragmentBinding;
import com.pine.template.mvvm.vm.MvvmShopNoPaginationListVm;
import com.pine.tool.architecture.mvvm.ui.MvvmFragment;

import java.util.ArrayList;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvvmShopNoPaginationListFragment extends
        MvvmFragment<MvvmShopNoPaginationListFragmentBinding, MvvmShopNoPaginationListVm>
        implements SwipeRefreshLayout.OnRefreshListener {
    private MvvmShopListNoPaginationAdapter mMvvmHomeItemAdapter;

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
    public void observeInitLiveData(Bundle savedInstanceState) {
        mViewModel.getShopListData().observe(this, new Observer<ArrayList<MvvmShopItemEntity>>() {
            @Override
            public void onChanged(@Nullable ArrayList<MvvmShopItemEntity> mvvmShopItemEntities) {
                mMvvmHomeItemAdapter.setData(mvvmShopItemEntities);
            }
        });
    }

    @Override
    protected int getFragmentLayoutResId() {
        return R.layout.mvvm_fragment_shop_no_pagination_list;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mBinding.setPresenter(new Presenter());
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
        mMvvmHomeItemAdapter = new MvvmShopListNoPaginationAdapter();
        mMvvmHomeItemAdapter.enableInitLoading(true);
        mBinding.recycleView.setAdapter(mMvvmHomeItemAdapter);
        mBinding.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        });
    }

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
        mViewModel.loadShopNoPaginationListData();
    }

    @Override
    public void setLoadingUiVisibility(boolean visibility) {
        hideSoftInputFromWindow();
        mBinding.swipeRefreshLayout.setRefreshing(visibility);
    }

    public class Presenter {
        public void onRefreshBtnClick(View view) {
            onRefresh();
        }
    }

}
