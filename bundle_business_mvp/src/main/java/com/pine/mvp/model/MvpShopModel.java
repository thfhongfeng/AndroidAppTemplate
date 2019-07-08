package com.pine.mvp.model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pine.config.BuildConfig;
import com.pine.mvp.MvpConstants;
import com.pine.mvp.MvpUrlConstants;
import com.pine.mvp.bean.MvpProductDetailEntity;
import com.pine.mvp.bean.MvpShopAndProductEntity;
import com.pine.mvp.bean.MvpShopDetailEntity;
import com.pine.mvp.bean.MvpShopItemEntity;
import com.pine.tool.architecture.mvp.model.IModelAsyncResponse;
import com.pine.tool.exception.BusinessException;
import com.pine.tool.request.RequestManager;
import com.pine.tool.request.callback.JsonCallback;
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

public class MvpShopModel {
    private final String TAG = LogUtils.makeLogTag(this.getClass());
    private static final int REQUEST_ADD_SHOP = 1;
    private static final int REQUEST_QUERY_SHOP_DETAIL = 2;
    private static final int REQUEST_QUERY_SHOP_LIST = 3;
    private static final int REQUEST_QUERY_SHOP_AND_PRODUCT_LIST = 4;
    private static final int REQUEST_ADD_PRODUCT = 5;

    public void requestAddShop(final Map<String, String> params,
                               @NonNull final IModelAsyncResponse<MvpShopDetailEntity> callback) {
        String url = MvpUrlConstants.Add_Shop;
        RequestManager.setJsonRequest(url, params, TAG, REQUEST_ADD_SHOP,
                handleResponse(callback, params));
    }

    public void requestShopDetailData(final Map<String, String> params,
                                      @NonNull final IModelAsyncResponse<MvpShopDetailEntity> callback) {
        String url = MvpUrlConstants.Query_ShopDetail;
        RequestManager.setJsonRequest(url, params, TAG, REQUEST_QUERY_SHOP_DETAIL,
                handleResponse(callback, params));
    }

    public void requestShopListData(final Map<String, String> params,
                                    @NonNull final IModelAsyncResponse<ArrayList<MvpShopItemEntity>> callback) {
        String url = MvpUrlConstants.Query_ShopList;
        RequestManager.setJsonRequest(url, params, TAG, REQUEST_QUERY_SHOP_LIST,
                handleResponse(callback, params));
    }

    public void requestShopAndProductListData(Map<String, String> params,
                                              @NonNull final IModelAsyncResponse<ArrayList<MvpShopAndProductEntity>> callback) {
        String url = MvpUrlConstants.Query_ShopAndProductList;
        RequestManager.setJsonRequest(url, params, TAG, REQUEST_QUERY_SHOP_AND_PRODUCT_LIST,
                handleResponse(callback, params));
    }

    public void requestAddProduct(final Map<String, String> params,
                                  @NonNull final IModelAsyncResponse<MvpProductDetailEntity> callback) {
        String url = MvpUrlConstants.Add_Product;
        RequestManager.setJsonRequest(url, params, TAG, REQUEST_ADD_PRODUCT,
                handleResponse(callback, params));
    }

    private <T> JsonCallback handleResponse(final IModelAsyncResponse<T> callback,
                                            final Object carryData) {
        return new JsonCallback() {
            @Override
            public void onResponse(int what, JSONObject jsonObject) {
                if (what == REQUEST_ADD_SHOP) {
                    // Test code begin
                    if (!"local".equalsIgnoreCase(BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER)) {
                        try {
                            jsonObject = new JSONObject("{success:true,code:200,message:'',data:{id:'110020190328102000000008'}}");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    // Test code end
                    if (jsonObject.optBoolean(MvpConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvpConstants.DATA), new TypeToken<MvpShopDetailEntity>() {
                        }.getType());
                        if (callback != null) {
                            callback.onResponse(retData);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFail(new BusinessException(jsonObject.optString("message")));
                        }
                    }
                } else if (what == REQUEST_QUERY_SHOP_DETAIL) {
                    // Test code begin
                    if (!"local".equalsIgnoreCase(BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER)) {
                        jsonObject = getShopDetailData(carryData);
                    }
                    // Test code end
                    if (jsonObject.optBoolean(MvpConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvpConstants.DATA), new TypeToken<MvpShopDetailEntity>() {
                        }.getType());
                        if (callback != null) {
                            callback.onResponse(retData);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFail(new BusinessException(jsonObject.optString("message")));
                        }
                    }
                } else if (what == REQUEST_QUERY_SHOP_LIST) {
                    // Test code begin
                    if (!"local".equalsIgnoreCase(BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER)) {
                        jsonObject = getShopListData(carryData);
                    }
                    // Test code end
                    if (jsonObject.optBoolean(MvpConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvpConstants.DATA), new TypeToken<List<MvpShopItemEntity>>() {
                        }.getType());
                        if (callback != null) {
                            callback.onResponse(retData);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFail(new BusinessException(jsonObject.optString("message")));
                        }
                    }
                } else if (what == REQUEST_QUERY_SHOP_AND_PRODUCT_LIST) {
                    // Test code begin
                    if (!"local".equalsIgnoreCase(BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER)) {
                        jsonObject = getShopAndProductListData(carryData);
                    }
                    // Test code end
                    if (jsonObject.optBoolean(MvpConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvpConstants.DATA), new TypeToken<List<MvpShopAndProductEntity>>() {
                        }.getType());
                        if (callback != null) {
                            callback.onResponse(retData);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFail(new BusinessException(jsonObject.optString("message")));
                        }
                    }
                } else if (what == REQUEST_ADD_PRODUCT) {
                    // Test code begin
                    if (!"local".equalsIgnoreCase(BuildConfig.APP_THIRD_DATA_SOURCE_PROVIDER)) {
                        try {
                            jsonObject = new JSONObject("{success:true,code:200,message:'',data:{id:'110120190328102000000008'}}");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    // Test code end
                    if (jsonObject.optBoolean(MvpConstants.SUCCESS)) {
                        T retData = new Gson().fromJson(jsonObject.optString(MvpConstants.DATA), new TypeToken<MvpProductDetailEntity>() {
                        }.getType());
                        if (callback != null) {
                            callback.onResponse(retData);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFail(new BusinessException(jsonObject.optString("message")));
                        }
                    }
                }
            }

            @Override
            public boolean onFail(int what, Exception e) {
                if (callback != null) {
                    return callback.onFail(e);
                }
                return false;
            }

            @Override
            public void onCancel(int what) {
                if (callback != null) {
                    callback.onCancel();
                }
            }
        };
    }

    // Test code begin
    private JSONObject getShopDetailData(Object paramsObj) {
        Map<String, String> params = (HashMap<String, String>) paramsObj;
        double endLatBd = 31.221367;
        double endLonBd = 121.635707;
        double startLatBd = DecimalUtils.add(endLatBd, new Random().nextDouble(), 6);
        double startLonBd = DecimalUtils.add(endLonBd, new Random().nextDouble(), 6);
        double[] locations = GPSUtils.bd09_To_gps84(endLatBd, endLonBd);
        double distance = GPSUtils.getDistance(locations[0], locations[1],
                startLatBd, startLonBd);
        String distanceStr = String.valueOf(distance);
        String id = params.get("id");
        String index = id.substring(id.length() - 2);
        if ("0".equals(index.substring(0, 1))) {
            index = index.substring(1, 2);
        }
        String imgUrls = new Random().nextInt(10) > 1 ?
                "http://pic31.nipic.com/20130720/5793914_122325176000_2.jpg,https://hellorfimg.zcool.cn/preview/70789213.jpg"
                : "";
        String res = "{success:true,code:200,message:'',data:" +
                "{id:'" + id + "',name:'Shop Item " + index +
                "',type:'2',typeName:'食品',mainImgUrl:'http://pic31.nipic.com/20130720/5793914_122325176000_2.jpg'" +
                ",distance:'" + distanceStr + "',latitude:'" + endLatBd + "',longitude:'" + endLonBd +
                "',imgUrls:'" + imgUrls + "'" +
                ",onlineDate:'2019-03-01',mobile:'18672943566'" +
                ",addressDistrict:'上海市浦东新区浦东新区',addressZipCode:'310115'" +
                ",addressStreet:'盛夏路888号'" +
                ",description:'Shop Detail description'}}";
        try {
            return new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private JSONObject getShopListData(Object paramsObj) {
        if (new Random().nextInt(10) == 9) {
            try {
                return new JSONObject("{success:true,code:200,message:'',data:[]}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        Map<String, String> params = (HashMap<String, String>) paramsObj;
        int pageNo = params.containsKey(MvpConstants.PAGE_NO) ? Integer.parseInt(params.get(MvpConstants.PAGE_NO)) : 1;
        int pageSize = params.containsKey(MvpConstants.PAGE_SIZE) ? Integer.parseInt(params.get(MvpConstants.PAGE_SIZE)) : 12;
        double endLatBd = 31.221367;
        double endLonBd = 121.635707;
        double startLatBd = DecimalUtils.add(endLatBd, new Random().nextDouble(), 6);
        double startLonBd = DecimalUtils.add(endLonBd, new Random().nextDouble(), 6);
        double[] locations = GPSUtils.bd09_To_gps84(endLatBd, endLonBd);
        double distance = GPSUtils.getDistance(locations[0], locations[1],
                startLatBd, startLonBd);
        String distanceStr = String.valueOf(distance);
        int index = (pageNo - 1) * 10 + 1;
        String id = "1100201903281020000000" + (index > 9 ? index : "0" + index);
        String res = "{success:true,code:200,message:'',data:" +
                "[{id:'" + id + "',name:'Shop Item " + index +
                "', distance:'" + distanceStr + "',mainImgUrl:''}";
        for (int i = 1; i < pageSize; i++) {
            distance += 1333;
            distanceStr = String.valueOf(distance);
            index++;
            id = "1100201903281020000000" + (index > 9 ? index : "0" + index);
            res += ",{id:'" + id + "',name:'Shop Item " + index +
                    "', distance:'" + distanceStr + "',mainImgUrl:'http://pic31.nipic.com/20130720/5793914_122325176000_2.jpg'}";
        }
        res += "]}";
        try {
            return new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private JSONObject getShopAndProductListData(Object paramsObj) {
        if (new Random().nextInt(5) == 4) {
            try {
                return new JSONObject("{success:true,code:200,message:'',data:[]}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        Map<String, String> params = (HashMap<String, String>) paramsObj;
        int pageNo = params.containsKey(MvpConstants.PAGE_NO) ? Integer.parseInt(params.get(MvpConstants.PAGE_NO)) : 1;
        int pageSize = params.containsKey(MvpConstants.PAGE_SIZE) ? Integer.parseInt(params.get(MvpConstants.PAGE_SIZE)) : 12;
        double endLatBd = 31.221367;
        double endLonBd = 121.635707;
        double startLatBd = DecimalUtils.add(endLatBd, new Random().nextDouble(), 6);
        double startLonBd = DecimalUtils.add(endLonBd, new Random().nextDouble(), 6);
        double[] locations = GPSUtils.bd09_To_gps84(endLatBd, endLonBd);
        double distance = GPSUtils.getDistance(locations[0], locations[1],
                startLatBd, startLonBd);
        String distanceStr = String.valueOf(distance);
        int shopIndex = (pageNo - 1) * 10 + 1;
        String shopId = "1100201903281020000000" + (shopIndex > 9 ? shopIndex : "0" + shopIndex);
        String res = "{success:true,code:200,message:'',data:" +
                "[{id:'" + shopId + "',name:'Shop Item " + shopIndex + "', distance:'" + distanceStr +
                "',mainImgUrl:''," +
                "products:[{name:'Product Item 1'}, " +
                "{name:'Product Item 2'},{name:'Product Item 3'}]}";
        for (int i = 1; i < pageSize; i++) {
            distance += 1333;
            distanceStr = String.valueOf(distance);
            shopIndex++;
            shopId = "1100201903281020000000" + (shopIndex > 9 ? shopIndex : "0" + shopIndex);
            res += ",{id:'" + shopId + "',name:'Shop Item " + shopIndex +
                    "', distance:'" + distanceStr + "',mainImgUrl:'http://pic31.nipic.com/20130720/5793914_122325176000_2.jpg', " +
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
