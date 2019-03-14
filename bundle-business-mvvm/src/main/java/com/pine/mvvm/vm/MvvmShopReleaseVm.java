package com.pine.mvvm.vm;

import android.arch.lifecycle.MutableLiveData;
import android.text.TextUtils;

import com.pine.base.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.base.architecture.mvvm.vm.BaseViewModel;
import com.pine.mvvm.R;
import com.pine.mvvm.bean.MvvmShopDetailEntity;
import com.pine.mvvm.model.MvvmShopModel;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.RegexUtils;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public class MvvmShopReleaseVm extends BaseViewModel {
    private MvvmShopModel mModel = new MvvmShopModel();

    @Override
    public void afterViewInit() {
        setShopTypeArrData(AppUtils.getApplicationContext().getResources().getStringArray(R.array.mvvm_shop_type));
        setShopTypeNameArrData(AppUtils.getApplicationContext().getResources().getStringArray(R.array.mvvm_shop_name_type));
        setShopDetailData(new MvvmShopDetailEntity());
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
        if (getUiLoadingData().getValue()) {
            return;
        }
        MvvmShopDetailEntity entity = shopDetailData.getValue();
        if (TextUtils.isEmpty(entity.getName())) {
            setToastData(R.string.mvvm_shop_release_name_need);
            return;
        }
        if (TextUtils.isEmpty(entity.getType())) {
            setToastData(R.string.mvvm_shop_release_type_need);
            return;
        }
        if (TextUtils.isEmpty(entity.getTypeName())) {
            setToastData(R.string.mvvm_shop_release_type_need);
            return;
        }
        if (TextUtils.isEmpty(entity.getOnlineDate())) {
            setToastData(R.string.mvvm_shop_release_online_date_need);
            return;
        }
        if (TextUtils.isEmpty(entity.getMobile())) {
            setToastData(R.string.mvvm_shop_release_contact_need);
            return;
        }
        if (!RegexUtils.isMobilePhoneNumber(entity.getMobile())) {
            setToastData(R.string.mvvm_shop_release_mobile_incorrect_format);
            return;
        }
        if (TextUtils.isEmpty(entity.getAddressDistrict()) || TextUtils.isEmpty(entity.getAddressZipCode())) {
            setToastData(R.string.mvvm_shop_release_address_need);
            return;
        }
        if (TextUtils.isEmpty(entity.getLatitude()) || TextUtils.isEmpty(entity.getLongitude())) {
            setToastData(R.string.mvvm_shop_release_address_location_need);
            return;
        }
        if (TextUtils.isEmpty(entity.getImgUrls())) {
            setToastData(R.string.mvvm_shop_release_photo_image_need);
            return;
        }
        setUiLoadingData(true);
        mModel.requestAddShop(entity.toMapIgnoreEmpty(), new IModelAsyncResponse<MvvmShopDetailEntity>() {
            @Override
            public void onResponse(MvvmShopDetailEntity entity) {
                setUiLoadingData(false);
                resetUi();
                setToastData(R.string.mvvm_shop_release_success);
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

    private MutableLiveData<String[]> shopTypeArrData = new MutableLiveData<>();

    public MutableLiveData<String[]> getShopTypeArrData() {
        return shopTypeArrData;
    }

    public void setShopTypeArrData(String[] shopTypeArr) {
        shopTypeArrData.setValue(shopTypeArr);
    }

    private MutableLiveData<String[]> shopTypeNameArrData = new MutableLiveData<>();

    public MutableLiveData<String[]> getShopTypeNameArrData() {
        return shopTypeNameArrData;
    }

    public void setShopTypeNameArrData(String[] shopTypeNameArr) {
        shopTypeNameArrData.setValue(shopTypeNameArr);
    }

    private MutableLiveData<MvvmShopDetailEntity> shopDetailData = new MutableLiveData<>();

    public MutableLiveData<MvvmShopDetailEntity> getShopDetailData() {
        return shopDetailData;
    }

    public void setShopDetailData(MvvmShopDetailEntity shopDetail) {
        shopDetailData.setValue(shopDetail);
    }
}
