package com.pine.base.database.sqlite.server;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.support.annotation.NonNull;

import com.pine.base.database.DbRequestBean;
import com.pine.base.database.DbResponse;
import com.pine.base.database.DbResponseGenerator;
import com.pine.base.database.sqlite.SQLiteDbHelper;
import com.pine.tool.util.RegexUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static com.pine.base.database.IDbRequestManager.PRODUCT_TABLE_NAME;
import static com.pine.base.database.IDbRequestManager.SHOP_TABLE_NAME;

public class SQLiteShopServer extends SQLiteBaseServer {

    public static DbResponse addShop(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                     @NonNull Map<String, Map<String, String>> header) {
        try {
            long id = insert(new SQLiteDbHelper(context).getWritableDatabase(),
                    SHOP_TABLE_NAME, requestBean.getParams());
            if (id == -1) {
                return DbResponseGenerator.getBadArgsRep(requestBean, header);
            } else {
                return DbResponseGenerator.getSuccessRep(requestBean, header, "{'id':" + id + "}");
            }
        } catch (SQLException e) {
            return DbResponseGenerator.getExceptionRep(requestBean, header, e);
        }
    }

    public static DbResponse queryShopDetail(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                             @NonNull Map<String, Map<String, String>> header) {
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
                }
                return DbResponseGenerator.getSuccessRep(requestBean, header, jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return DbResponseGenerator.getExceptionRep(requestBean, header, e);
            } finally {
                cursor.close();
            }
        } catch (SQLException e) {
            return DbResponseGenerator.getExceptionRep(requestBean, header, e);
        }
    }

    public static DbResponse queryShopList(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                           @NonNull Map<String, Map<String, String>> header) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getReadableDatabase();
        try {
            Map<String, String> params = requestBean.getParams();
            String latitudeStr = params.remove("latitude");
            String longitudeStr = params.remove("longitude");
            Cursor cursor = query(db, SHOP_TABLE_NAME, params);
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
                    jsonArray.put(jsonObject);
                }
                return DbResponseGenerator.getSuccessRep(requestBean, header, jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return DbResponseGenerator.getExceptionRep(requestBean, header, e);
            } finally {
                cursor.close();
            }
        } catch (SQLException e) {
            return DbResponseGenerator.getExceptionRep(requestBean, header, e);
        }
    }

    public static DbResponse queryShopProductList(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                                  @NonNull Map<String, Map<String, String>> header) {
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
                return DbResponseGenerator.getSuccessRep(requestBean, header, shopArr.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return DbResponseGenerator.getExceptionRep(requestBean, header, e);
            } finally {
                db.endTransaction();
                cursor.close();
            }
        } catch (SQLException e) {
            if (db.inTransaction()) {
                db.endTransaction();
            }
            return DbResponseGenerator.getExceptionRep(requestBean, header, e);
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
