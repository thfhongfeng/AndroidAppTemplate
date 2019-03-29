package com.pine.mvp.model.local;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.base.architecture.mvp.model.IModelAsyncResponse;
import com.pine.base.request.database.DbRequestManager;
import com.pine.base.request.database.IDbCommand;
import com.pine.base.request.database.callback.DbJsonCallback;
import com.pine.mvp.MvpConstants;
import com.pine.mvp.bean.MvpShopAndProductEntity;
import com.pine.mvp.bean.MvpShopDetailEntity;
import com.pine.mvp.bean.MvpShopItemEntity;
import com.pine.mvp.model.IMvpShopModel;
import com.pine.tool.util.LogUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MvpShopLocalModel implements IMvpShopModel {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private static final int DB_ADD_SHOP = 1;
    private static final int DB_QUERY_SHOP_DETAIL = 2;
    private static final int DB_QUERY_SHOP_LIST = 3;
    private static final int DB_QUERY_SHOP_AND_PRODUCT_LIST = 4;

    @Override
    public void requestAddShop(Map<String, String> params,
                               @NonNull IModelAsyncResponse<MvpShopDetailEntity> callback) {
        DbRequestManager.setJsonRequest(IDbCommand.REQUEST_ADD_SHOP, params, TAG, DB_ADD_SHOP,
                handleDbResponse(callback));
    }

    @Override
    public void requestShopDetailData(Map<String, String> params,
                                      @NonNull IModelAsyncResponse<MvpShopDetailEntity> callback) {
        DbRequestManager.setJsonRequest(IDbCommand.REQUEST_QUERY_SHOP_DETAIL, params, TAG, DB_QUERY_SHOP_DETAIL,
                handleDbResponse(callback));
    }

    @Override
    public void requestShopListData(Map<String, String> params,
                                    @NonNull IModelAsyncResponse<ArrayList<MvpShopItemEntity>> callback) {
        DbRequestManager.setJsonRequest(IDbCommand.REQUEST_QUERY_SHOP_LIST, params, TAG, DB_QUERY_SHOP_LIST,
                handleDbResponse(callback));
    }

    @Override
    public void requestShopAndProductListData(Map<String, String> params,
                                              @NonNull IModelAsyncResponse<ArrayList<MvpShopAndProductEntity>> callback) {
        DbRequestManager.setJsonRequest(IDbCommand.REQUEST_QUERY_SHOP_AND_PRODUCT_LIST, params, TAG, DB_QUERY_SHOP_AND_PRODUCT_LIST,
                handleDbResponse(callback));
    }

    private <T> DbJsonCallback handleDbResponse(final IModelAsyncResponse<T> callback) {
        return new DbJsonCallback() {

            @Override
            public void onResponse(int what, JSONObject jsonObject) {
                if (what == DB_ADD_SHOP) {
                    if (jsonObject.optBoolean(MvpConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvpConstants.DATA), new TypeToken<MvpShopDetailEntity>() {
                        }.getType());
                        callback.onResponse(retData);
                    } else {
                        callback.onFail(new Exception(jsonObject.optString("message")));
                    }
                } else if (what == DB_QUERY_SHOP_DETAIL) {
                    if (jsonObject.optBoolean(MvpConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvpConstants.DATA), new TypeToken<MvpShopDetailEntity>() {
                        }.getType());
                        callback.onResponse(retData);
                    } else {
                        callback.onFail(new Exception(jsonObject.optString("message")));
                    }
                } else if (what == DB_QUERY_SHOP_LIST) {
                    if (jsonObject.optBoolean(MvpConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvpConstants.DATA), new TypeToken<List<MvpShopItemEntity>>() {
                        }.getType());
                        callback.onResponse(retData);
                    } else {
                        callback.onFail(new Exception(jsonObject.optString("message")));
                    }
                } else if (what == DB_QUERY_SHOP_AND_PRODUCT_LIST) {
                    if (jsonObject.optBoolean(MvpConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvpConstants.DATA), new TypeToken<List<MvpShopAndProductEntity>>() {
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
