package com.pine.mvvm.vm;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.text.TextUtils;

import com.pine.base.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.base.architecture.mvvm.vm.BaseViewModel;
import com.pine.mvvm.bean.MvvmShopDetailEntity;
import com.pine.mvvm.model.IMvvmShopModel;
import com.pine.mvvm.model.MvvmModelFactory;
import com.pine.mvvm.model.net.MvvmShopModel;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public class MvvmShopDetailVm extends BaseViewModel {
    private String mId;
    private IMvvmShopModel mShopModel = MvvmModelFactory.getMvvmShopModel();

    @Override
    public boolean parseIntentData(Bundle bundle) {
        mId = bundle.getString("id", "");
        if (TextUtils.isEmpty(mId)) {
            finishUi();
            return true;
        }
        return false;
    }

    public void loadShopDetailData() {
        if (isUiLoading()) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("id", mId);
        setUiLoading(true);
        mShopModel.requestShopDetailData(params, new IModelAsyncResponse<MvvmShopDetailEntity>() {
            @Override
            public void onResponse(MvvmShopDetailEntity entity) {
                setUiLoading(false);
                setShopDetail(entity);
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

    private MutableLiveData<MvvmShopDetailEntity> shopDetailData = new MutableLiveData<>();

    public MutableLiveData<MvvmShopDetailEntity> getShopDetailData() {
        return shopDetailData;
    }

    public void setShopDetail(MvvmShopDetailEntity shopDetail) {
        shopDetailData.setValue(shopDetail);
    }
}
