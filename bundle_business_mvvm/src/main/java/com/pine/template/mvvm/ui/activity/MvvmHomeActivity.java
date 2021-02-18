package com.pine.template.mvvm.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pine.template.base.architecture.mvvm.ui.activity.BaseMvvmActionBarImageMenuActivity;
import com.pine.template.config.ConfigKey;
import com.pine.template.config.switcher.ConfigSwitcherServer;
import com.pine.template.mvvm.R;
import com.pine.template.mvvm.databinding.MvvmHomeActivityBinding;
import com.pine.template.mvvm.ui.fragment.MvvmShopNoPaginationListFragment;
import com.pine.template.mvvm.ui.fragment.MvvmShopPaginationListFragment;
import com.pine.template.mvvm.ui.fragment.MvvmShopTreeListFragment;
import com.pine.template.mvvm.ui.fragment.MvvmWebViewFragment;
import com.pine.template.mvvm.vm.MvvmHomeVm;
import com.pine.tool.adapter.TabFragmentPagerAdapter;
import com.pine.tool.permission.PermissionsAnnotation;

/**
 * Created by tanghongfeng on 2019/2/25
 */

@PermissionsAnnotation(Permissions = {Manifest.permission.ACCESS_FINE_LOCATION})
public class MvvmHomeActivity extends BaseMvvmActionBarImageMenuActivity<MvvmHomeActivityBinding, MvvmHomeVm> {

    @Override
    public void observeInitLiveData(Bundle savedInstanceState) {

    }

    @Override
    protected boolean beforeInitOnCreate(@Nullable Bundle savedInstanceState) {
        super.beforeInitOnCreate(savedInstanceState);
        setActionBarTag(ACTION_BAR_CENTER_TITLE_TAG | ACTION_BAR_NO_GO_BACK_TAG);
        return false;
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.mvvm_activity_home;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mBinding.setPresenter(new Presenter());
        initView();
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
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    @Override
    protected void setupActionBar(View actionbar, ImageView goBackIv, TextView titleTv, ImageView menuBtnIv) {
        titleTv.setText(R.string.mvvm_home_title);
        if (ConfigSwitcherServer.getInstance().isEnable(ConfigKey.FUN_ADD_SHOP_KEY)) {
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
