package com.pine.template.mvp.presenter;

import android.text.TextUtils;

import com.pine.template.base.component.map.LocationInfo;
import com.pine.template.base.component.map.MapSdkManager;
import com.pine.template.mvp.MvpKeyConstants;
import com.pine.template.mvp.adapter.MvpShopListPaginationTreeAdapter;
import com.pine.template.mvp.bean.MvpShopAndProductEntity;
import com.pine.template.mvp.contract.IMvpShopTreeListContract;
import com.pine.template.mvp.model.MvpShopModel;
import com.pine.tool.architecture.mvp.presenter.Presenter;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.response.IAsyncResponse;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvpShopTreeListPresenter extends Presenter<IMvpShopTreeListContract.Ui>
        implements IMvpShopTreeListContract.Presenter {
    private MvpShopModel mShopModel;
    private MvpShopListPaginationTreeAdapter mMvpHomeItemAdapter;

    public MvpShopTreeListPresenter() {
        mShopModel = new MvpShopModel();
    }

    @Override
    public MvpShopListPaginationTreeAdapter getListAdapter() {
        if (mMvpHomeItemAdapter == null) {
            mMvpHomeItemAdapter = new MvpShopListPaginationTreeAdapter();
            mMvpHomeItemAdapter.enableInitLoading(true);
        }
        return mMvpHomeItemAdapter;
    }

    @Override
    public void loadShopTreeListData(final boolean refresh) {
        if (mIsLoadProcessing) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        int pageNo = 1;
        if (!refresh) {
            pageNo = mMvpHomeItemAdapter.getNextPageNo();
        }
        params.put(MvpKeyConstants.PAGE_NO, String.valueOf(pageNo));
        params.put(MvpKeyConstants.PAGE_SIZE, String.valueOf(mMvpHomeItemAdapter.getPageSize()));
        LocationInfo location = MapSdkManager.getLocation();
        if (location != null) {
            params.put("latitude", String.valueOf(location.getLatitude()));
            params.put("longitude", String.valueOf(location.getLongitude()));
        }
        setUiLoading(true);
        mShopModel.requestShopAndProductListData(params, new IAsyncResponse<ArrayList<MvpShopAndProductEntity>>() {
            @Override
            public void onResponse(ArrayList<MvpShopAndProductEntity> list) {
                setUiLoading(false);
                if (isUiAlive()) {
                    if (refresh) {
                        mMvpHomeItemAdapter.setData(list);
                    } else {
                        mMvpHomeItemAdapter.addData(list);
                    }
                }
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
                if (e instanceof MessageException) {
                    if (!TextUtils.isEmpty(e.getMessage())) {
                        showShortToast(e.getMessage());
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onCancel() {
                setUiLoading(false);
            }
        });
    }
}
