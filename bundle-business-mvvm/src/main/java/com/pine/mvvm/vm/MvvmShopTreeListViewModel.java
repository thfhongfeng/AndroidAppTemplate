package com.pine.mvvm.vm;

import android.os.Bundle;

import com.pine.base.architecture.mvvm.data.BaseMvvmLiveData;
import com.pine.base.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.base.architecture.mvvm.vm.BaseViewModel;
import com.pine.base.component.map.LocationInfo;
import com.pine.base.component.map.MapSdkManager;
import com.pine.mvvm.MvvmConstants;
import com.pine.mvvm.bean.MvvmShopAndProductEntity;
import com.pine.mvvm.model.MvvmShopModel;

import java.util.ArrayList;
import java.util.HashMap;

public class MvvmShopTreeListViewModel extends BaseViewModel {
    private MvvmShopModel mShopModel = new MvvmShopModel();

    BaseMvvmLiveData<ArrayList<MvvmShopAndProductEntity>, Boolean> shopListData = new BaseMvvmLiveData<>();

    public BaseMvvmLiveData<ArrayList<MvvmShopAndProductEntity>, Boolean> getShopListData() {
        return shopListData;
    }

    public void setShopListData(ArrayList<MvvmShopAndProductEntity> shopList, boolean refresh) {
        shopListData.setValue(shopList, refresh);
    }

    @Override
    public boolean parseInitData(Bundle bundle) {
        return false;
    }

    public void loadShopTreeListData(final boolean refresh, int pageNo, int pageSize) {
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
        mShopModel.requestShopAndProductListData(params, new IModelAsyncResponse<ArrayList<MvvmShopAndProductEntity>>() {
            @Override
            public void onResponse(ArrayList<MvvmShopAndProductEntity> list) {
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