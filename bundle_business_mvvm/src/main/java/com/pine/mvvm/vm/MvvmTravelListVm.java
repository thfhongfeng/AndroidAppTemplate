package com.pine.mvvm.vm;

import android.os.Bundle;
import android.text.TextUtils;

import com.pine.mvvm.MvvmConstants;
import com.pine.mvvm.bean.MvvmTravelNoteItemEntity;
import com.pine.mvvm.model.MvvmTravelNoteModel;
import com.pine.tool.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.binding.data.CustomLiveData;
import com.pine.tool.exception.BusinessException;

import java.util.ArrayList;
import java.util.HashMap;

public class MvvmTravelListVm extends ViewModel {
    public String mId;
    MvvmTravelNoteModel mTravelNoteModel = new MvvmTravelNoteModel();

    @Override
    public boolean parseIntentData(Bundle bundle) {
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
        params.put(MvvmConstants.PAGE_NO, String.valueOf(pageNo));
        params.put(MvvmConstants.PAGE_SIZE, String.valueOf(pageSize));
        params.put("id", mId);
        setUiLoading(true);
        mTravelNoteModel.requestTravelNoteListData(params, new IModelAsyncResponse<ArrayList<MvvmTravelNoteItemEntity>>() {
            @Override
            public void onResponse(ArrayList<MvvmTravelNoteItemEntity> list) {
                setUiLoading(false);
                setTravelList(list, refresh);
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
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
                setUiLoading(false);
            }
        });
    }

    CustomLiveData<ArrayList<MvvmTravelNoteItemEntity>, Boolean> travelListData = new CustomLiveData<>();

    public CustomLiveData<ArrayList<MvvmTravelNoteItemEntity>, Boolean> getTravelListData() {
        return travelListData;
    }

    public void setTravelList(ArrayList<MvvmTravelNoteItemEntity> travelList, boolean refresh) {
        travelListData.setValue(travelList, refresh);
    }
}
