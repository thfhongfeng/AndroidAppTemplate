package com.pine.mvvm.model;

import android.support.annotation.NonNull;

import com.pine.base.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.mvvm.bean.MvvmTravelNoteCommentEntity;
import com.pine.mvvm.bean.MvvmTravelNoteDetailEntity;
import com.pine.mvvm.bean.MvvmTravelNoteItemEntity;

import java.util.ArrayList;
import java.util.Map;

public interface IMvvmTravelNoteModel {
    void requestAddTravelNote(final Map<String, String> params,
                              @NonNull final IModelAsyncResponse<MvvmTravelNoteDetailEntity> callback);

    void requestTravelNoteDetailData(final Map<String, String> params,
                                     @NonNull final IModelAsyncResponse<MvvmTravelNoteDetailEntity> callback);

    void requestTravelNoteListData(final Map<String, String> params,
                                   @NonNull final IModelAsyncResponse<ArrayList<MvvmTravelNoteItemEntity>> callback);


    void requestTravelNoteCommentData(final Map<String, String> params,
                                      @NonNull final IModelAsyncResponse<ArrayList<MvvmTravelNoteCommentEntity>> callback);
}
