package com.pine.template.mvp.presenter;

import android.text.TextUtils;

import com.pine.template.base.component.map.LocationInfo;
import com.pine.template.base.component.map.MapSdkManager;
import com.pine.template.mvp.adapter.MvpShopListNoPaginationAdapter;
import com.pine.template.mvp.bean.MvpShopItemEntity;
import com.pine.template.mvp.contract.IMvpShopNoPaginationListContract;
import com.pine.template.mvp.model.MvpShopModel;
import com.pine.tool.architecture.mvp.presenter.Presenter;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.response.IAsyncResponse;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvpShopNoPaginationListPresenter extends Presenter<IMvpShopNoPaginationListContract.Ui>
        implements IMvpShopNoPaginationListContract.Presenter {
    private MvpShopModel mShopModel;
    private MvpShopListNoPaginationAdapter mMvpHomeItemAdapter;

    public MvpShopNoPaginationListPresenter() {
        mShopModel = new MvpShopModel();
    }

    @Override
    public MvpShopListNoPaginationAdapter getListAdapter() {
        if (mMvpHomeItemAdapter == null) {
            mMvpHomeItemAdapter = new MvpShopListNoPaginationAdapter();
            mMvpHomeItemAdapter.enableInitLoading(true);
        }
        return mMvpHomeItemAdapter;
    }

    @Override
    public void loadShopNoPaginationListData() {
        if (mIsLoadProcessing) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        LocationInfo location = MapSdkManager.getLocation();
        if (location != null) {
            params.put("latitude", String.valueOf(location.getLatitude()));
            params.put("longitude", String.valueOf(location.getLongitude()));
        }
        setUiLoading(true);
        mShopModel.requestShopListData(params, new IAsyncResponse<ArrayList<MvpShopItemEntity>>() {
            @Override
            public void onResponse(ArrayList<MvpShopItemEntity> list) {
                setUiLoading(false);
                if (isUiAlive()) {
                    mMvpHomeItemAdapter.setData(list);
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
