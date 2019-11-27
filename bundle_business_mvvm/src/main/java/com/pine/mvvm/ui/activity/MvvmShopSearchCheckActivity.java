package com.pine.mvvm.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pine.base.architecture.mvvm.ui.activity.BaseMvvmActionBarTextMenuActivity;
import com.pine.base.recycle_view.adapter.BaseListAdapter;
import com.pine.mvvm.R;
import com.pine.mvvm.adapter.MvvmShopCheckListPaginationAdapter;
import com.pine.mvvm.bean.MvvmShopItemEntity;
import com.pine.mvvm.bean.MvvmShopSearchBean;
import com.pine.mvvm.databinding.MvvmShopSearchCheckActivityBinding;
import com.pine.mvvm.vm.MvvmShopSearchCheckVm;
import com.pine.tool.util.KeyboardUtils;

import java.util.ArrayList;
import java.util.Map;

public class MvvmShopSearchCheckActivity extends
        BaseMvvmActionBarTextMenuActivity<MvvmShopSearchCheckActivityBinding, MvvmShopSearchCheckVm>
        implements SwipeRefreshLayout.OnRefreshListener {
    public final static String RESULT_CHECKED_LIST_KEY = "result_checked_list_key";
    public final static String REQUEST_CHECKED_LIST_KEY = "request_checked_list_key";

    private MvvmShopCheckListPaginationAdapter mShopCheckListPaginationAdapter;

    @Override
    public void observeInitLiveData() {
        mViewModel.getSearchKey().observe(this, new Observer<MvvmShopSearchBean>() {
            @Override
            public void onChanged(@Nullable MvvmShopSearchBean searchBean) {
                mBinding.setSearchBean(searchBean);
            }
        });

        mViewModel.getShopListData().observe(this, new Observer<ArrayList<MvvmShopItemEntity>>() {
            @Override
            public void onChanged(@Nullable ArrayList<MvvmShopItemEntity> mvvmShopItemEntities) {
                if (mViewModel.getShopListData().getCustomData()) {
                    mShopCheckListPaginationAdapter.setData(mvvmShopItemEntities, mViewModel.mSearchMode);
                } else {
                    mShopCheckListPaginationAdapter.addData(mvvmShopItemEntities);
                }
            }
        });
    }

    @Override
    protected void setupActionBar(ImageView goBackIv, TextView titleTv, TextView menuBtnTv) {
        titleTv.setText(R.string.mvvm_shop_check_title);
        menuBtnTv.setText(R.string.mvvm_complete);
        menuBtnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeAction();
            }
        });
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.mvvm_activity_shop_search_check;
    }

    @Override
    protected void init() {
        mBinding.setPresenter(new Presenter());
        initView();
    }

    private void initView() {
        mBinding.searchKeyEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    KeyboardUtils.closeSoftKeyboard(MvvmShopSearchCheckActivity.this);
                    onRefresh();
                    return true;
                }
                return false;
            }
        });

        mBinding.swipeRefreshLayout.setOnRefreshListener(this);
        mBinding.swipeRefreshLayout.setColorSchemeResources(
                R.color.red,
                R.color.yellow,
                R.color.green
        );
        mBinding.swipeRefreshLayout.setDistanceToTriggerSync(250);
        if (mBinding.swipeRefreshLayout != null) {
            mBinding.swipeRefreshLayout.setRefreshing(true);
        }
        mBinding.swipeRefreshLayout.setEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.recycleView.setLayoutManager(linearLayoutManager);
        mBinding.recycleView.setHasFixedSize(true);
        mShopCheckListPaginationAdapter = new MvvmShopCheckListPaginationAdapter(
                mViewModel.mInitBelongShopList,
                MvvmShopCheckListPaginationAdapter.SHOP_CHECK_VIEW_HOLDER);
        mShopCheckListPaginationAdapter.setOnScrollListener(mBinding.recycleView,
                new BaseListAdapter.IOnScrollListener() {
                    @Override
                    public void onLoadMore() {
                        onLoadingMore();
                    }
                });
        mBinding.recycleView.setAdapter(mShopCheckListPaginationAdapter);

        mBinding.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mBinding.swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    @Override
    public void observeSyncLiveData(int liveDataObjTag) {

    }

    @Override
    public void onRefresh() {
        mViewModel.postSearch(true, 1, mShopCheckListPaginationAdapter.getPageSize());
    }

    public void onLoadingMore() {
        mViewModel.postSearch(false,
                mShopCheckListPaginationAdapter.getNextPageNo(),
                mShopCheckListPaginationAdapter.getPageSize());
    }

    public void goAllSelectMode() {
        mBinding.searchKeyEt.setText("");
        onRefresh();
    }

    public void completeAction() {
        if (mViewModel.mSearchMode) {
            goAllSelectMode();
        } else {
            Map<String, MvvmShopItemEntity> checkedData = mShopCheckListPaginationAdapter.getAllCheckedData();
            if (checkedData == null || checkedData.size() < 1) {
                showShortToast(R.string.mvvm_shop_check_item_need);
                return;
            }
            ArrayList<MvvmShopItemEntity> list = new ArrayList<>();
            for (MvvmShopItemEntity entity : checkedData.values()) {
                list.add(entity);
            }
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(RESULT_CHECKED_LIST_KEY, list);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void setLoadingUiVisibility(boolean processing) {
        if (mBinding.swipeRefreshLayout == null) {
            return;
        }
        mBinding.swipeRefreshLayout.setRefreshing(processing);
    }

    public class Presenter {
        public void onSearchBtnClick(View view) {
            onRefresh();
        }

        public void onClearSearchKeyClick(View view) {
            mViewModel.setSearchKey(new MvvmShopSearchBean());
        }

        public void onClearCheckListClick(View view) {
            mShopCheckListPaginationAdapter.clearCheckedData();
        }
    }
}
