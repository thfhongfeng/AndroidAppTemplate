package com.pine.template.mvvm.vm;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.pine.template.mvvm.MvvmConstants;
import com.pine.template.mvvm.bean.MvvmTravelNoteCommentEntity;
import com.pine.template.mvvm.bean.MvvmTravelNoteDetailEntity;
import com.pine.template.mvvm.model.MvvmTravelNoteModel;
import com.pine.tool.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.tool.architecture.mvvm.vm.ViewModel;
import com.pine.tool.binding.data.ParametricLiveData;
import com.pine.tool.exception.MessageException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tanghongfeng on 2019/3/1
 */

public class MvvmTravelNoteDetailVm extends ViewModel {
    private String mId;
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

    private MutableLiveData<MvvmTravelNoteDetailEntity> travelNoteDetailDate = new MutableLiveData<>();

    public MutableLiveData<MvvmTravelNoteDetailEntity> getTravelNoteDetailDate() {
        return travelNoteDetailDate;
    }

    public void setTravelNoteDetail(MvvmTravelNoteDetailEntity travelNoteDetail) {
        travelNoteDetailDate.setValue(travelNoteDetail);
    }

    private ParametricLiveData<List<MvvmTravelNoteCommentEntity>, Boolean> travelNoteCommentListDate = new ParametricLiveData<>();

    public ParametricLiveData<List<MvvmTravelNoteCommentEntity>, Boolean> getTravelNoteCommentListDate() {
        return travelNoteCommentListDate;
    }

    public void setTravelNoteCommentList(List<MvvmTravelNoteCommentEntity> travelNoteCommentList, boolean refresh) {
        travelNoteCommentListDate.setValue(travelNoteCommentList, refresh);
    }
}
