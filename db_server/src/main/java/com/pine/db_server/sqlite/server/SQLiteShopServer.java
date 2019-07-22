package com.pine.db_server.sqlite.server;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.pine.db_server.DbResponseGenerator;
import com.pine.db_server.DbSession;
import com.pine.db_server.sqlite.SQLiteDbHelper;
import com.pine.db_server.sqlite.SQLiteDbServerManager;
import com.pine.tool.request.impl.database.DbRequestBean;
import com.pine.tool.request.impl.database.DbResponse;
import com.pine.tool.util.RegexUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.pine.db_server.DbConstants.ACCOUNT_TABLE_NAME;
import static com.pine.db_server.DbConstants.PRODUCT_TABLE_NAME;
import static com.pine.db_server.DbConstants.SHOP_TABLE_NAME;
import static com.pine.tool.request.IRequestManager.SESSION_ID;

public class SQLiteShopServer extends SQLiteBaseServer {

    public static DbResponse addShop(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                     @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getWritableDatabase();
        try {
            DbSession session = SQLiteDbServerManager.getInstance().getOrGenerateSession(cookies.get(SESSION_ID));
            if (TextUtils.isEmpty(session.getAccountId())) {
                return DbResponseGenerator.getLoginFailJsonRep(requestBean, cookies, "请登录");
            } else {
                Map<String, String> params = new HashMap<>();
                params.put("id", session.getAccountId());
                Cursor cursor = query(db, ACCOUNT_TABLE_NAME, params);
                if (cursor.moveToFirst()) {
                    Map<String, String> requestParams = requestBean.getParams();
                    requestParams.put("id", "1100" + new Date().getTime());
                    requestParams.put("accountId", cursor.getString(cursor.getColumnIndex("id")));
                    requestParams.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                    requestParams.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                    long id = insert(db, SHOP_TABLE_NAME, requestParams);
                    if (id == -1) {
                        return DbResponseGenerator.getBadArgsJsonRep(requestBean, cookies);
                    } else {
                        return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, "{'id':" + id + "}");
                    }
                } else {
                    return DbResponseGenerator.getLoginFailJsonRep(requestBean, cookies, "不存在该用户");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return DbResponseGenerator.getExceptionJsonRep(requestBean, cookies, e);
        } finally {
            db.close();
        }
    }

    public static DbResponse queryShopDetail(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                             @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getReadableDatabase();
        try {
            Map<String, String> params = requestBean.getParams();
            Cursor cursor = query(db, SHOP_TABLE_NAME, params);
            try {
                JSONObject jsonObject = new JSONObject();
                if (cursor.moveToFirst()) {
                    jsonObject.put("id", cursor.getString(cursor.getColumnIndex("id")));
                    jsonObject.put("name", cursor.getString(cursor.getColumnIndex("name")));
                    jsonObject.put("type", cursor.getString(cursor.getColumnIndex("type")));
                    jsonObject.put("typeName", cursor.getString(cursor.getColumnIndex("typeName")));
                    jsonObject.put("onlineDate", cursor.getString(cursor.getColumnIndex("onlineDate")));
                    jsonObject.put("mobile", cursor.getString(cursor.getColumnIndex("mobile")));
                    jsonObject.put("latitude", cursor.getString(cursor.getColumnIndex("latitude")));
                    jsonObject.put("longitude", cursor.getString(cursor.getColumnIndex("longitude")));
                    jsonObject.put("addressDistrict", cursor.getString(cursor.getColumnIndex("addressDistrict")));
                    jsonObject.put("addressZipCode", cursor.getString(cursor.getColumnIndex("addressZipCode")));
                    jsonObject.put("addressStreet", cursor.getString(cursor.getColumnIndex("addressStreet")));
                    jsonObject.put("mainImgUrl", cursor.getString(cursor.getColumnIndex("mainImgUrl")));
                    jsonObject.put("imgUrls", cursor.getString(cursor.getColumnIndex("imgUrls")));
                    jsonObject.put("description", cursor.getString(cursor.getColumnIndex("description")));
                    jsonObject.put("remark", cursor.getString(cursor.getColumnIndex("remark")));
                    jsonObject.put("createTime", cursor.getString(cursor.getColumnIndex("createTime")));
                    jsonObject.put("updateTime", cursor.getString(cursor.getColumnIndex("updateTime")));
                }
                cursor.close();
                return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                cursor.close();
                return DbResponseGenerator.getExceptionJsonRep(requestBean, cookies, e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return DbResponseGenerator.getExceptionJsonRep(requestBean, cookies, e);
        } finally {
            db.close();
        }
    }

    public static DbResponse queryShopList(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                           @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getReadableDatabase();
        try {
            Map<String, String> params = requestBean.getParams();
            String latitudeStr = params.remove("latitude");
            String longitudeStr = params.remove("longitude");
            String searchKey = params.remove("searchKey");
            Cursor cursor = null;
            if (!TextUtils.isEmpty(searchKey)) {
                params.put("name", searchKey);
                cursor = dimSearch(db, SHOP_TABLE_NAME, params);
            } else {
                cursor = query(db, SHOP_TABLE_NAME, params);
            }
            try {
                JSONArray jsonArray = new JSONArray();
                while (cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", cursor.getString(cursor.getColumnIndex("id")));
                    jsonObject.put("name", cursor.getString(cursor.getColumnIndex("name")));
                    jsonObject.put("type", cursor.getString(cursor.getColumnIndex("type")));
                    jsonObject.put("typeName", cursor.getString(cursor.getColumnIndex("typeName")));
                    jsonObject.put("mobile", cursor.getString(cursor.getColumnIndex("mobile")));
                    double startLatitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                    double startLongitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                    double distance = getDistance(latitudeStr, longitudeStr, startLatitude, startLongitude);
                    if (distance > 0) {
                        jsonObject.put("distance", String.valueOf(distance));
                    }
                    jsonObject.put("mainImgUrl", cursor.getString(cursor.getColumnIndex("mainImgUrl")));
                    jsonObject.put("createTime", cursor.getString(cursor.getColumnIndex("createTime")));
                    jsonObject.put("updateTime", cursor.getString(cursor.getColumnIndex("updateTime")));
                    jsonArray.put(jsonObject);
                }
                cursor.close();
                return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                cursor.close();
                return DbResponseGenerator.getExceptionJsonRep(requestBean, cookies, e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return DbResponseGenerator.getExceptionJsonRep(requestBean, cookies, e);
        } finally {
            db.close();
        }
    }

    public static DbResponse queryShopProductList(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                                  @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getReadableDatabase();
        try {
            Map<String, String> params = requestBean.getParams();
            String latitudeStr = params.remove("latitude");
            String longitudeStr = params.remove("longitude");
            db.beginTransaction();
            Cursor cursor = query(db, SHOP_TABLE_NAME, params);
            try {
                JSONArray shopArr = new JSONArray();
                while (cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    jsonObject.put("id", id);
                    jsonObject.put("name", cursor.getString(cursor.getColumnIndex("name")));
                    jsonObject.put("type", cursor.getString(cursor.getColumnIndex("type")));
                    jsonObject.put("typeName", cursor.getString(cursor.getColumnIndex("typeName")));
                    jsonObject.put("mobile", cursor.getString(cursor.getColumnIndex("mobile")));
                    double startLatitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                    double startLongitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                    double distance = getDistance(latitudeStr, longitudeStr, startLatitude, startLongitude);
                    if (distance > 0) {
                        jsonObject.put("distance", String.valueOf(distance));
                    }
                    jsonObject.put("mainImgUrl", cursor.getString(cursor.getColumnIndex("mainImgUrl")));
                    jsonObject.put("createTime", cursor.getString(cursor.getColumnIndex("createTime")));
                    jsonObject.put("updateTime", cursor.getString(cursor.getColumnIndex("updateTime")));
                    Cursor productCursor = query(db, PRODUCT_TABLE_NAME, new String[]{"id", "name"},
                            "shopId=?", new String[]{id}, null, null, null);
                    JSONArray productArr = new JSONArray();
                    while (productCursor.moveToNext()) {
                        JSONObject productObj = new JSONObject();
                        productObj.put("id", productCursor.getString(productCursor.getColumnIndex("id")));
                        productObj.put("name", productCursor.getString(productCursor.getColumnIndex("name")));
                        productArr.put(productObj);
                    }
                    jsonObject.put("products", productArr);
                    shopArr.put(jsonObject);
                }
                db.setTransactionSuccessful();
                cursor.close();
                return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, shopArr.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                cursor.close();
                return DbResponseGenerator.getExceptionJsonRep(requestBean, cookies, e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return DbResponseGenerator.getExceptionJsonRep(requestBean, cookies, e);
        } finally {
            if (db.inTransaction()) {
                db.endTransaction();
            }
            db.close();
        }
    }

    public static DbResponse addProduct(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                        @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getWritableDatabase();
        try {
            DbSession session = SQLiteDbServerManager.getInstance().getOrGenerateSession(cookies.get(SESSION_ID));
            if (TextUtils.isEmpty(session.getAccountId())) {
                return DbResponseGenerator.getLoginFailJsonRep(requestBean, cookies, "请登录");
            } else {
                Map<String, String> queryParams = new HashMap<>();
                queryParams.put("id", session.getAccountId());
                Cursor cursor = query(db, ACCOUNT_TABLE_NAME, queryParams);
                if (cursor.moveToFirst()) {
                    Map<String, String> requestParams = requestBean.getParams();
                    requestParams.put("id", "1101" + new Date().getTime());
                    requestParams.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                    requestParams.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                    long id = insert(db, PRODUCT_TABLE_NAME, requestParams);
                    if (id == -1) {
                        return DbResponseGenerator.getBadArgsJsonRep(requestBean, cookies);
                    } else {
                        return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, "{'id':" + id + "}");
                    }
                } else {
                    return DbResponseGenerator.getLoginFailJsonRep(requestBean, cookies, "不存在该用户");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return DbResponseGenerator.getExceptionJsonRep(requestBean, cookies, e);
        } finally {
            db.close();
        }
    }

    private static float getDistance(String endLatitudeStr, String endLongitudeStr,
                                     double startLatitude, double startLongitude) {
        if (RegexUtils.isMathNumber(endLatitudeStr) && RegexUtils.isMathNumber(endLongitudeStr)) {
            double endLatitude = Double.parseDouble(endLatitudeStr);
            double endLongitude = Double.parseDouble(endLongitudeStr);
            float[] distance = new float[1];
            Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, distance);
            return distance[0];
        }
        return 0;
    }
}
