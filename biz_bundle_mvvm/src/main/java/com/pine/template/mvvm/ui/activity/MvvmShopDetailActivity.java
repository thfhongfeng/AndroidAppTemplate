package com.pine.template.mvvm.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pine.app.template.biz_bundle_mvvm.BuildConfigKey;
import com.pine.template.base.architecture.mvvm.ui.activity.BaseMvvmActionBarActivity;
import com.pine.template.base.component.map.MapSdkManager;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.template.mvvm.MvvmUrlConstants;
import com.pine.template.mvvm.R;
import com.pine.template.mvvm.bean.MvvmShopDetailEntity;
import com.pine.template.mvvm.databinding.MvvmShopDetailActivityBinding;
import com.pine.template.mvvm.vm.MvvmShopDetailVm;
import com.pine.tool.util.DecimalUtils;

/**
 * Created by tanghongfeng on 2018/10/9
 */

public class MvvmShopDetailActivity extends BaseMvvmActionBarActivity<MvvmShopDetailActivityBinding, MvvmShopDetailVm>
        implements SwipeRefreshLayout.OnRefreshListener {

    @Override
    public void observeInitLiveData(Bundle savedInstanceState) {
        mViewModel.getShopDetailData().observe(this, new Observer<MvvmShopDetailEntity>() {
            @Override
            public void onChanged(@Nullable MvvmShopDetailEntity mvvmShopDetailEntity) {
                mBinding.setShopDetail(mvvmShopDetailEntity);
            }
        });
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.mvvm_activity_shop_detail;
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

        mBinding.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        });

        mBinding.photoIuv.init(this);

        mBinding.goAddProductBtnTv.setVisibility(ConfigSwitcherServer
                .isEnable(BuildConfigKey.FUN_ADD_PRODUCT) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    @Override
    protected void setupActionBar(View actionbar, ImageView goBackIv, TextView titleTv) {
        titleTv.setText(R.string.mvvm_shop_detail_title);
    }

    @Override
    public void onRefresh() {
        mViewModel.loadShopDetailData();
    }

    @Override
    public void setLoadingUiVisibility(boolean visibility) {
        hideSoftInputFromWindow();
        mBinding.swipeRefreshLayout.setRefreshing(visibility);
    }

    public class Presenter {
        public void onAddressMarkerTvClick(View view) {
            String marker = mBinding.addressMarkerTv.getText().toString();
            double[] latLng = new double[2];
            latLng[0] = -1;
            latLng[1] = -1;
            if (!TextUtils.isEmpty(marker)) {
                String[] latLngStr = marker.split(",");
                if (latLngStr.length == 2) {
                    latLng[0] = DecimalUtils.format(latLngStr[0].trim(), 6);
                    latLng[1] = DecimalUtils.format(latLngStr[1].trim(), 6);
                    Intent intent = MapSdkManager.getMarkMapActivityIntent(
                            MvvmShopDetailActivity.this, latLng[0], latLng[1], false);
                    if (intent != null) {
                        startActivity(intent);
                    }
                }
            }
        }

        public void goShopH5Ui(View v) {
            Intent intent = new Intent(MvvmShopDetailActivity.this, MvvmWebViewActivity.class);
            intent.putExtra("url", MvvmUrlConstants.H5_DefaultUrl());
            startActivity(intent);
        }

        public void goTravelNoteListUi(View v, String id) {
            Intent intent = new Intent(MvvmShopDetailActivity.this, MvvmTravelNoteListActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }

        public void goAddProductUi(View v, String id) {
            Intent intent = new Intent(MvvmShopDetailActivity.this, MvvmProductReleaseActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }
}
