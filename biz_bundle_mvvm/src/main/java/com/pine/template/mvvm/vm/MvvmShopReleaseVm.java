package com.pine.template.mvvm.vm;

import android.content.Context;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.pine.template.mvvm.R;
import com.pine.template.mvvm.bean.MvvmShopDetailEntity;
import com.pine.template.mvvm.model.MvvmShopModel;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.response.IAsyncResponse;
import com.pine.tool.util.AppUtils;
import com.pine.tool.util.LogUtils;
import com.pine.tool.util.RegexUtils;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public class MvvmShopReleaseVm extends ViewModel {
    private MvvmShopModel mShopModel = new MvvmShopModel();

    @Override
    public void afterViewInit(Context activity) {
        setShopTypeNameArr(AppUtils.getApplicationContext().getResources().getStringArray(R.array.mvvm_shop_type_name));
        setShopTypeValueArr(AppUtils.getApplicationContext().getResources().getStringArray(R.array.mvvm_shop_type_value));
        setShopDetail(new MvvmShopDetailEntity());
    }

    public HashMap<String, String> makeUploadDefaultParams() {
        HashMap<String, String> params = new HashMap<>();
        // Test code begin
        params.put("bizType", "10");
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
        LogUtils.d(TAG, "addShop " + entity);
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
        if (!TextUtils.isEmpty(entity.getImgUrls())) {
            int pos = entity.getImgUrls().indexOf(",");
            if (pos == -1) {
                entity.setMainImgUrl(entity.getImgUrls());
            } else {
                entity.setMainImgUrl(entity.getImgUrls().substring(0, pos));
            }
        }
        setUiLoading(true);
        mShopModel.requestAddShop(entity.toMapJsonIgnoreEmpty(), new IAsyncResponse<MvvmShopDetailEntity>() {
            @Override
            public void onResponse(MvvmShopDetailEntity entity) {
                setUiLoading(false);
                resetUi();
                setToastResId(R.string.mvvm_shop_release_success);
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

    private MutableLiveData<String[]> shopTypeNameArrData = new MutableLiveData<>();

    public MutableLiveData<String[]> getShopTypeNameArrData() {
        return shopTypeNameArrData;
    }

    public void setShopTypeNameArr(String[] shopTypeNameArr) {
        shopTypeNameArrData.setValue(shopTypeNameArr);
    }

    private MutableLiveData<String[]> shopTypeValueArrData = new MutableLiveData<>();

    public MutableLiveData<String[]> getShopTypeValueArrData() {
        return shopTypeValueArrData;
    }

    public void setShopTypeValueArr(String[] shopTypeArr) {
        shopTypeValueArrData.setValue(shopTypeArr);
    }

    private MutableLiveData<MvvmShopDetailEntity> shopDetailData = new MutableLiveData<>();

    public MutableLiveData<MvvmShopDetailEntity> getShopDetailData() {
        return shopDetailData;
    }

    public void setShopDetail(MvvmShopDetailEntity shopDetail) {
        shopDetailData.setValue(shopDetail);
    }
}
