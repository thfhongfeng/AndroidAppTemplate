package com.pine.mvp.contract;

import com.pine.mvp.adapter.MvpShopCheckListPaginationAdapter;
import com.pine.tool.architecture.mvp.contract.IBaseContract;
import com.pine.tool.bean.BaseInputParam;

/**
 * Created by tanghongfeng on 2018/11/15
 */

public interface IMvpShopSearchCheckContract {
    interface Ui extends IBaseContract.Ui {
        BaseInputParam getSearchKey(String key);

        void goAllSelectMode();
    }

    interface Presenter extends IBaseContract.Presenter {
        MvpShopCheckListPaginationAdapter getListAdapter();

        void postSearch(boolean refresh);

        void completeAction();

        void clearCurCheck();
    }
}
