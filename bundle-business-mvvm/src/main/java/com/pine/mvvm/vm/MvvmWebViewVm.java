package com.pine.mvvm.vm;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;

import com.pine.base.architecture.mvvm.vm.BaseViewModel;
import com.pine.base.component.share.bean.ShareBean;
import com.pine.mvvm.MvvmUrlConstants;

import java.util.ArrayList;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public class MvvmWebViewVm extends BaseViewModel {
    private String mH5Url;

    @Override
    public boolean parseIntentData(Bundle bundle) {
        mH5Url = bundle.getString("url", MvvmUrlConstants.H5_DefaultUrl);
        return false;
    }

    @Override
    public void afterViewInit() {
        setH5Url(mH5Url);
        setShareBeanList(getShareBeanList());
    }

    private ArrayList<ShareBean> getShareBeanList() {
        ArrayList<ShareBean> shareBeanList = new ArrayList<>();
        ShareBean shareBean = new ShareBean(ShareBean.SHARE_TARGET_QQ, ShareBean.SHARE_CONTENT_TYPE_TEXT_URL,
                "Item Detail Title", "Item Detail Text",
                "Item Detail Desc", getH5UrlData().getValue());
        shareBeanList.add(shareBean);
        shareBean = new ShareBean(ShareBean.SHARE_TARGET_WX, ShareBean.SHARE_CONTENT_TYPE_TEXT_URL,
                "Item Detail Title", "Item Detail Text",
                "Item Detail Desc", getH5UrlData().getValue());
        shareBeanList.add(shareBean);
        shareBean = new ShareBean(ShareBean.SHARE_TARGET_WX_FRIEND_CIRCLE, ShareBean.SHARE_CONTENT_TYPE_TEXT_URL,
                "Item Detail Title", "Item Detail Text",
                "Item Detail Desc", getH5UrlData().getValue());
        shareBeanList.add(shareBean);
        shareBean = new ShareBean(ShareBean.SHARE_TARGET_WEI_BO, ShareBean.SHARE_CONTENT_TYPE_TEXT_URL,
                "Item Detail Title", "Item Detail Text",
                "Item Detail Desc", getH5UrlData().getValue());
        shareBeanList.add(shareBean);
        return shareBeanList;
    }

    private MutableLiveData<String> h5UrlData = new MutableLiveData<>();

    public MutableLiveData<String> getH5UrlData() {
        return h5UrlData;
    }

    public void setH5Url(String h5Url) {
        h5UrlData.setValue(h5Url);
    }

    private MutableLiveData<ArrayList<ShareBean>> shareBeanListData = new MutableLiveData<>();

    public void setShareBeanList(ArrayList<ShareBean> shareBeanList) {
        shareBeanListData.setValue(shareBeanList);
    }

    public MutableLiveData<ArrayList<ShareBean>> getShareBeanListData() {
        return shareBeanListData;
    }
}