package com.pine.mvp.contract;

import com.pine.mvp.bean.MvpShopDetailEntity;
import com.pine.tool.architecture.mvp.contract.IContract;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public interface IMvpShopDetailContract {
    interface Ui extends IContract.Ui {
        void setupShopDetail(MvpShopDetailEntity entity);
    }

    interface Presenter extends IContract.Presenter {
        void loadShopDetailData();

        void showMarkerInMap();

        void goToShopH5Activity();

        void goToTravelNoteListActivity();

        void goAddProductActivity();
    }
}
