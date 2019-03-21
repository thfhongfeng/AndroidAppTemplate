package com.pine.mvvm.model.local;

import android.support.annotation.NonNull;

import com.pine.base.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.mvvm.bean.MvvmShopAndProductEntity;
import com.pine.mvvm.bean.MvvmShopDetailEntity;
import com.pine.mvvm.bean.MvvmShopItemEntity;
import com.pine.mvvm.model.IMvvmShopModel;

import java.util.ArrayList;
import java.util.Map;

public class MvvmShopLocalModel implements IMvvmShopModel {
    @Override
    public void requestAddShop(Map<String, String> params, @NonNull IModelAsyncResponse<MvvmShopDetailEntity> callback) {

    }

    @Override
    public void requestShopDetailData(Map<String, String> params, @NonNull IModelAsyncResponse<MvvmShopDetailEntity> callback) {

    }

    @Override
    public void requestShopListData(Map<String, String> params, @NonNull IModelAsyncResponse<ArrayList<MvvmShopItemEntity>> callback) {

    }

    @Override
    public void requestShopAndProductListData(Map<String, String> params, @NonNull IModelAsyncResponse<ArrayList<MvvmShopAndProductEntity>> callback) {

    }
}
