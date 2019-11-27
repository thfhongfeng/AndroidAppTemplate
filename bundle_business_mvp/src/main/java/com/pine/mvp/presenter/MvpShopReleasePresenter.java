package com.pine.mvp.presenter;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.pine.mvp.R;
import com.pine.mvp.bean.MvpShopDetailEntity;
import com.pine.mvp.contract.IMvpShopReleaseContract;
import com.pine.mvp.model.MvpShopModel;
import com.pine.tool.architecture.mvp.model.IModelAsyncResponse;
import com.pine.tool.architecture.mvp.presenter.Presenter;
import com.pine.tool.bean.InputParam;
import com.pine.tool.exception.MessageException;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class MvpShopReleasePresenter extends Presenter<IMvpShopReleaseContract.Ui>
        implements IMvpShopReleaseContract.Presenter {
    private MvpShopModel mShopModel;

    public MvpShopReleasePresenter() {
        mShopModel = new MvpShopModel();
    }

    @NonNull
    @Override
    public String[] getShopTypeNameArr() {
        return getContext().getResources().getStringArray(R.array.mvp_shop_type_name);
    }

    @NonNull
    @Override
    public String[] getShopTypeValueArr() {
        return getContext().getResources().getStringArray(R.array.mvp_shop_type_value);
    }

    @NonNull
    @Override
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

    @Override
    public void addShop() {
        if (mIsLoadProcessing) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();

        InputParam<String> name = getUi().getShopNameParam("name");
        if (name.checkIsEmpty(R.string.mvp_shop_release_name_need)) {
            return;
        } else {
            params.put(name.getKey(), name.getValue());
        }

        InputParam<String> type = getUi().getShopTypeParam("type");
        if (type.checkIsEmpty(R.string.mvp_shop_release_type_need)) {
            return;
        } else {
            params.put(type.getKey(), type.getValue());
        }

        InputParam<String> typeName = getUi().getShopTypeNameParam("typeName");
        if (typeName.checkIsEmpty(R.string.mvp_shop_release_type_need)) {
            return;
        } else {
            params.put(typeName.getKey(), typeName.getValue());
        }

        InputParam<String> onlineDate = getUi().getShopOnlineDateParam("onlineDate");
        if (onlineDate.checkIsEmpty(R.string.mvp_shop_release_online_date_need)) {
            return;
        } else {
            params.put(onlineDate.getKey(), onlineDate.getValue());
        }

        InputParam<String> mobile = getUi().getShopContactMobileParam("mobile");
        if (mobile.checkIsEmpty(R.string.mvp_shop_release_contact_need) ||
                !mobile.checkIsPhone(R.string.mvp_shop_release_mobile_incorrect_format)) {
            return;
        } else {
            params.put(mobile.getKey(), mobile.getValue());
        }

        InputParam<String> address = getUi().getShopAddressParam("addressDistrict");
        if (address.checkIsEmpty(R.string.mvp_shop_release_address_need)) {
            return;
        } else {
            params.put(address.getKey(), address.getValue());
        }

        InputParam<String> addressZipCode = getUi().getShopAddressZipCodeParam("addressZipCode");
        if (addressZipCode.checkIsEmpty(R.string.mvp_shop_release_address_need)) {
            return;
        } else {
            params.put(addressZipCode.getKey(), addressZipCode.getValue());
        }

        InputParam<String> latitude = getUi().getShopLocationLatParam("latitude");
        if (latitude.checkIsEmpty(R.string.mvp_shop_release_address_location_need)) {
            return;
        } else {
            params.put(latitude.getKey(), latitude.getValue());
        }

        InputParam<String> longitude = getUi().getShopLocationLonParam("longitude");
        if (longitude.checkIsEmpty(R.string.mvp_shop_release_address_location_need)) {
            return;
        } else {
            params.put(longitude.getKey(), longitude.getValue());
        }

        params.put("addressStreet", getUi().getShopDetailAddressParam("addressStreet").getValue().toString());
        params.put("description", getUi().getShopDescriptionParam("description").getValue().toString());
        params.put("remark", getUi().getShopRemarkParam("remark").getValue().toString());

        InputParam<String> images = getUi().getShopImagesParam("imgUrls");
        if (images.checkIsEmpty(R.string.mvp_shop_release_photo_image_need)) {
            return;
        } else {
            int pos = images.getValue().indexOf(",");
            if (pos == -1) {
                params.put("mainImgUrl", images.getValue());
            } else {
                params.put("mainImgUrl", images.getValue().substring(0, pos));
            }
            params.put(images.getKey(), images.getValue());
        }

        setUiLoading(true);
        mShopModel.requestAddShop(params, new IModelAsyncResponse<MvpShopDetailEntity>() {
            @Override
            public void onResponse(MvpShopDetailEntity entity) {
                setUiLoading(false);
                showShortToast(R.string.mvp_shop_release_success);
                finishUi();
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
                if (e instanceof MessageException) {
                    if (!TextUtils.isEmpty(e.getMessage())) {
                        showShortToast(e.getMessage());
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
}
