package com.pine.mvp.presenter;

import android.text.TextUtils;

import com.pine.base.component.map.LocationInfo;
import com.pine.base.component.map.MapSdkManager;
import com.pine.mvp.MvpConstants;
import com.pine.mvp.adapter.MvpShopListPaginationTreeAdapter;
import com.pine.mvp.bean.MvpShopAndProductEntity;
import com.pine.mvp.contract.IMvpShopTreeListContract;
import com.pine.mvp.model.MvpShopModel;
import com.pine.tool.architecture.mvp.model.IModelAsyncResponse;
import com.pine.tool.architecture.mvp.presenter.Presenter;
import com.pine.tool.exception.MessageException;

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
        params.put(MvpConstants.PAGE_NO, String.valueOf(pageNo));
        params.put(MvpConstants.PAGE_SIZE, String.valueOf(mMvpHomeItemAdapter.getPageSize()));
        LocationInfo location = MapSdkManager.getInstance().getLocation();
        if (location != null) {
            params.put("latitude", String.valueOf(location.getLatitude()));
            params.put("longitude", String.valueOf(location.getLongitude()));
        }
        setUiLoading(true);
        mShopModel.requestShopAndProductListData(params, new IModelAsyncResponse<ArrayList<MvpShopAndProductEntity>>() {
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
