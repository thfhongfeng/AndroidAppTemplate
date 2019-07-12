package com.pine.mvp.contract;

import com.pine.mvp.adapter.MvpShopListPaginationAdapter;
import com.pine.tool.architecture.mvp.contract.IContract;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public interface IMvpShopPaginationContract {
    interface Ui extends IContract.Ui {

    }

    interface Presenter extends IContract.Presenter {
        void loadShopPaginationListData(boolean refresh);

        MvpShopListPaginationAdapter getListAdapter();
    }
}
