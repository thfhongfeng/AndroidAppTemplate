package com.pine.template.mvp.presenter;

import android.os.Bundle;
import android.text.TextUtils;

import com.pine.template.base.bean.InputParam;
import com.pine.template.mvp.R;
import com.pine.template.mvp.bean.MvpProductDetailEntity;
import com.pine.template.mvp.contract.IMvpProductReleaseContract;
import com.pine.template.mvp.model.MvpShopModel;
import com.pine.tool.architecture.mvp.presenter.Presenter;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.response.IAsyncResponse;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/13
 */

public class MvpProductReleasePresenter extends Presenter<IMvpProductReleaseContract.Ui>
        implements IMvpProductReleaseContract.Presenter {
    private MvpShopModel mShopModel;
    private String mShopId;

    public MvpProductReleasePresenter() {
        mShopModel = new MvpShopModel();
    }

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
    public void addProduct() {
        if (mIsLoadProcessing) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();

        InputParam<String> name = getUi().getProductNameParam("name");
        if (name.checkIsEmpty(R.string.mvp_product_release_name_need)) {
            return;
        } else {
            params.put(name.getKey(), name.getValue());
        }

        InputParam<String> price = getUi().getProductPriceParam("price");
        if (price.checkIsEmpty(R.string.mvp_product_release_price_need)) {
            return;
        } else {
            params.put(price.getKey(), price.getValue());
        }

        InputParam<String> shelvePrice = getUi().getProductShelvePriceParam("shelvePrice");
        if (shelvePrice.checkIsEmpty(R.string.mvp_product_release_shelve_price_need)) {
            return;
        } else {
            params.put(shelvePrice.getKey(), shelvePrice.getValue());
        }

        InputParam<String> shelveDate = getUi().getProductShelveDateParam("shelveDate");
        if (shelveDate.checkIsEmpty(R.string.mvp_product_release_shelve_date_need)) {
            return;
        } else {
            params.put(shelveDate.getKey(), shelveDate.getValue());
        }
        params.put("description", getUi().getProductDescriptionParam("description").getValue().toString());
        params.put("remark", getUi().getProductRemarkParam("remark").getValue().toString());
        params.put("shopId", mShopId);
        setUiLoading(true);
        mShopModel.requestAddProduct(params, new IAsyncResponse<MvpProductDetailEntity>() {
            @Override
            public void onResponse(MvpProductDetailEntity entity) {
                setUiLoading(false);
                showShortToast(R.string.mvp_product_release_success);
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
