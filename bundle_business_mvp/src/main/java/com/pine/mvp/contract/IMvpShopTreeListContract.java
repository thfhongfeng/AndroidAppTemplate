package com.pine.mvp.contract;

import com.pine.mvp.adapter.MvpShopListPaginationTreeAdapter;
import com.pine.tool.architecture.mvp.contract.IBaseContract;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public interface IMvpShopTreeListContract {
    interface Ui extends IBaseContract.Ui {

    }

    interface Presenter extends IBaseContract.Presenter {
        void loadShopTreeListData(boolean refresh);

        MvpShopListPaginationTreeAdapter getListAdapter();
    }
}
