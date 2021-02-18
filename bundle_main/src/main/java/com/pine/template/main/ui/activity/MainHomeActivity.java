package com.pine.template.main.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;

import com.pine.template.base.architecture.mvvm.ui.activity.BaseMvvmNoActionBarActivity;
import com.pine.template.main.R;
import com.pine.template.main.adapter.MainBusinessAdapter;
import com.pine.template.main.bean.MainBusinessItemEntity;
import com.pine.template.main.databinding.MainHomeActivityBinding;
import com.pine.template.main.vm.MainHomeVm;
import com.pine.tool.widget.decor.GridSpacingItemDecoration;

import java.util.ArrayList;

public class MainHomeActivity extends BaseMvvmNoActionBarActivity<MainHomeActivityBinding, MainHomeVm> {
    private MainBusinessAdapter mMainBusinessAdapter;

    @Override
    public void observeInitLiveData(Bundle savedInstanceState) {
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
    protected void init(Bundle savedInstanceState) {
        initView();
    }

    private void initView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mBinding.businessRv.setLayoutManager(layoutManager);
        mBinding.businessRv.addItemDecoration(new GridSpacingItemDecoration(3,
                getResources().getDimensionPixelOffset(R.dimen.dp_10), true));
        mBinding.businessRv.setHasFixedSize(true);
        mMainBusinessAdapter = new MainBusinessAdapter();
        mMainBusinessAdapter.enableEmptyComplete(true, false);
        mBinding.businessRv.setAdapter(mMainBusinessAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.loadBusinessBundleData();
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }
}
