package com.pine.template.mvvm.vm;

import android.content.Context;
import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;

import com.pine.template.base.component.share.bean.ShareBean;
import com.pine.template.base.component.share.bean.UrlTextShareBean;
import com.pine.template.mvvm.MvvmUrlConstants;
import com.pine.tool.architecture.mvvm.vm.ViewModel;

import java.util.ArrayList;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public class MvvmWebViewVm extends ViewModel {
    private String mH5Url;

    @Override
    public boolean parseIntentData(Context activity, Bundle bundle) {
        mH5Url = bundle.getString("url", MvvmUrlConstants.H5_DefaultUrl);
        return false;
    }

    @Override
    public void afterViewInit(Context activity) {
        setH5Url(mH5Url);
        setShareBeanList(getShareBeanList());
    }

    private ArrayList<ShareBean> getShareBeanList() {
        ArrayList<ShareBean> shareBeanList = new ArrayList<>();
        UrlTextShareBean shareBean = new UrlTextShareBean(ShareBean.SHARE_TARGET_QQ, ShareBean.SHARE_CONTENT_TYPE_TEXT_URL,
                "Item Detail Title", "Item Detail Text",
                "Item Detail Desc", getH5UrlData().getValue());
        shareBeanList.add(shareBean);
        shareBean = new UrlTextShareBean(ShareBean.SHARE_TARGET_WX, ShareBean.SHARE_CONTENT_TYPE_TEXT_URL,
                "Item Detail Title", "Item Detail Text",
                "Item Detail Desc", getH5UrlData().getValue());
        shareBeanList.add(shareBean);
        shareBean = new UrlTextShareBean(ShareBean.SHARE_TARGET_WX_FRIEND_CIRCLE, ShareBean.SHARE_CONTENT_TYPE_TEXT_URL,
                "Item Detail Title", "Item Detail Text",
                "Item Detail Desc", getH5UrlData().getValue());
        shareBeanList.add(shareBean);
        shareBean = new UrlTextShareBean(ShareBean.SHARE_TARGET_WEI_BO, ShareBean.SHARE_CONTENT_TYPE_TEXT_URL,
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
