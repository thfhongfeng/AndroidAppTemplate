package com.pine.template.mvp.presenter;

import android.text.TextUtils;

import com.pine.template.base.component.map.ILocationListener;
import com.pine.template.base.component.map.LocationActionType;
import com.pine.template.base.component.map.LocationInfo;
import com.pine.template.base.component.map.MapSdkManager;
import com.pine.template.mvp.MvpKeyConstants;
import com.pine.template.mvp.adapter.MvpShopListPaginationAdapter;
import com.pine.template.mvp.bean.MvpShopItemEntity;
import com.pine.template.mvp.contract.IMvpShopPaginationContract;
import com.pine.template.mvp.model.MvpShopModel;
import com.pine.tool.architecture.mvp.presenter.Presenter;
import com.pine.tool.architecture.state.UiState;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.response.IAsyncResponse;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvpShopPaginationListPresenter extends Presenter<IMvpShopPaginationContract.Ui>
        implements IMvpShopPaginationContract.Presenter {
    private MvpShopModel mShopModel;
    private MvpShopListPaginationAdapter mMvpHomeItemAdapter;

    private ILocationListener mLocationListener = new ILocationListener() {
        @Override
        public void onReceiveLocation(LocationInfo locationInfo) {
            loadShopPaginationListData(true);
        }

        @Override
        public void onReceiveFail() {

        }
    };

    public MvpShopPaginationListPresenter() {
        mShopModel = new MvpShopModel();
    }

    @Override
    public void onUiState(UiState state) {
        super.onUiState(state);
        switch (state) {
            case UI_STATE_ON_INIT:
                break;
            case UI_STATE_ON_RESUME:
                MapSdkManager.registerLocationListener(mLocationListener, LocationActionType.ONCE);
                break;
            case UI_STATE_ON_PAUSE:
                break;
            case UI_STATE_ON_STOP:
                MapSdkManager.unregisterLocationListener(mLocationListener);
                break;
            case UI_STATE_ON_DETACH:
                break;
        }
    }

    @Override
    public MvpShopListPaginationAdapter getListAdapter() {
        if (mMvpHomeItemAdapter == null) {
            mMvpHomeItemAdapter = new MvpShopListPaginationAdapter();
            mMvpHomeItemAdapter.enableInitLoading(true);
        }
        return mMvpHomeItemAdapter;
    }

    @Override
    public void loadShopPaginationListData(final boolean refresh) {
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
        mShopModel.requestShopListData(params, new IAsyncResponse<ArrayList<MvpShopItemEntity>>() {
            @Override
            public void onResponse(ArrayList<MvpShopItemEntity> list) {
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
