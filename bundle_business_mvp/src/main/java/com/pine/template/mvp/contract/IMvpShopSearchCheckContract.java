package com.pine.template.mvp.contract;

import com.pine.template.mvp.adapter.MvpShopCheckListPaginationAdapter;
import com.pine.tool.architecture.mvp.contract.IContract;
import com.pine.tool.bean.InputParam;

/**
 * Created by tanghongfeng on 2018/11/15
 */

public interface IMvpShopSearchCheckContract {
    interface Ui extends IContract.Ui {
        InputParam getSearchKey(String key);

        void goAllSelectMode();
    }

    interface Presenter extends IContract.Presenter {
        MvpShopCheckListPaginationAdapter getListAdapter();

        void postSearch(boolean refresh);

        void completeAction();

        void clearCurCheck();
    }
}
