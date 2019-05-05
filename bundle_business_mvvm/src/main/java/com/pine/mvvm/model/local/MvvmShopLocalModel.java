package com.pine.mvvm.model.local;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.base.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.base.request.database.DbRequestManager;
import com.pine.base.request.database.callback.DbJsonCallback;
import com.pine.mvvm.MvvmConstants;
import com.pine.mvvm.bean.MvvmShopAndProductEntity;
import com.pine.mvvm.bean.MvvmShopDetailEntity;
import com.pine.mvvm.bean.MvvmShopItemEntity;
import com.pine.mvvm.model.IMvvmShopModel;
import com.pine.tool.util.LogUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MvvmShopLocalModel implements IMvvmShopModel {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private static final int DB_ADD_SHOP = 1;
    private static final int DB_QUERY_SHOP_DETAIL = 2;
    private static final int DB_QUERY_SHOP_LIST = 3;
    private static final int DB_QUERY_SHOP_AND_PRODUCT_LIST = 4;

    @Override
    public void requestAddShop(Map<String, String> params, @NonNull IModelAsyncResponse<MvvmShopDetailEntity> callback) {
        DbRequestManager.setJsonRequest(IDbCommand.REQUEST_ADD_SHOP, params, TAG, DB_ADD_SHOP,
                handleDbResponse(callback));
    }

    @Override
    public void requestShopDetailData(Map<String, String> params, @NonNull IModelAsyncResponse<MvvmShopDetailEntity> callback) {
        DbRequestManager.setJsonRequest(IDbCommand.REQUEST_QUERY_SHOP_DETAIL, params, TAG, DB_QUERY_SHOP_DETAIL,
                handleDbResponse(callback));
    }

    @Override
    public void requestShopListData(Map<String, String> params, @NonNull IModelAsyncResponse<ArrayList<MvvmShopItemEntity>> callback) {
        DbRequestManager.setJsonRequest(IDbCommand.REQUEST_QUERY_SHOP_LIST, params, TAG, DB_QUERY_SHOP_LIST,
                handleDbResponse(callback));
    }

    @Override
    public void requestShopAndProductListData(Map<String, String> params, @NonNull IModelAsyncResponse<ArrayList<MvvmShopAndProductEntity>> callback) {
        DbRequestManager.setJsonRequest(IDbCommand.REQUEST_QUERY_SHOP_AND_PRODUCT_LIST, params, TAG, DB_QUERY_SHOP_AND_PRODUCT_LIST,
                handleDbResponse(callback));
    }

    private <T> DbJsonCallback handleDbResponse(final com.pine.base.architecture.mvvm.model.IModelAsyncResponse<T> callback) {
        return new DbJsonCallback() {

            @Override
            public void onResponse(int what, JSONObject jsonObject) {
                if (what == DB_ADD_SHOP) {
                    if (jsonObject.optBoolean(MvvmConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvvmConstants.DATA), new TypeToken<MvvmShopDetailEntity>() {
                        }.getType());
                        callback.onResponse(retData);
                    } else {
                        callback.onFail(new Exception(jsonObject.optString("message")));
                    }
                } else if (what == DB_QUERY_SHOP_DETAIL) {
                    if (jsonObject.optBoolean(MvvmConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvvmConstants.DATA), new TypeToken<MvvmShopDetailEntity>() {
                        }.getType());
                        callback.onResponse(retData);
                    } else {
                        callback.onFail(new Exception(jsonObject.optString("message")));
                    }
                } else if (what == DB_QUERY_SHOP_LIST) {
                    if (jsonObject.optBoolean(MvvmConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvvmConstants.DATA), new TypeToken<List<MvvmShopItemEntity>>() {
                        }.getType());
                        callback.onResponse(retData);
                    } else {
                        callback.onFail(new Exception(jsonObject.optString("message")));
                    }
                } else if (what == DB_QUERY_SHOP_AND_PRODUCT_LIST) {
                    if (jsonObject.optBoolean(MvvmConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvvmConstants.DATA), new TypeToken<List<MvvmShopAndProductEntity>>() {
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
