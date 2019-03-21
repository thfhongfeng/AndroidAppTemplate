package com.pine.mvvm.model.local;

import android.support.annotation.NonNull;

import com.pine.base.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.mvvm.bean.MvvmTravelNoteCommentEntity;
import com.pine.mvvm.bean.MvvmTravelNoteDetailEntity;
import com.pine.mvvm.bean.MvvmTravelNoteItemEntity;
import com.pine.mvvm.model.IMvvmTravelNoteModel;

import java.util.ArrayList;
import java.util.Map;

public class MvvmTravelNoteLocalModel implements IMvvmTravelNoteModel {
    @Override
    public void requestAddTravelNote(Map<String, String> params, @NonNull IModelAsyncResponse<MvvmTravelNoteDetailEntity> callback) {

    }

    @Override
    public void requestTravelNoteDetailData(Map<String, String> params, @NonNull IModelAsyncResponse<MvvmTravelNoteDetailEntity> callback) {

    }

    @Override
    public void requestTravelNoteListData(Map<String, String> params, @NonNull IModelAsyncResponse<ArrayList<MvvmTravelNoteItemEntity>> callback) {

    }

    @Override
    public void requestTravelNoteCommentData(Map<String, String> params, @NonNull IModelAsyncResponse<ArrayList<MvvmTravelNoteCommentEntity>> callback) {

    }
}
