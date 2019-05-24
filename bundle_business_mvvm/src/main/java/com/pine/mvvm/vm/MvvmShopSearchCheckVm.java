package com.pine.mvvm.vm;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.text.TextUtils;

import com.pine.base.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.base.architecture.mvvm.vm.BaseViewModel;
import com.pine.base.binding.data.BaseLiveData;
import com.pine.base.exception.BusinessException;
import com.pine.mvvm.MvvmConstants;
import com.pine.mvvm.bean.MvvmShopItemEntity;
import com.pine.mvvm.bean.MvvmShopSearchBean;
import com.pine.mvvm.model.MvvmShopModel;

import java.util.ArrayList;
import java.util.HashMap;

import static com.pine.mvvm.ui.activity.MvvmShopSearchCheckActivity.REQUEST_CHECKED_LIST_KEY;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public class MvvmShopSearchCheckVm extends BaseViewModel {
    private MvvmShopModel mShopModel = new MvvmShopModel();
    public ArrayList<MvvmShopItemEntity> mInitBelongShopList = new ArrayList<>();
    public boolean mSearchMode;

    @Override
    public boolean parseIntentData(Bundle bundle) {
        mInitBelongShopList = bundle.getParcelableArrayList(REQUEST_CHECKED_LIST_KEY);
        return false;
    }

    @Override
    public void afterViewInit() {
        setSearchKey(new MvvmShopSearchBean());
    }

    public void postSearch(final boolean refresh, int pageNo, int pageSize) {
        if (isUiLoading()) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(MvvmConstants.PAGE_NO, String.valueOf(pageNo));
        params.put(MvvmConstants.PAGE_SIZE, String.valueOf(pageSize));

        MvvmShopSearchBean searchKeyBean = mSearchKeyData.getValue();
        if (TextUtils.isEmpty(searchKeyBean.getName())) {
            mSearchMode = false;
        } else {
            mSearchMode = true;
            params.put("searchKey", searchKeyBean.getName());
        }

        setUiLoading(true);
        mShopModel.requestShopListData(params, new IModelAsyncResponse<ArrayList<MvvmShopItemEntity>>() {
            @Override
            public void onResponse(ArrayList<MvvmShopItemEntity> mvvmShopItemEntities) {
                setUiLoading(false);
                setShopList(mvvmShopItemEntities, refresh);
            }

            @Override
            public boolean onFail(Exception e) {
                if (e instanceof BusinessException) {
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

    BaseLiveData<ArrayList<MvvmShopItemEntity>, Boolean> mShopListData = new BaseLiveData<>();

    public void setShopList(ArrayList<MvvmShopItemEntity> list, boolean refresh) {
        mShopListData.setValue(list, refresh);
    }

    public BaseLiveData<ArrayList<MvvmShopItemEntity>, Boolean> getShopListData() {
        return mShopListData;
    }
}
