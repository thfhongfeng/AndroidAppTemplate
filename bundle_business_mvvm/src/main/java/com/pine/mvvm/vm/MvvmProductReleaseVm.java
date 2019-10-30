package com.pine.mvvm.vm;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.text.TextUtils;

import com.pine.mvvm.R;
import com.pine.mvvm.bean.MvvmProductDetailEntity;
import com.pine.mvvm.model.MvvmShopModel;
import com.pine.tool.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.exception.MessageException;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public class MvvmProductReleaseVm extends ViewModel {
    private MvvmShopModel mShopModel = new MvvmShopModel();
    private String mShopId;

    @Override
    public boolean parseIntentData(Bundle bundle) {
        mShopId = bundle.getString("id", "");
        if (TextUtils.isEmpty(mShopId)) {
            finishUi();
            return true;
        }
        return false;
    }

    @Override
    public void afterViewInit() {
        setProductDetail(new MvvmProductDetailEntity());
    }

    public void addProduct() {
        if (isUiLoading()) {
            return;
        }
        MvvmProductDetailEntity entity = productDetailData.getValue();
        if (TextUtils.isEmpty(entity.getName())) {
            setToastResId(R.string.mvvm_product_release_name_need);
            return;
        }
        if (TextUtils.isEmpty(entity.getPrice())) {
            setToastResId(R.string.mvvm_product_release_price_need);
            return;
        }
        if (TextUtils.isEmpty(entity.getShelvePrice())) {
            setToastResId(R.string.mvvm_product_release_shelve_price_need);
            return;
        }
        if (TextUtils.isEmpty(entity.getShelveDate())) {
            setToastResId(R.string.mvvm_product_release_shelve_date_need);
            return;
        }
        entity.setShopId(mShopId);
        setUiLoading(true);
        mShopModel.requestAddProduct(entity.toMapJsonIgnoreEmpty(), new IModelAsyncResponse<MvvmProductDetailEntity>() {
            @Override
            public void onResponse(MvvmProductDetailEntity entity) {
                setUiLoading(false);
                resetUi();
                setToastResId(R.string.mvvm_product_release_success);
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

    private MutableLiveData<MvvmProductDetailEntity> productDetailData = new MutableLiveData<>();

    public MutableLiveData<MvvmProductDetailEntity> getProductDetailData() {
        return productDetailData;
    }

    public void setProductDetail(MvvmProductDetailEntity productDetail) {
        productDetailData.setValue(productDetail);
    }
}
