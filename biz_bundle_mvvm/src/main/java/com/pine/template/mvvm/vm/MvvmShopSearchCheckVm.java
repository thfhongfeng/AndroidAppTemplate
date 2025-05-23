package com.pine.template.mvvm.vm;

import static com.pine.template.mvvm.ui.activity.MvvmShopSearchCheckActivity.REQUEST_CHECKED_LIST_KEY;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.pine.template.mvvm.MvvmKeyConstants;
import com.pine.template.mvvm.bean.MvvmShopItemEntity;
import com.pine.template.mvvm.bean.MvvmShopSearchBean;
import com.pine.template.mvvm.model.MvvmShopModel;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.binding.data.ParametricLiveData;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.response.IAsyncResponse;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public class MvvmShopSearchCheckVm extends ViewModel {
    private MvvmShopModel mShopModel = new MvvmShopModel();
    public ArrayList<MvvmShopItemEntity> mInitBelongShopList = new ArrayList<>();
    public boolean mSearchMode;

    @Override
    public boolean parseIntentData(Context activity, Bundle bundle) {
        mInitBelongShopList = bundle.getParcelableArrayList(REQUEST_CHECKED_LIST_KEY);
        return false;
    }

    @Override
    public void afterViewInit(Context activity) {
        setSearchKey(new MvvmShopSearchBean());
    }

    public void postSearch(final boolean refresh, int pageNo, int pageSize) {
        if (isUiLoading()) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(MvvmKeyConstants.PAGE_NO, String.valueOf(pageNo));
        params.put(MvvmKeyConstants.PAGE_SIZE, String.valueOf(pageSize));

        MvvmShopSearchBean searchKeyBean = mSearchKeyData.getValue();
        if (TextUtils.isEmpty(searchKeyBean.getName())) {
            mSearchMode = false;
        } else {
            mSearchMode = true;
            params.put("searchKey", searchKeyBean.getName());
        }

        setUiLoading(true);
        mShopModel.requestShopListData(params, new IAsyncResponse<ArrayList<MvvmShopItemEntity>>() {
            @Override
            public void onResponse(ArrayList<MvvmShopItemEntity> mvvmShopItemEntities) {
                setUiLoading(false);
                setShopList(mvvmShopItemEntities, refresh);
            }

            @Override
            public boolean onFail(Exception e) {
                if (e instanceof MessageException) {
                    if (!TextUtils.isEmpty(e.getMessage())) {
                        setToastMsg(e.getMessage());
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onCancel() {

            }
        });
    }

    MutableLiveData<MvvmShopSearchBean> mSearchKeyData = new MutableLiveData<>();

    public void setSearchKey(MvvmShopSearchBean searchKey) {
        mSearchKeyData.setValue(searchKey);
    }

    public MutableLiveData<MvvmShopSearchBean> getSearchKey() {
        return mSearchKeyData;
    }

    ParametricLiveData<ArrayList<MvvmShopItemEntity>, Boolean> mShopListData = new ParametricLiveData<>();

    public void setShopList(ArrayList<MvvmShopItemEntity> list, boolean refresh) {
        mShopListData.setValue(list, refresh);
    }

    public ParametricLiveData<ArrayList<MvvmShopItemEntity>, Boolean> getShopListData() {
        return mShopListData;
    }
}
