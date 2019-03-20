package com.pine.mvvm.vm;

import android.arch.lifecycle.MutableLiveData;

import com.pine.base.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.base.architecture.mvvm.vm.BaseViewModel;
import com.pine.base.component.map.LocationInfo;
import com.pine.base.component.map.MapSdkManager;
import com.pine.mvvm.bean.MvvmShopItemEntity;
import com.pine.mvvm.model.IMvvmShopModel;
import com.pine.mvvm.model.MvvmModelFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class MvvmShopNoPaginationListVm extends BaseViewModel {
    private IMvvmShopModel mShopModel = MvvmModelFactory.getMvvmShopModel();

    public void loadShopNoPaginationListData() {
        if (isUiLoading()) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        LocationInfo location = MapSdkManager.getInstance().getLocation();
        if (location != null) {
            params.put("latitude", String.valueOf(location.getLatitude()));
            params.put("longitude", String.valueOf(location.getLongitude()));
        }
        setUiLoading(true);
        mShopModel.requestShopListData(params, new IModelAsyncResponse<ArrayList<MvvmShopItemEntity>>() {
            @Override
            public void onResponse(ArrayList<MvvmShopItemEntity> list) {
                setUiLoading(false);
                setShopList(list);
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
                return false;
            }

            @Override
            public void onCancel() {
                setUiLoading(false);
            }
        });
    }

    MutableLiveData<ArrayList<MvvmShopItemEntity>> shopListData = new MutableLiveData<>();

    public MutableLiveData<ArrayList<MvvmShopItemEntity>> getShopListData() {
        return shopListData;
    }

    public void setShopList(ArrayList<MvvmShopItemEntity> shopList) {
        shopListData.setValue(shopList);
    }
}
