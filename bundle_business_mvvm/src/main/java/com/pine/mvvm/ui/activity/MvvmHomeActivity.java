package com.pine.mvvm.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pine.base.architecture.mvvm.ui.activity.BaseMvvmActionBarImageMenuActivity;
import com.pine.base.permission.PermissionsAnnotation;
import com.pine.config.switcher.ConfigFuncSwitcher;
import com.pine.mvvm.R;
import com.pine.mvvm.databinding.MvvmHomeActivityBinding;
import com.pine.mvvm.ui.fragment.MvvmShopNoPaginationListFragment;
import com.pine.mvvm.ui.fragment.MvvmShopPaginationListFragment;
import com.pine.mvvm.ui.fragment.MvvmShopTreeListFragment;
import com.pine.mvvm.ui.fragment.MvvmWebViewFragment;
import com.pine.mvvm.vm.MvvmHomeVm;
import com.pine.tool.adapter.TabFragmentPagerAdapter;

/**
 * Created by tanghongfeng on 2019/2/25
 */

@PermissionsAnnotation(Permissions = {Manifest.permission.ACCESS_FINE_LOCATION})
public class MvvmHomeActivity extends BaseMvvmActionBarImageMenuActivity<MvvmHomeActivityBinding, MvvmHomeVm> {

    @Override
    protected void beforeInitOnCreate(@Nullable Bundle savedInstanceState) {
        super.beforeInitOnCreate(savedInstanceState);
        setActionBarTag(ACTION_BAR_CENTER_TITLE_TAG | ACTION_BAR_NO_GO_BACK_TAG);
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.mvvm_activity_home;
    }

    @Override
    protected void init() {
        initBindingAndVm();
        initView();
    }

    private void initBindingAndVm() {
        mBinding.setPresenter(new Presenter());
    }

    private void initView() {
        mBinding.viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager(),
                new Fragment[]{
                        new MvvmShopPaginationListFragment(), new MvvmShopTreeListFragment(),
                        new MvvmShopNoPaginationListFragment(), new MvvmWebViewFragment()},
                new String[]{"PartA", "PartB", "PartC", "PartD"}));
        mBinding.viewPagerTabLayout.setupWithViewPager(mBinding.viewPager);
    }

    @Override
    public void onSyncLiveDataInit(int liveDataObjTag) {

    }

    @Override
    protected void setupActionBar(ImageView goBackIv, TextView titleTv, ImageView menuBtnIv) {
        titleTv.setText(R.string.mvvm_home_title);
        if (ConfigFuncSwitcher.getInstance().canAddProduct()) {
            menuBtnIv.setImageResource(R.mipmap.base_ic_add);
            menuBtnIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MvvmHomeActivity.this, MvvmShopReleaseActivity.class);
                    startActivity(intent);
                }
            });
            menuBtnIv.setVisibility(View.VISIBLE);
        } else {
            menuBtnIv.setVisibility(View.GONE);
        }
    }

    public class Presenter {

    }
}