package com.pine.template.mvvm.vm;

import android.text.TextUtils;

import com.pine.template.base.component.map.LocationInfo;
import com.pine.template.base.component.map.MapSdkManager;
import com.pine.template.mvvm.MvvmKeyConstants;
import com.pine.template.mvvm.bean.MvvmShopAndProductEntity;
import com.pine.template.mvvm.model.MvvmShopModel;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.binding.data.ParametricLiveData;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.response.IAsyncResponse;

import java.util.ArrayList;
import java.util.HashMap;

public class MvvmShopTreeListVm extends ViewModel {
    private MvvmShopModel mShopModel = new MvvmShopModel();

    public void loadShopTreeListData(final boolean refresh, int pageNo, int pageSize) {
        if (isUiLoading()) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(MvvmKeyConstants.PAGE_NO, String.valueOf(pageNo));
        params.put(MvvmKeyConstants.PAGE_SIZE, String.valueOf(pageSize));
        LocationInfo location = MapSdkManager.getLocation();
        if (location != null) {
            params.put("latitude", String.valueOf(location.getLatitude()));
            params.put("longitude", String.valueOf(location.getLongitude()));
        }
        setUiLoading(true);
        mShopModel.requestShopAndProductListData(params, new IAsyncResponse<ArrayList<MvvmShopAndProductEntity>>() {
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
