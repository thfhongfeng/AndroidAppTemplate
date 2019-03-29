package com.pine.mvvm.model.local;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.base.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.base.request.database.DbRequestManager;
import com.pine.base.request.database.IDbCommand;
import com.pine.base.request.database.callback.DbJsonCallback;
import com.pine.mvvm.MvvmConstants;
import com.pine.mvvm.bean.MvvmTravelNoteCommentEntity;
import com.pine.mvvm.bean.MvvmTravelNoteDetailEntity;
import com.pine.mvvm.bean.MvvmTravelNoteItemEntity;
import com.pine.mvvm.model.IMvvmTravelNoteModel;
import com.pine.tool.util.LogUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MvvmTravelNoteLocalModel implements IMvvmTravelNoteModel {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private static final int DB_ADD_TRAVEL_NOTE = 1;
    private static final int DB_QUERY_TRAVEL_NOTE_DETAIL = 2;
    private static final int DB_QUERY_TRAVEL_NOTE_LIST = 3;
    private static final int DB_QUERY_TRAVEL_NOTE_COMMENT_LIST = 4;

    @Override
    public void requestAddTravelNote(Map<String, String> params, @NonNull IModelAsyncResponse<MvvmTravelNoteDetailEntity> callback) {
        DbRequestManager.setJsonRequest(IDbCommand.REQUEST_ADD_TRAVEL_NOTE, params, TAG, DB_ADD_TRAVEL_NOTE,
                handleDbResponse(callback));
    }

    @Override
    public void requestTravelNoteDetailData(Map<String, String> params, @NonNull IModelAsyncResponse<MvvmTravelNoteDetailEntity> callback) {
        DbRequestManager.setJsonRequest(IDbCommand.REQUEST_QUERY_TRAVEL_NOTE_DETAIL, params, TAG, DB_QUERY_TRAVEL_NOTE_DETAIL,
                handleDbResponse(callback));
    }

    @Override
    public void requestTravelNoteListData(Map<String, String> params, @NonNull IModelAsyncResponse<ArrayList<MvvmTravelNoteItemEntity>> callback) {
        DbRequestManager.setJsonRequest(IDbCommand.REQUEST_QUERY_TRAVEL_NOTE_LIST, params, TAG, DB_QUERY_TRAVEL_NOTE_LIST,
                handleDbResponse(callback));
    }

    @Override
    public void requestTravelNoteCommentData(Map<String, String> params, @NonNull IModelAsyncResponse<ArrayList<MvvmTravelNoteCommentEntity>> callback) {
        DbRequestManager.setJsonRequest(IDbCommand.REQUEST_QUERY_TRAVEL_NOTE_COMMENT_LIST, params, TAG, DB_QUERY_TRAVEL_NOTE_COMMENT_LIST,
                handleDbResponse(callback));
    }

    private <T> DbJsonCallback handleDbResponse(final com.pine.base.architecture.mvvm.model.IModelAsyncResponse<T> callback) {
        return new DbJsonCallback() {

            @Override
            public void onResponse(int what, JSONObject jsonObject) {
                if (what == DB_ADD_TRAVEL_NOTE) {
                    if (jsonObject.optBoolean(MvvmConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvvmConstants.DATA), new TypeToken<MvvmTravelNoteDetailEntity>() {
                        }.getType());
                        callback.onResponse(retData);
                    } else {
                        callback.onFail(new Exception(jsonObject.optString("message")));
                    }
                } else if (what == DB_QUERY_TRAVEL_NOTE_DETAIL) {
                    if (jsonObject.optBoolean(MvvmConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvvmConstants.DATA), new TypeToken<MvvmTravelNoteDetailEntity>() {
                        }.getType());
                        callback.onResponse(retData);
                    } else {
                        callback.onFail(new Exception(jsonObject.optString("message")));
                    }
                } else if (what == DB_QUERY_TRAVEL_NOTE_LIST) {
                    if (jsonObject.optBoolean(MvvmConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvvmConstants.DATA), new TypeToken<List<MvvmTravelNoteItemEntity>>() {
                        }.getType());
                        callback.onResponse(retData);
                    } else {
                        callback.onFail(new Exception(jsonObject.optString("message")));
                    }
                } else if (what == DB_QUERY_TRAVEL_NOTE_COMMENT_LIST) {
                    if (jsonObject.optBoolean(MvvmConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvvmConstants.DATA), new TypeToken<List<MvvmTravelNoteCommentEntity>>() {
                        }.getType());
                        callback.onResponse(retData);
                    } else {
                        callback.onFail(new Exception(jsonObject.optString("message")));
                    }
                }
            }

            public boolean onFail(int what, Exception e) {
                return callback.onFail(e);
            }

            @Override
            public void onCancel(int what) {
                callback.onCancel();
            }
        };
    }
}
