package com.pine.template.mvp.ui.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.pine.template.base.architecture.mvp.ui.activity.BaseMvpActionBarTextMenuActivity;
import com.pine.template.base.recycle_view.adapter.BaseListAdapter;
import com.pine.template.mvp.R;
import com.pine.template.mvp.adapter.MvpShopCheckListPaginationAdapter;
import com.pine.template.mvp.contract.IMvpShopSearchCheckContract;
import com.pine.template.mvp.presenter.MvpShopSearchCheckPresenter;
import com.pine.tool.bean.InputParam;
import com.pine.tool.util.KeyboardUtils;

/**
 * Created by tanghongfeng on 2018/11/15
 */

public class MvpShopSearchCheckActivity extends
        BaseMvpActionBarTextMenuActivity<IMvpShopSearchCheckContract.Ui, MvpShopSearchCheckPresenter>
        implements IMvpShopSearchCheckContract.Ui,
        SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private SwipeRefreshLayout swipe_refresh_layout;
    private RecyclerView recycle_view;
    private ImageView search_iv, search_key_clear_iv;
    private EditText search_key_et;
    private TextView clear_check_tv;

    @Override
    protected void setupActionBar(View actionbar, ImageView goBackIv, TextView titleTv, TextView menuBtnTv) {
        titleTv.setText(R.string.mvp_shop_check_title);
        menuBtnTv.setText(R.string.mvp_complete);
        menuBtnTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.completeAction();
            }
        });
    }

    @Override
    protected int getActivityLayoutResId() {
        return R.layout.mvp_activity_shop_search_check;
    }

    @Override
    protected void findViewOnCreate(Bundle savedInstanceState) {
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        recycle_view = findViewById(R.id.recycle_view);
        search_iv = findViewById(R.id.search_iv);
        search_key_clear_iv = findViewById(R.id.search_key_clear_iv);
        search_key_et = findViewById(R.id.search_key_et);
        clear_check_tv = findViewById(R.id.clear_check_tv);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        search_iv.setOnClickListener(this);
        search_key_clear_iv.setOnClickListener(this);
        clear_check_tv.setOnClickListener(this);
        search_key_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                    KeyboardUtils.closeSoftKeyboard(MvpShopSearchCheckActivity.this);
                    mPresenter.postSearch(true);
                    return true;
                }
                return false;
            }
        });

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycle_view.setLayoutManager(linearLayoutManager);
        recycle_view.setHasFixedSize(true);

        MvpShopCheckListPaginationAdapter adapter = mPresenter.getListAdapter();
        adapter.setOnScrollListener(recycle_view,
                new BaseListAdapter.IOnScrollListener() {
                    @Override
                    public void onLoadMore() {
                        onLoadingMore();
                    }
                });
        recycle_view.setAdapter(adapter);

        swipe_refresh_layout.post(new Runnable() {
            @Override
            public void run() {
                swipe_refresh_layout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    @Override
    public void onRefresh() {
        mPresenter.postSearch(true);
    }

    public void onLoadingMore() {
        mPresenter.postSearch(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_iv) {
            onRefresh();
        } else if (v.getId() == R.id.search_key_clear_iv) {
            search_key_et.setText("");
        } else if (v.getId() == R.id.clear_check_tv) {
            mPresenter.clearCurCheck();
        }
    }

    @Override
    public InputParam getSearchKey(String key) {
        return new InputParam(this, key, search_key_et.getText().toString());
    }

    @Override
    public void goAllSelectMode() {
        search_key_et.setText("");
        onRefresh();
    }

    @Override
    public void setLoadingUiVisibility(boolean processing) {
        if (swipe_refresh_layout == null) {
            return;
        }
        swipe_refresh_layout.setRefreshing(processing);
    }
}
