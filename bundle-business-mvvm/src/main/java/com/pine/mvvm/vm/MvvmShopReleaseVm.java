package com.pine.mvvm.vm;

import android.arch.lifecycle.MutableLiveData;
import android.text.TextUtils;

import com.pine.base.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.base.architecture.mvvm.vm.BaseViewModel;
import com.pine.mvvm.R;
import com.pine.mvvm.bean.MvvmShopDetailEntity;
import com.pine.mvvm.model.IMvvmShopModel;
import com.pine.mvvm.model.MvvmModelFactory;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.RegexUtils;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public class MvvmShopReleaseVm extends BaseViewModel {
    private IMvvmShopModel mShopModel = MvvmModelFactory.getMvvmShopModel();

    @Override
    public void afterViewInit() {
        setShopTypeArr(AppUtils.getApplicationContext().getResources().getStringArray(R.array.mvvm_shop_type));
        setShopTypeNameArr(AppUtils.getApplicationContext().getResources().getStringArray(R.array.mvvm_shop_name_type));
        setShopDetail(new MvvmShopDetailEntity());
    }

    public HashMap<String, String> makeUploadDefaultParams() {
        HashMap<String, String> params = new HashMap<>();
        // Test code begin
        params.put("bizType", "10");
        params.put("orderNum", "100");
        params.put("orderNum", "100");
        params.put("descr", "");
        params.put("fileType", "1");
        // Test code end
        return params;
    }

    public void addShop() {
        if (isUiLoading()) {
            return;
        }
        MvvmShopDetailEntity entity = shopDetailData.getValue();
        if (TextUtils.isEmpty(entity.getName())) {
            setToastResId(R.string.mvvm_shop_release_name_need);
            return;
        }
        if (TextUtils.isEmpty(entity.getType())) {
            setToastResId(R.string.mvvm_shop_release_type_need);
            return;
        }
        if (TextUtils.isEmpty(entity.getTypeName())) {
            setToastResId(R.string.mvvm_shop_release_type_need);
            return;
        }
        if (TextUtils.isEmpty(entity.getOnlineDate())) {
            setToastResId(R.string.mvvm_shop_release_online_date_need);
            return;
        }
        if (TextUtils.isEmpty(entity.getMobile())) {
            setToastResId(R.string.mvvm_shop_release_contact_need);
            return;
        }
        if (!RegexUtils.isMobilePhoneNumber(entity.getMobile())) {
            setToastResId(R.string.mvvm_shop_release_mobile_incorrect_format);
            return;
        }
        if (TextUtils.isEmpty(entity.getAddressDistrict()) || TextUtils.isEmpty(entity.getAddressZipCode())) {
            setToastResId(R.string.mvvm_shop_release_address_need);
            return;
        }
        if (TextUtils.isEmpty(entity.getLatitude()) || TextUtils.isEmpty(entity.getLongitude())) {
            setToastResId(R.string.mvvm_shop_release_address_location_need);
            return;
        }
        if (TextUtils.isEmpty(entity.getImgUrls())) {
            setToastResId(R.string.mvvm_shop_release_photo_image_need);
            return;
        }
        setUiLoading(true);
        mShopModel.requestAddShop(entity.toMapJsonIgnoreEmpty(), new IModelAsyncResponse<MvvmShopDetailEntity>() {
            @Override
            public void onResponse(MvvmShopDetailEntity entity) {
                setUiLoading(false);
                resetUi();
                setToastResId(R.string.mvvm_shop_release_success);
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

    private MutableLiveData<String[]> shopTypeArrData = new MutableLiveData<>();

    public MutableLiveData<String[]> getShopTypeArrData() {
        return shopTypeArrData;
    }

    public void setShopTypeArr(String[] shopTypeArr) {
        shopTypeArrData.setValue(shopTypeArr);
    }

    private MutableLiveData<String[]> shopTypeNameArrData = new MutableLiveData<>();

    public MutableLiveData<String[]> getShopTypeNameArrData() {
        return shopTypeNameArrData;
    }

    public void setShopTypeNameArr(String[] shopTypeNameArr) {
        shopTypeNameArrData.setValue(shopTypeNameArr);
    }

    private MutableLiveData<MvvmShopDetailEntity> shopDetailData = new MutableLiveData<>();

    public MutableLiveData<MvvmShopDetailEntity> getShopDetailData() {
        return shopDetailData;
    }

    public void setShopDetail(MvvmShopDetailEntity shopDetail) {
        shopDetailData.setValue(shopDetail);
    }
}