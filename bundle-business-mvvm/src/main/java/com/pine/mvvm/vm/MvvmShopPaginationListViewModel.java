package com.pine.mvvm.vm;

import android.os.Bundle;

import com.pine.base.architecture.mvvm.data.BaseMvvmLiveData;
import com.pine.base.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.base.architecture.mvvm.vm.BaseViewModel;
import com.pine.base.component.map.LocationInfo;
import com.pine.base.component.map.MapSdkManager;
import com.pine.mvvm.MvvmConstants;
import com.pine.mvvm.bean.MvvmShopItemEntity;
import com.pine.mvvm.model.MvvmShopModel;

import java.util.ArrayList;
import java.util.HashMap;

public class MvvmShopPaginationListViewModel extends BaseViewModel {
    private MvvmShopModel mShopModel = new MvvmShopModel();

    BaseMvvmLiveData<ArrayList<MvvmShopItemEntity>, Boolean> shopListData = new BaseMvvmLiveData<>();

    public BaseMvvmLiveData<ArrayList<MvvmShopItemEntity>, Boolean> getShopListData() {
        return shopListData;
    }

    public void setShopListData(ArrayList<MvvmShopItemEntity> shopList, boolean refresh) {
        shopListData.setValue(shopList, refresh);
    }

    @Override
    public boolean parseInitData(Bundle bundle) {
        return false;
    }

    public void loadShopPaginationListData(final boolean refresh, int pageNo, int pageSize) {
        if (getUiLoadingData().getValue()) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(MvvmConstants.PAGE_NO, String.valueOf(pageNo));
        params.put(MvvmConstants.PAGE_SIZE, String.valueOf(pageSize));
        LocationInfo location = MapSdkManager.getInstance().getLocation();
        if (location != null) {
            params.put("latitude", String.valueOf(location.getLatitude()));
            params.put("longitude", String.valueOf(location.getLongitude()));
        }
        setUiLoadingData(true);
        mShopModel.requestShopListData(params, new IModelAsyncResponse<ArrayList<MvvmShopItemEntity>>() {
            @Override
            public void onResponse(ArrayList<MvvmShopItemEntity> list) {
                setUiLoadingData(false);
                setShopListData(list, refresh);
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoadingData(false);
                return false;
            }

            @Override
            public void onCancel() {
                setUiLoadingData(false);
            }
        });
    }
}