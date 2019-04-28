package com.pine.mvvm.model;

import android.support.annotation.NonNull;

import com.pine.base.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.mvvm.bean.MvvmShopAndProductEntity;
import com.pine.mvvm.bean.MvvmShopDetailEntity;
import com.pine.mvvm.bean.MvvmShopItemEntity;

import java.util.ArrayList;
import java.util.Map;

public interface IMvvmShopModel {
    void requestAddShop(final Map<String, String> params,
                        @NonNull final IModelAsyncResponse<MvvmShopDetailEntity> callback);

    void requestShopDetailData(final Map<String, String> params,
                               @NonNull final IModelAsyncResponse<MvvmShopDetailEntity> callback);

    void requestShopListData(final Map<String, String> params,
                             @NonNull final IModelAsyncResponse<ArrayList<MvvmShopItemEntity>> callback);

    void requestShopAndProductListData(Map<String, String> params,
                                       @NonNull final IModelAsyncResponse<ArrayList<MvvmShopAndProductEntity>> callback);
}
