package com.pine.template.mvp.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pine.template.base.component.map.ILocationListener;
import com.pine.template.base.component.map.LocationActionType;
import com.pine.template.base.component.map.LocationInfo;
import com.pine.template.base.component.map.MapSdkManager;
import com.pine.template.mvp.R;
import com.pine.template.mvp.contract.IMvpShopNoPaginationListContract;
import com.pine.template.mvp.presenter.MvpShopNoPaginationListPresenter;
import com.pine.tool.architecture.mvp.ui.MvpFragment;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvpShopNoPaginationListFragment extends MvpFragment<IMvpShopNoPaginationListContract.Ui, MvpShopNoPaginationListPresenter>
        implements IMvpShopNoPaginationListContract.Ui, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private SwipeRefreshLayout swipe_refresh_layout;
    private RecyclerView recycle_view;
    private TextView refresh_btn_tv;

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
        return R.layout.mvp_fragment_shop_no_pagination_list;
    }

    @Override
    protected void findViewOnCreateView(View layout, Bundle savedInstanceState) {
        swipe_refresh_layout = layout.findViewById(R.id.swipe_refresh_layout);
        recycle_view = layout.findViewById(R.id.recycle_view);
        refresh_btn_tv = layout.findViewById(R.id.refresh_btn_tv);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle_view.setLayoutManager(linearLayoutManager);
        recycle_view.setHasFixedSize(true);
        recycle_view.setAdapter(mPresenter.getListAdapter());

        swipe_refresh_layout.post(new Runnable() {
            @Override
            public void run() {
                swipe_refresh_layout.setRefreshing(true);
                onRefresh();
            }
        });
        initEvent();
    }

    private void initEvent() {
        refresh_btn_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.refresh_btn_tv) {
            onRefresh();
        }
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
        mPresenter.loadShopNoPaginationListData();
    }

    @Override
    public void setLoadingUiVisibility(boolean processing) {
        if (swipe_refresh_layout == null) {
            return;
        }
        swipe_refresh_layout.setRefreshing(processing);
    }
}
