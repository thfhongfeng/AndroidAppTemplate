package com.pine.template.mvp.contract;

import androidx.annotation.NonNull;

import com.pine.template.base.bean.InputParam;
import com.pine.tool.architecture.mvp.contract.IContract;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public interface IMvpProductReleaseContract {
    interface Ui extends IContract.Ui {
        @NonNull
        InputParam getProductNameParam(String key);

        @NonNull
        InputParam getProductPriceParam(String key);

        @NonNull
        InputParam getProductShelvePriceParam(String key);

        @NonNull
        InputParam getProductShelveDateParam(String key);

        @NonNull
        InputParam getProductDescriptionParam(String key);

        @NonNull
        InputParam getProductRemarkParam(String key);
    }

    interface Presenter extends IContract.Presenter {
        void addProduct();
    }
}
