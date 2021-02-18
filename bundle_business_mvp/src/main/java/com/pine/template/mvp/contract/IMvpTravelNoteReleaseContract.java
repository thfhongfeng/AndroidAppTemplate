package com.pine.template.mvp.contract;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.pine.template.base.component.uploader.FileUploadComponent;
import com.pine.template.mvp.bean.MvpShopItemEntity;
import com.pine.tool.architecture.mvp.contract.IContract;
import com.pine.tool.bean.InputParam;

import java.util.ArrayList;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public interface IMvpTravelNoteReleaseContract {
    interface Ui extends IContract.Ui {

        void setBelongShop(String ids, String names);

        @NonNull
        InputParam getNoteTitleParam(String key);

        @NonNull
        InputParam getNoteSetOutDateParam(String key);

        @NonNull
        InputParam getNoteTravelDayCountParam(String key);

        @NonNull
        InputParam getNoteBelongShopsParam(String key, ArrayList<MvpShopItemEntity> list);

        @NonNull
        InputParam getNotePrefaceParam(String key);

        @NonNull
        InputParam getNoteContentParam(String key);
    }

    interface Presenter extends IContract.Presenter {
        @NonNull
        FileUploadComponent.OneByOneUploadAdapter getUploadAdapter();

        void selectBelongShop();

        void onBelongShopSelected(Intent data);

        void addNote();
    }
}
