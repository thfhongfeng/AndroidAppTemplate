package com.pine.mvvm.vm;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.text.TextUtils;

import com.pine.base.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.base.architecture.mvvm.vm.BaseViewModel;
import com.pine.mvvm.bean.MvvmShopDetailEntity;
import com.pine.mvvm.model.MvvmShopModel;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public class MvvmShopDetailViewModel extends BaseViewModel {
    private String mId;
    private MvvmShopModel mModel = new MvvmShopModel();

    private MutableLiveData<MvvmShopDetailEntity> shopDetailData = new MutableLiveData<>();

    public MutableLiveData<MvvmShopDetailEntity> getShopDetailData() {
        return shopDetailData;
    }

    public void setShopDetailData(MvvmShopDetailEntity shopDetail) {
        shopDetailData.setValue(shopDetail);
    }

    @Override
    public boolean parseInitData(Bundle bundle) {
        mId = bundle.getString("id", "");
        if (TextUtils.isEmpty(mId)) {
            setFinishData(true);
            return true;
        }
        return false;
    }

    public void loadShopDetailData() {
        if (getUiLoadingData().getValue()) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("id", mId);
        setUiLoadingData(true);
        mModel.requestShopDetailData(params, new IModelAsyncResponse<MvvmShopDetailEntity>() {
            @Override
            public void onResponse(MvvmShopDetailEntity entity) {
                setUiLoadingData(false);
                setShopDetailData(entity);
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