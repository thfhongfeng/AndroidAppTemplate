package com.pine.mvvm.vm;

import com.pine.base.architecture.mvp.model.IModelAsyncResponse;
import com.pine.base.architecture.mvvm.data.BaseMvvmLiveData;
import com.pine.base.architecture.mvvm.vm.BaseViewModel;
import com.pine.base.component.map.LocationInfo;
import com.pine.base.component.map.MapSdkManager;
import com.pine.mvvm.MvvmConstants;
import com.pine.mvvm.bean.MvvmShopItemEntity;
import com.pine.mvvm.model.MvvmShopModel;

import org.json.JSONException;

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

    public void loadShopPaginationListData(final boolean refresh, int pageNo, int pageSize) {
        if (getUiLoading().getValue()) {
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
        if (!mShopModel.requestShopListData(params, new IModelAsyncResponse<ArrayList<MvvmShopItemEntity>>() {
            @Override
            public void onResponse(ArrayList<MvvmShopItemEntity> list) {
                setUiLoading(false);
                setShopListData(list, refresh);
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
                if (e instanceof JSONException) {
//                    if (isUiAlive()) {
//                        Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
//                    }
                }
                return false;
            }
        })) {
            setUiLoading(false);
        }
    }
}
