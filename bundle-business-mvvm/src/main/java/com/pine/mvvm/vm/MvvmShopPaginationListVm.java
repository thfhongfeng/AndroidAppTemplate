package com.pine.mvvm.vm;

import com.pine.base.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.base.architecture.mvvm.vm.BaseViewModel;
import com.pine.base.binding.data.BaseLiveData;
import com.pine.base.component.map.LocationInfo;
import com.pine.base.component.map.MapSdkManager;
import com.pine.mvvm.MvvmConstants;
import com.pine.mvvm.bean.MvvmShopItemEntity;
import com.pine.mvvm.model.MvvmShopModel;

import java.util.ArrayList;
import java.util.HashMap;

public class MvvmShopPaginationListVm extends BaseViewModel {
    private MvvmShopModel mShopModel = new MvvmShopModel();

    public void loadShopPaginationListData(final boolean refresh, int pageNo, int pageSize) {
        if (isUiLoading()) {
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
        setUiLoading(true);
        mShopModel.requestShopListData(params, new IModelAsyncResponse<ArrayList<MvvmShopItemEntity>>() {
            @Override
            public void onResponse(ArrayList<MvvmShopItemEntity> list) {
                setUiLoading(false);
                setShopList(list, refresh);
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

    BaseLiveData<ArrayList<MvvmShopItemEntity>, Boolean> shopListData = new BaseLiveData<>();

    public BaseLiveData<ArrayList<MvvmShopItemEntity>, Boolean> getShopListData() {
        return shopListData;
    }

    public void setShopList(ArrayList<MvvmShopItemEntity> shopList, boolean refresh) {
        shopListData.setValue(shopList, refresh);
    }

}
