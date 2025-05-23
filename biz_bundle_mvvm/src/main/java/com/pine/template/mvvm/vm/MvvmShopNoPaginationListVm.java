package com.pine.template.mvvm.vm;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.pine.template.base.component.map.LocationInfo;
import com.pine.template.base.component.map.MapSdkManager;
import com.pine.template.mvvm.bean.MvvmShopItemEntity;
import com.pine.template.mvvm.model.MvvmShopModel;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.response.IAsyncResponse;

import java.util.ArrayList;
import java.util.HashMap;

public class MvvmShopNoPaginationListVm extends ViewModel {
    private MvvmShopModel mShopModel = new MvvmShopModel();

    public void loadShopNoPaginationListData() {
        if (isUiLoading()) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        LocationInfo location = MapSdkManager.getLocation();
        if (location != null) {
            params.put("latitude", String.valueOf(location.getLatitude()));
            params.put("longitude", String.valueOf(location.getLongitude()));
        }
        setUiLoading(true);
        mShopModel.requestShopListData(params, new IAsyncResponse<ArrayList<MvvmShopItemEntity>>() {
            @Override
            public void onResponse(ArrayList<MvvmShopItemEntity> list) {
                setUiLoading(false);
                setShopList(list);
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

    MutableLiveData<ArrayList<MvvmShopItemEntity>> shopListData = new MutableLiveData<>();

    public MutableLiveData<ArrayList<MvvmShopItemEntity>> getShopListData() {
        return shopListData;
    }

    public void setShopList(ArrayList<MvvmShopItemEntity> shopList) {
        shopListData.setValue(shopList);
    }
}
