package com.pine.mvp.contract;

import android.support.annotation.NonNull;

import com.pine.tool.architecture.mvp.contract.IContract;
import com.pine.tool.bean.InputParam;

import java.util.HashMap;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public interface IMvpShopReleaseContract {
    interface Ui extends IContract.Ui {
        @NonNull
        InputParam getShopNameParam(String key);

        @NonNull
        InputParam getShopTypeParam(String key);

        @NonNull
        InputParam getShopTypeNameParam(String key);

        @NonNull
        InputParam getShopOnlineDateParam(String key);

        @NonNull
        InputParam getShopContactMobileParam(String key);

        @NonNull
        InputParam getShopAddressParam(String key);

        @NonNull
        InputParam getShopAddressZipCodeParam(String key);

        @NonNull
        InputParam getShopLocationLonParam(String key);

        @NonNull
        InputParam getShopLocationLatParam(String key);

        @NonNull
        InputParam getShopDetailAddressParam(String key);

        @NonNull
        InputParam getShopDescriptionParam(String key);

        @NonNull
        InputParam getShopRemarkParam(String key);

        @NonNull
        InputParam getShopImagesParam(String key);
    }

    interface Presenter extends IContract.Presenter {
        @NonNull
        String[] getShopTypeNameArr();

        @NonNull
        String[] getShopTypeValueArr();

        @NonNull
        HashMap<String, String> makeUploadDefaultParams();

        void addShop();
    }
}
