package com.pine.template.mvvm.vm;

import android.text.TextUtils;

import com.pine.template.base.component.map.LocationInfo;
import com.pine.template.base.component.map.MapSdkManager;
import com.pine.template.mvvm.MvvmConstants;
import com.pine.template.mvvm.bean.MvvmShopItemEntity;
import com.pine.template.mvvm.model.MvvmShopModel;
import com.pine.tool.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.binding.data.ParametricLiveData;
import com.pine.tool.exception.MessageException;

import java.util.ArrayList;
import java.util.HashMap;

public class MvvmShopPaginationListVm extends ViewModel {
    private MvvmShopModel mShopModel = new MvvmShopModel();

    public void loadShopPaginationListData(final boolean refresh, int pageNo, int pageSize) {
        if (isUiLoading()) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(MvvmConstants.PAGE_NO, String.valueOf(pageNo));
        params.put(MvvmConstants.PAGE_SIZE, String.valueOf(pageSize));
        LocationInfo location = MapSdkManager.getLocation();
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

    ParametricLiveData<ArrayList<MvvmShopItemEntity>, Boolean> shopListData = new ParametricLiveData<>();

    public ParametricLiveData<ArrayList<MvvmShopItemEntity>, Boolean> getShopListData() {
        return shopListData;
    }

    public void setShopList(ArrayList<MvvmShopItemEntity> shopList, boolean refresh) {
        shopListData.setValue(shopList, refresh);
    }

}
