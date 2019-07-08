package com.pine.mvp.contract;

import com.pine.mvp.adapter.MvpShopListNoPaginationAdapter;
import com.pine.tool.architecture.mvp.contract.IBaseContract;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public interface IMvpShopNoPaginationListContract {
    interface Ui extends IBaseContract.Ui {

    }

    interface Presenter extends IBaseContract.Presenter {
        void loadShopNoPaginationListData();

        MvpShopListNoPaginationAdapter getListAdapter();
    }
}
