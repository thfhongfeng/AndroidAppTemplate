package com.pine.mvp.contract;

import android.support.annotation.NonNull;

import com.pine.tool.architecture.mvp.contract.IBaseContract;
import com.pine.tool.bean.BaseInputParam;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public interface IMvpProductReleaseContract {
    interface Ui extends IBaseContract.Ui {
        @NonNull
        BaseInputParam getProductNameParam(String key);

        @NonNull
        BaseInputParam getProductPriceParam(String key);

        @NonNull
        BaseInputParam getProductShelvePriceParam(String key);

        @NonNull
        BaseInputParam getProductShelveDateParam(String key);

        @NonNull
        BaseInputParam getProductDescriptionParam(String key);

        @NonNull
        BaseInputParam getProductRemarkParam(String key);
    }

    interface Presenter extends IBaseContract.Presenter {
        void addProduct();
    }
}
