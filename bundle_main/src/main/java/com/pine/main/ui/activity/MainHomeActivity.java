package com.pine.main.ui.activity;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;

import com.pine.base.architecture.mvvm.ui.activity.BaseMvvmNoActionBarActivity;
import com.pine.main.R;
import com.pine.main.adapter.MainBusinessAdapter;
import com.pine.main.bean.MainBusinessItemEntity;
import com.pine.main.databinding.MainHomeActivityBinding;
import com.pine.main.vm.MainHomeVm;
import com.pine.tool.widget.decor.GridSpacingItemDecoration;

import java.util.ArrayList;

public class MainHomeActivity extends BaseMvvmNoActionBarActivity<MainHomeActivityBinding, MainHomeVm> {
    private MainBusinessAdapter mMainBusinessAdapter;

    @Override
    public void initLiveDataObserver() {
        mViewModel.getBusinessBundleListData().observe(this,
                new Observer<ArrayList<MainBusinessItemEntity>>() {
                    @Override
                    public void onChanged(@Nullable ArrayList<MainBusinessItemEntity> list) {
                        mMainBusinessAdapter.setData(list);
                    }
                });
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.main_activity_home;
    }

    @Override
    protected void init() {
        initView();
    }

    private void initView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mBinding.businessRv.setLayoutManager(layoutManager);
        mBinding.businessRv.addItemDecoration(new GridSpacingItemDecoration(3,
                getResources().getDimensionPixelOffset(R.dimen.dp_10), true));
        mBinding.businessRv.setHasFixedSize(true);
        mMainBusinessAdapter = new MainBusinessAdapter(
                MainBusinessAdapter.BUSINESS_VIEW_HOLDER);
        mMainBusinessAdapter.enableEmptyComplete(true, false);
        mBinding.businessRv.setAdapter(mMainBusinessAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.loadBusinessBundleData();
    }

    @Override
    public void onSyncLiveDataInit(int liveDataObjTag) {

    }
}
