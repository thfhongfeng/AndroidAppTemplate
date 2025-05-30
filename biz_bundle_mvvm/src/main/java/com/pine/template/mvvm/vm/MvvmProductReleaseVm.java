package com.pine.template.mvvm.vm;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.pine.template.mvvm.R;
import com.pine.template.mvvm.bean.MvvmProductDetailEntity;
import com.pine.template.mvvm.model.MvvmShopModel;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.response.IAsyncResponse;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public class MvvmProductReleaseVm extends ViewModel {
    private MvvmShopModel mShopModel = new MvvmShopModel();
    private String mShopId;

    @Override
    public boolean parseIntentData(Context activity, Bundle bundle) {
        mShopId = bundle.getString("id", "");
        if (TextUtils.isEmpty(mShopId)) {
            finishUi();
            return true;
        }
        return false;
    }

    @Override
    public void afterViewInit(Context activity) {
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
        mShopModel.requestAddProduct(entity.toMapJsonIgnoreEmpty(), new IAsyncResponse<MvvmProductDetailEntity>() {
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
