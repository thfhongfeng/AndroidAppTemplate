package com.pine.template.mvvm.vm;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.pine.template.mvvm.MvvmKeyConstants;
import com.pine.template.mvvm.bean.MvvmTravelNoteItemEntity;
import com.pine.template.mvvm.model.MvvmTravelNoteModel;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.binding.data.ParametricLiveData;
import com.pine.tool.exception.MessageException;
import com.pine.tool.request.response.IAsyncResponse;

import java.util.ArrayList;
import java.util.HashMap;

public class MvvmTravelListVm extends ViewModel {
    public String mId;
    MvvmTravelNoteModel mTravelNoteModel = new MvvmTravelNoteModel();

    @Override
    public boolean parseIntentData(Context activity, Bundle bundle) {
        mId = bundle.getString("id", "");
        if (TextUtils.isEmpty(mId)) {
            finishUi();
            return true;
        }
        return false;
    }

    public void loadTravelNoteListData(final boolean refresh, int pageNo, int pageSize) {
        if (isUiLoading()) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(MvvmKeyConstants.PAGE_NO, String.valueOf(pageNo));
        params.put(MvvmKeyConstants.PAGE_SIZE, String.valueOf(pageSize));
        params.put("id", mId);
        setUiLoading(true);
        mTravelNoteModel.requestTravelNoteListData(params, new IAsyncResponse<ArrayList<MvvmTravelNoteItemEntity>>() {
            @Override
            public void onResponse(ArrayList<MvvmTravelNoteItemEntity> list) {
                setUiLoading(false);
                setTravelList(list, refresh);
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
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
                setUiLoading(false);
            }
        });
    }

    ParametricLiveData<ArrayList<MvvmTravelNoteItemEntity>, Boolean> travelListData = new ParametricLiveData<>();

    public ParametricLiveData<ArrayList<MvvmTravelNoteItemEntity>, Boolean> getTravelListData() {
        return travelListData;
    }

    public void setTravelList(ArrayList<MvvmTravelNoteItemEntity> travelList, boolean refresh) {
        travelListData.setValue(travelList, refresh);
    }
}
