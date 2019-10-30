package com.pine.mvvm.vm;

import android.text.TextUtils;

import com.pine.base.component.map.LocationInfo;
import com.pine.base.component.map.MapSdkManager;
import com.pine.mvvm.MvvmConstants;
import com.pine.mvvm.bean.MvvmShopAndProductEntity;
import com.pine.mvvm.model.MvvmShopModel;
import com.pine.tool.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.binding.data.ParametricLiveData;
import com.pine.tool.exception.MessageException;

import java.util.ArrayList;
import java.util.HashMap;

public class MvvmShopTreeListVm extends ViewModel {
    private MvvmShopModel mShopModel = new MvvmShopModel();

    public void loadShopTreeListData(final boolean refresh, int pageNo, int pageSize) {
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
        mShopModel.requestShopAndProductListData(params, new IModelAsyncResponse<ArrayList<MvvmShopAndProductEntity>>() {
            @Override
            public void onResponse(ArrayList<MvvmShopAndProductEntity> list) {
                setUiLoading(false);
                setShopList(list, refresh);
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
                if (e instanceof MessageException) {
                    if (!TextUtils.isEmpty(e.getMessage())) {
                        setToastMsg(e.getMessage());
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

    ParametricLiveData<ArrayList<MvvmShopAndProductEntity>, Boolean> shopListData = new ParametricLiveData<>();

    public ParametricLiveData<ArrayList<MvvmShopAndProductEntity>, Boolean> getShopListData() {
        return shopListData;
    }

    public void setShopList(ArrayList<MvvmShopAndProductEntity> shopList, boolean refresh) {
        shopListData.setValue(shopList, refresh);
    }
}
