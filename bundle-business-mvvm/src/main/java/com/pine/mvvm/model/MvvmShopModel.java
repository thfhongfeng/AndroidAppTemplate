package com.pine.mvvm.model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.base.architecture.mvvm.model.IModelAsyncResponse;
import com.pine.base.http.HttpRequestManager;
import com.pine.base.http.callback.HttpJsonCallback;
import com.pine.mvvm.MvvmConstants;
import com.pine.mvvm.MvvmUrlConstants;
import com.pine.mvvm.bean.MvvmShopAndProductEntity;
import com.pine.mvvm.bean.MvvmShopDetailEntity;
import com.pine.mvvm.bean.MvvmShopItemEntity;
import com.pine.tool.util.DecimalUtils;
import com.pine.tool.util.GPSUtils;
import com.pine.tool.util.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by tanghongfeng on 2018/9/28
 */

public class MvvmShopModel {
    private static final int HTTP_ADD_SHOP = 1;
    private static final int HTTP_QUERY_SHOP_DETAIL = 2;
    private static final int HTTP_QUERY_SHOP_LIST = 3;
    private static final int HTTP_QUERY_SHOP_AND_PRODUCT_LIST = 4;
    private final String TAG = LogUtils.makeLogTag(this.getClass());

    public void requestAddShop(final HashMap<String, String> params,
                               @NonNull final IModelAsyncResponse<MvvmShopDetailEntity> callback) {
        String url = MvvmUrlConstants.Add_Shop;
        HttpRequestManager.setJsonRequest(url, params, TAG, HTTP_ADD_SHOP,
                handleHttpResponse(callback));
    }

    public void requestShopDetailData(final Map<String, String> params,
                                      @NonNull final IModelAsyncResponse<MvvmShopDetailEntity> callback) {
        String url = MvvmUrlConstants.Query_ShopDetail;
        HttpRequestManager.setJsonRequest(url, params, TAG, HTTP_QUERY_SHOP_DETAIL,
                handleHttpResponse(callback));
    }

    public void requestShopListData(final Map<String, String> params,
                                    @NonNull final IModelAsyncResponse<ArrayList<MvvmShopItemEntity>> callback) {
        String url = MvvmUrlConstants.Query_ShopList;
        HttpRequestManager.setJsonRequest(url, params, TAG, HTTP_QUERY_SHOP_LIST,
                handleHttpResponse(callback));
    }

    public void requestShopAndProductListData(Map<String, String> params,
                                              @NonNull final IModelAsyncResponse<ArrayList<MvvmShopAndProductEntity>> callback) {
        String url = MvvmUrlConstants.Query_ShopAndProductList;
        HttpRequestManager.setJsonRequest(url, params, TAG, HTTP_QUERY_SHOP_AND_PRODUCT_LIST,
                handleHttpResponse(callback));
    }

    private <T> HttpJsonCallback handleHttpResponse(final IModelAsyncResponse<T> callback) {
        return new HttpJsonCallback() {
            @Override
            public void onResponse(int what, JSONObject jsonObject) {
                if (what == HTTP_ADD_SHOP) {
                    // Test code begin
                    jsonObject = getShopDetailData();
                    // Test code end
                    if (jsonObject.optBoolean(MvvmConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvvmConstants.DATA), new TypeToken<MvvmShopDetailEntity>() {
                        }.getType());
                        callback.onResponse(retData);
                    } else {
                        callback.onFail(new Exception(jsonObject.optString("message")));
                    }
                } else if (what == HTTP_QUERY_SHOP_DETAIL) {
                    // Test code begin
                    jsonObject = getShopDetailData();
                    // Test code end
                    if (jsonObject.optBoolean(MvvmConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvvmConstants.DATA), new TypeToken<MvvmShopDetailEntity>() {
                        }.getType());
                        callback.onResponse(retData);
                    } else {
                        callback.onFail(new Exception(jsonObject.optString("message")));
                    }
                } else if (what == HTTP_QUERY_SHOP_LIST) {
                    // Test code begin
                    jsonObject = getShopListData();
                    // Test code end
                    if (jsonObject.optBoolean(MvvmConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvvmConstants.DATA), new TypeToken<List<MvvmShopItemEntity>>() {
                        }.getType());
                        callback.onResponse(retData);
                    } else {
                        callback.onFail(new Exception(jsonObject.optString("message")));
                    }
                } else if (what == HTTP_QUERY_SHOP_AND_PRODUCT_LIST) {
                    // Test code begin
                    jsonObject = getShopAndProductListData();
                    // Test code end
                    if (jsonObject.optBoolean(MvvmConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvvmConstants.DATA), new TypeToken<List<MvvmShopAndProductEntity>>() {
                        }.getType());
                        callback.onResponse(retData);
                    } else {
                        callback.onFail(new Exception(jsonObject.optString("message")));
                    }
                }
            }

            @Override
            public boolean onFail(int what, Exception e) {
                return callback.onFail(e);
            }

            @Override
            public void onCancel(int what) {
                callback.onCancel();
            }
        };
    }

    // Test code begin
    private JSONObject getShopDetailData() {
        double endLatBd = 31.221367;
        double endLonBd = 121.635707;
        double startLatBd = DecimalUtils.add(endLatBd, new Random().nextDouble(), 6);
        double startLonBd = DecimalUtils.add(endLonBd, new Random().nextDouble(), 6);
        double[] locations = GPSUtils.bd09_To_gps84(endLatBd, endLonBd);
        double distance = GPSUtils.getDistance(locations[0], locations[1],
                startLatBd, startLonBd);
        String distanceStr = String.valueOf(distance);
        int startIndex = new Random().nextInt(10000);
        String res = "{success:true,code:200,message:'',data:" +
                "{id:'" + startIndex + "',name:'Shop Item " + startIndex +
                "', distance:'" + distanceStr + "',imgUrl:''," +
                "description:'Shop Detail description Shop Detail description Shop Detail description Shop Detail description Shop Detail description'}}";
        try {
            return new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private JSONObject getShopListData() {
        if (new Random().nextInt(10) == 9) {
            try {
                return new JSONObject("{success:true,code:200,message:'',data:[]}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        double endLatBd = 31.221367;
        double endLonBd = 121.635707;
        double startLatBd = DecimalUtils.add(endLatBd, new Random().nextDouble(), 6);
        double startLonBd = DecimalUtils.add(endLonBd, new Random().nextDouble(), 6);
        double[] locations = GPSUtils.bd09_To_gps84(endLatBd, endLonBd);
        double distance = GPSUtils.getDistance(locations[0], locations[1],
                startLatBd, startLonBd);
        String distanceStr = String.valueOf(distance);
        int startIndex = new Random().nextInt(10000);
        String res = "{success:true,code:200,message:'',data:" +
                "[{id:'" + startIndex + "',name:'Shop Item " + startIndex +
                "', distance:'" + distanceStr + "',imgUrl:''}";
        for (int i = 1; i < 10; i++) {
            distance += 1333;
            distanceStr = String.valueOf(distance);
            res += ",{id:'" + (startIndex + i) + "',name:'Shop Item " + (startIndex + i) +
                    "', distance:'" + distanceStr + "',imgUrl:''}";
        }
        res += "]}";
        try {
            return new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private JSONObject getShopAndProductListData() {
        if (new Random().nextInt(5) == 4) {
            try {
                return new JSONObject("{success:true,code:200,message:'',data:[]}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        double endLatBd = 31.221367;
        double endLonBd = 121.635707;
        double startLatBd = DecimalUtils.add(endLatBd, new Random().nextDouble(), 6);
        double startLonBd = DecimalUtils.add(endLonBd, new Random().nextDouble(), 6);
        double[] locations = GPSUtils.bd09_To_gps84(endLatBd, endLonBd);
        double distance = GPSUtils.getDistance(locations[0], locations[1],
                startLatBd, startLonBd);
        String distanceStr = String.valueOf(distance);
        int startIndex = new Random().nextInt(10000);
        String res = "{success:true,code:200,message:'',data:" +
                "[{id:'" + startIndex + "',name:'Shop Item " + startIndex + "', distance:'" + distanceStr +
                "',imgUrl:'https://img.zcool.cn/community/019af55798a4090000018c1be7a078.jpg@1280w_1l_2o_100sh.webp'," +
                "products:[{name:'Product Item 1'}, " +
                "{name:'Product Item 2'},{name:'Product Item 3'}]}";
        for (int i = 1; i < 10; i++) {
            distance += 1333;
            distanceStr = String.valueOf(distance);
            res += ",{id:'" + (startIndex + i) + "',name:'Shop Item " + (startIndex + i) +
                    "', distance:'" + distanceStr + "',imgUrl:'https://img.zcool.cn/community/019af55798a4090000018c1be7a078.jpg@1280w_1l_2o_100sh.webp', " +
                    "products:[{name:'Product Item 1'}, {name:'Product Item 2'}]}";
        }
        res += "]}";
        try {
            return new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
    // Test code end
}