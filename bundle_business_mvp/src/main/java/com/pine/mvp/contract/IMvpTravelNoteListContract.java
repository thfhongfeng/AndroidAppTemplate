package com.pine.mvp.contract;

import com.pine.mvp.adapter.MvpTravelNoteListPaginationAdapter;
import com.pine.tool.architecture.mvp.contract.IContract;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public interface IMvpTravelNoteListContract {
    interface Ui extends IContract.Ui {

    }

    interface Presenter extends IContract.Presenter {
        void loadTravelNoteListData(boolean refresh);

        MvpTravelNoteListPaginationAdapter getListAdapter();

        void goToAddTravelNoteActivity();
    }
}
