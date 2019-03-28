package com.pine.mvp.model.local;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.base.architecture.mvp.model.IModelAsyncResponse;
import com.pine.base.database.DbRequestManager;
import com.pine.base.database.IDbCommand;
import com.pine.base.database.callback.DbJsonCallback;
import com.pine.base.http.HttpRequestManager;
import com.pine.base.http.callback.HttpJsonCallback;
import com.pine.mvp.MvpConstants;
import com.pine.mvp.MvpUrlConstants;
import com.pine.mvp.bean.MvpShopAndProductEntity;
import com.pine.mvp.bean.MvpShopDetailEntity;
import com.pine.mvp.bean.MvpShopItemEntity;
import com.pine.mvp.bean.MvpTravelNoteCommentEntity;
import com.pine.mvp.bean.MvpTravelNoteDetailEntity;
import com.pine.mvp.bean.MvpTravelNoteItemEntity;
import com.pine.mvp.model.IMvpTravelNoteModel;
import com.pine.tool.util.LogUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MvpTravelNoteLocalModel implements IMvpTravelNoteModel {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private static final int DB_ADD_TRAVEL_NOTE = 1;
    private static final int DB_QUERY_TRAVEL_NOTE_DETAIL = 2;
    private static final int DB_QUERY_TRAVEL_NOTE_LIST = 3;
    private static final int DB_QUERY_TRAVEL_NOTE_COMMENT_LIST = 4;

    @Override
    public void requestAddTravelNote(Map<String, String> params,
                                     @NonNull IModelAsyncResponse<MvpTravelNoteDetailEntity> callback) {
        DbRequestManager.setJsonRequest(IDbCommand.REQUEST_ADD_TRAVEL_NOTE, params, TAG, DB_ADD_TRAVEL_NOTE,
                handleDbResponse(callback));
    }

    @Override
    public void requestTravelNoteDetailData(Map<String, String> params,
                                            @NonNull IModelAsyncResponse<MvpTravelNoteDetailEntity> callback) {
        DbRequestManager.setJsonRequest(IDbCommand.REQUEST_QUERY_TRAVEL_NOTE_DETAIL, params, TAG, DB_QUERY_TRAVEL_NOTE_DETAIL,
                handleDbResponse(callback));
    }

    @Override
    public void requestTravelNoteListData(Map<String, String> params,
                                          @NonNull IModelAsyncResponse<ArrayList<MvpTravelNoteItemEntity>> callback) {
        DbRequestManager.setJsonRequest(IDbCommand.REQUEST_QUERY_TRAVEL_NOTE_LIST, params, TAG, DB_QUERY_TRAVEL_NOTE_LIST,
                handleDbResponse(callback));
    }

    @Override
    public void requestTravelNoteCommentData(Map<String, String> params,
                                             @NonNull IModelAsyncResponse<ArrayList<MvpTravelNoteCommentEntity>> callback) {
        DbRequestManager.setJsonRequest(IDbCommand.REQUEST_QUERY_TRAVEL_NOTE_COMMENT_LIST, params, TAG, DB_QUERY_TRAVEL_NOTE_COMMENT_LIST,
                handleDbResponse(callback));
    }

    private <T> DbJsonCallback handleDbResponse(final IModelAsyncResponse<T> callback) {
        return new DbJsonCallback() {

            @Override
            public void onResponse(int what, JSONObject jsonObject) {
                if (what == DB_ADD_TRAVEL_NOTE) {
                    if (jsonObject.optBoolean(MvpConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvpConstants.DATA), new TypeToken<MvpTravelNoteDetailEntity>() {
                        }.getType());
                        callback.onResponse(retData);
                    } else {
                        callback.onFail(new Exception(jsonObject.optString("message")));
                    }
                } else if (what == DB_QUERY_TRAVEL_NOTE_DETAIL) {
                    if (jsonObject.optBoolean(MvpConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvpConstants.DATA), new TypeToken<MvpTravelNoteDetailEntity>() {
                        }.getType());
                        callback.onResponse(retData);
                    } else {
                        callback.onFail(new Exception(jsonObject.optString("message")));
                    }
                } else if (what == DB_QUERY_TRAVEL_NOTE_LIST) {
                    if (jsonObject.optBoolean(MvpConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvpConstants.DATA), new TypeToken<List<MvpTravelNoteItemEntity>>() {
                        }.getType());
                        callback.onResponse(retData);
                    } else {
                        callback.onFail(new Exception(jsonObject.optString("message")));
                    }
                } else if (what == DB_QUERY_TRAVEL_NOTE_COMMENT_LIST) {
                    if (jsonObject.optBoolean(MvpConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvpConstants.DATA), new TypeToken<List<MvpTravelNoteCommentEntity>>() {
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
