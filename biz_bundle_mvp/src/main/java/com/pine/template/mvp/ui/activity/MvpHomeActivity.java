package com.pine.template.mvp.ui.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.pine.app.template.biz_bundle_mvp.BuildConfigKey;
import com.pine.template.base.architecture.mvp.ui.activity.BaseMvpActionBarImageMenuActivity;
import com.pine.template.base.config.switcher.ConfigSwitcherServer;
import com.pine.template.base.widget.view.ViewPagerTabLayout;
import com.pine.template.mvp.R;
import com.pine.template.mvp.contract.IMvpHomeContract;
import com.pine.template.mvp.presenter.MvpHomePresenter;
import com.pine.template.mvp.ui.fragment.MvpShopNoPaginationListFragment;
import com.pine.template.mvp.ui.fragment.MvpShopPaginationListFragment;
import com.pine.template.mvp.ui.fragment.MvpShopTreeListFragment;
import com.pine.template.mvp.ui.fragment.MvpWebViewFragment;
import com.pine.tool.adapter.TabFragmentPagerAdapter;
import com.pine.tool.permission.PermissionsAnnotation;

/**
 * Created by tanghongfeng on 2018/9/13
 */

@PermissionsAnnotation(Permissions = {Manifest.permission.ACCESS_FINE_LOCATION})
public class MvpHomeActivity extends BaseMvpActionBarImageMenuActivity<IMvpHomeContract.Ui, MvpHomePresenter>
        implements IMvpHomeContract.Ui {
    private ViewPagerTabLayout view_pager_tab_layout;
    private ViewPager view_pager;

    @Override
    protected boolean beforeInitOnCreate(@Nullable Bundle savedInstanceState) {
        super.beforeInitOnCreate(savedInstanceState);
        setActionBarTag(ACTION_BAR_CENTER_TITLE_TAG | ACTION_BAR_NO_GO_BACK_TAG);
        return false;
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.mvp_activity_home;
    }

    @Override
    protected void findViewOnCreate(Bundle savedInstanceState) {
        view_pager_tab_layout = findViewById(R.id.view_pager_tab_layout);
        view_pager = findViewById(R.id.view_pager);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        setupViewPage();
    }

    private void setupViewPage() {
        view_pager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager(),
                new Fragment[]{
                        new MvpShopPaginationListFragment(), new MvpShopTreeListFragment(),
                        new MvpShopNoPaginationListFragment(), new MvpWebViewFragment()},
                new String[]{"PartA", "PartB", "PartC", "PartD"}));
        view_pager_tab_layout.setupWithViewPager(view_pager);
    }

    @Override
    protected void setupActionBar(View actionbar, ImageView goBackIv, TextView titleTv, ImageView menuBtnIv) {
        titleTv.setText(R.string.mvp_home_title);
        if (ConfigSwitcherServer.isEnable(BuildConfigKey.FUN_ADD_SHOP)) {
            menuBtnIv.setImageResource(R.mipmap.base_ic_add);
            menuBtnIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.goToAddShopActivity();
                }
            });
            menuBtnIv.setVisibility(View.VISIBLE);
        } else {
            menuBtnIv.setVisibility(View.GONE);
        }
    }
}
