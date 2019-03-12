package com.pine.mvvm.ui.activity;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pine.base.architecture.mvvm.ui.activity.BaseMvvmActionBarActivity;
import com.pine.mvvm.MvvmUrlConstants;
import com.pine.mvvm.R;
import com.pine.mvvm.bean.MvvmShopDetailEntity;
import com.pine.mvvm.databinding.MvvmShopDetailActivityBinding;
import com.pine.mvvm.vm.MvvmShopDetailVm;

/**
 * Created by tanghongfeng on 2018/10/9
 */

public class MvvmShopDetailActivity extends BaseMvvmActionBarActivity<MvvmShopDetailActivityBinding, MvvmShopDetailVm>
        implements SwipeRefreshLayout.OnRefreshListener {

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.mvvm_activity_shop_detail;
    }

    @Override
    protected void init() {
        initBindingAndVm();
        initView();
    }

    private void initBindingAndVm() {
        mBinding.setPresenter(new Presenter());

        mViewModel.getShopDetailData().observe(this, new Observer<MvvmShopDetailEntity>() {
            @Override
            public void onChanged(@Nullable MvvmShopDetailEntity mvvmShopDetailEntity) {
                mBinding.setShopDetail(mvvmShopDetailEntity);
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

        mBinding.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                onRefresh();
            }
        });
    }

    @Override
    protected void setupActionBar(ImageView goBackIv, TextView titleTv) {
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
        public void goShopH5Ui(View v) {
            Intent intent = new Intent(MvvmShopDetailActivity.this, MvvmWebViewActivity.class);
            intent.putExtra("url", MvvmUrlConstants.H5_DefaultUrl);
            startActivity(intent);
        }

        public void goTravelNoteListUi(View v) {
            Toast.makeText(MvvmShopDetailActivity.this, "goTravelNoteListUi", Toast.LENGTH_SHORT).show();
        }
    }
}
