package com.pine.template.mvp.contract;

import com.pine.template.base.component.share.bean.ShareBean;
import com.pine.tool.architecture.mvp.contract.IContract;

import java.util.ArrayList;

/**
 * Created by tanghongfeng on 2018/9/14
 */

public interface IMvpWebViewContract {
    interface Ui extends IContract.Ui {
        void loadUrl(String url);
    }

    interface Presenter extends IContract.Presenter {
        String getH5Url();

        ArrayList<ShareBean> getShareBeanList();
    }
}
