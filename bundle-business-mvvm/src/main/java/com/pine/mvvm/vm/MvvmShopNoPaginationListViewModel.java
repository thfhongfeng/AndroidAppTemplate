package com.pine.mvvm.vm;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;

import com.pine.base.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.base.architecture.mvvm.vm.BaseViewModel;
import com.pine.base.component.map.LocationInfo;
import com.pine.base.component.map.MapSdkManager;
import com.pine.mvvm.bean.MvvmShopItemEntity;
import com.pine.mvvm.model.MvvmShopModel;

import java.util.ArrayList;
import java.util.HashMap;

public class MvvmShopNoPaginationListViewModel extends BaseViewModel {
    private MvvmShopModel mShopModel = new MvvmShopModel();

    MutableLiveData<ArrayList<MvvmShopItemEntity>> shopListData = new MutableLiveData<>();

    public MutableLiveData<ArrayList<MvvmShopItemEntity>> getShopListData() {
        return shopListData;
    }

    public void setShopListData(ArrayList<MvvmShopItemEntity> shopList) {
        shopListData.setValue(shopList);
    }

    @Override
    public boolean parseInitData(Bundle bundle) {
        return false;
    }

    public void loadShopNoPaginationListData() {
        if (getUiLoadingData().getValue()) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
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
                setShopListData(list);
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
