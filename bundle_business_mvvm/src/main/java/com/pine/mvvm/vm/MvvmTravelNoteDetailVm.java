package com.pine.mvvm.vm;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.text.TextUtils;

import com.pine.base.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.base.architecture.mvvm.vm.BaseViewModel;
import com.pine.base.binding.data.BaseLiveData;
import com.pine.mvvm.MvvmConstants;
import com.pine.mvvm.bean.MvvmTravelNoteCommentEntity;
import com.pine.mvvm.bean.MvvmTravelNoteDetailEntity;
import com.pine.mvvm.model.MvvmTravelNoteModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public class MvvmTravelNoteDetailVm extends BaseViewModel {
    private String mId;
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

    public void loadTravelNoteDetailData(final int pageSize) {
        if (isUiLoading()) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("id", mId);
        setUiLoading(true);
        mTravelNoteModel.requestTravelNoteDetailData(params, new
                IModelAsyncResponse<MvvmTravelNoteDetailEntity>() {
                    @Override
                    public void onResponse(MvvmTravelNoteDetailEntity entity) {
                        setUiLoading(false);
                        setTravelNoteDetail(entity);
                        loadTravelNoteCommentData(true, 1, pageSize);
                    }

                    @Override
                    public boolean onFail(Exception e) {
                        setUiLoading(false);
                        return false;
                    }

                    @Override
                    public void onCancel() {
                        setUiLoading(false);
                    }
                });
    }

    public void loadTravelNoteCommentData(final boolean refresh, int pageNo, int pageSize) {
        if (isUiLoading()) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put(MvvmConstants.PAGE_NO, String.valueOf(pageNo));
        params.put(MvvmConstants.PAGE_SIZE, String.valueOf(pageSize));
        params.put("id", mId);
        setUiLoading(true);
        mTravelNoteModel.requestTravelNoteCommentData(params, new IModelAsyncResponse<ArrayList<MvvmTravelNoteCommentEntity>>() {
            @Override
            public void onResponse(ArrayList<MvvmTravelNoteCommentEntity> list) {
                setUiLoading(false);
                setTravelNoteCommentList(list, refresh);
            }

            @Override
            public boolean onFail(Exception e) {
                setUiLoading(false);
                return false;
            }

            @Override
            public void onCancel() {
                setUiLoading(false);
            }
        });
    }

    private MutableLiveData<MvvmTravelNoteDetailEntity> travelNoteDetailDate = new MutableLiveData<>();

    public MutableLiveData<MvvmTravelNoteDetailEntity> getTravelNoteDetailDate() {
        return travelNoteDetailDate;
    }

    public void setTravelNoteDetail(MvvmTravelNoteDetailEntity travelNoteDetail) {
        travelNoteDetailDate.setValue(travelNoteDetail);
    }

    private BaseLiveData<List<MvvmTravelNoteCommentEntity>, Boolean> travelNoteCommentListDate = new BaseLiveData<>();

    public BaseLiveData<List<MvvmTravelNoteCommentEntity>, Boolean> getTravelNoteCommentListDate() {
        return travelNoteCommentListDate;
    }

    public void setTravelNoteCommentList(List<MvvmTravelNoteCommentEntity> travelNoteCommentList, boolean refresh) {
        travelNoteCommentListDate.setValue(travelNoteCommentList, refresh);
    }
}
