package com.pine.db_server.sqlite.server;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.pine.db_server.DbResponseGenerator;
import com.pine.db_server.DbSession;
import com.pine.db_server.sqlite.SQLiteDbHelper;
import com.pine.db_server.sqlite.SQLiteDbServerManager;
import com.pine.tool.request.impl.database.DbRequestBean;
import com.pine.tool.request.impl.database.DbResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.pine.db_server.DbConstants.ACCOUNT_TABLE_NAME;
import static com.pine.db_server.DbConstants.SHOP_TABLE_NAME;
import static com.pine.db_server.DbConstants.TRAVEL_NOTE_COMMENT_TABLE_NAME;
import static com.pine.db_server.DbConstants.TRAVEL_NOTE_SHOP_TABLE_NAME;
import static com.pine.db_server.DbConstants.TRAVEL_NOTE_TABLE_NAME;
import static com.pine.tool.request.IRequestManager.SESSION_ID;

public class SQLiteTravelNoteServer extends SQLiteBaseServer {

    public static DbResponse addTravelNote(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                           @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getWritableDatabase();
        try {
            DbSession session = SQLiteDbServerManager.getInstance().getOrGenerateSession(cookies.get(SESSION_ID));
            if (TextUtils.isEmpty(session.getAccountId())) {
                return DbResponseGenerator.getLoginFailJsonRep(requestBean, cookies, "请登录");
            } else {
                Map<String, String> requestParams = requestBean.getParams();
                Map<String, String> params = new HashMap<>();
                params.put("id", session.getAccountId());
                Cursor cursor = query(db, ACCOUNT_TABLE_NAME, params);
                if (cursor.moveToFirst()) {
                    requestParams.put("authorId", cursor.getString(cursor.getColumnIndex("id")));
                    requestParams.put("author", cursor.getString(cursor.getColumnIndex("name")));
                    requestParams.put("createTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                    requestParams.put("updateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                    cursor.close();
                    String travelNoteId = "1102" + new Date().getTime();
                    requestParams.put("id", travelNoteId);
                    String belongShopsStr = requestParams.remove("belongShops");
                    JSONArray belongShops = null;
                    try {
                        belongShops = new JSONArray();
                        JSONArray belongShopArr = new JSONArray(belongShopsStr);
                        for (int i = 0; i < belongShopArr.length(); i++) {
                            JSONObject belongShop = new JSONObject();
                            belongShop.put("travelNoteId", travelNoteId);
                            belongShop.put("shopId", belongShopArr.optJSONObject(i).optString("id"));
                            belongShops.put(belongShop);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (belongShops == null || belongShops.length() < 1) {
                        return DbResponseGenerator.getBadArgsJsonRep(requestBean, cookies);
                    }
                    db.beginTransaction();
                    boolean isSuccess = true;
                    for (int i = 0; i < belongShops.length(); i++) {
                        JSONObject belongShop = belongShops.optJSONObject(i);
                        if (belongShop != null) {
                            isSuccess = isSuccess && insert(db, TRAVEL_NOTE_SHOP_TABLE_NAME, belongShop) >= 0;
                        }
                    }
                    long id = insert(db, TRAVEL_NOTE_TABLE_NAME, requestParams);
                    isSuccess = isSuccess && id >= 0;
                    if (isSuccess) {
                        db.setTransactionSuccessful();
                        return DbResponseGenerator.getSuccessJsonRep(requestBean, cookies, "{'id':" + id + "}");
                    } else {
                        return DbResponseGenerator.getBadArgsJsonRep(requestBean, cookies);
                    }
                } else {
                    return DbResponseGenerator.getLoginFailJsonRep(requestBean, cookies, "不存在该用户");
                }
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

    public static DbResponse queryTravelNoteDetail(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                                   @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getReadableDatabase();
        try {
            db.beginTransaction();
            Cursor cursor = query(db, TRAVEL_NOTE_TABLE_NAME, requestBean.getParams());
            try {
                JSONObject jsonObject = new JSONObject();
                if (cursor.moveToFirst()) {
                    String travelNoteId = cursor.getString(cursor.getColumnIndex("id"));
                    jsonObject.put("id", travelNoteId);
                    jsonObject.put("title", cursor.getString(cursor.getColumnIndex("title")));
                    jsonObject.put("authorId", cursor.getString(cursor.getColumnIndex("authorId")));
                    jsonObject.put("author", cursor.getString(cursor.getColumnIndex("author")));
                    jsonObject.put("likeCount", cursor.getInt(cursor.getColumnIndex("likeCount")));
                    jsonObject.put("hot", cursor.getInt(cursor.getColumnIndex("hot")));
                    jsonObject.put("headImgUrl", cursor.getString(cursor.getColumnIndex("headImgUrl")));
                    jsonObject.put("readCount", cursor.getInt(cursor.getColumnIndex("readCount")));
                    jsonObject.put("preface", cursor.getString(cursor.getColumnIndex("preface")));
                    String sql = "select t2.id,t2.name from " +
                            TRAVEL_NOTE_SHOP_TABLE_NAME + " t1," + SHOP_TABLE_NAME + " t2 where t1.travelNoteId=? and t1.shopId=t2.id";
                    Cursor travelNoteShopCursor = db.rawQuery(sql, new String[]{travelNoteId});
                    JSONArray belongShops = new JSONArray();
                    while (travelNoteShopCursor.moveToNext()) {
                        JSONObject shop = new JSONObject();
                        shop.put("id", travelNoteShopCursor.getString(travelNoteShopCursor.getColumnIndex("id")));
                        shop.put("name", travelNoteShopCursor.getString(travelNoteShopCursor.getColumnIndex("name")));
                        belongShops.put(shop);
                    }
                    jsonObject.put("belongShops", belongShops);
                    JSONArray days = new JSONArray(cursor.getString(cursor.getColumnIndex("days")));
                    jsonObject.put("days", days);
                    jsonObject.put("setOutDate", cursor.getString(cursor.getColumnIndex("setOutDate")));
                    jsonObject.put("createTime", cursor.getString(cursor.getColumnIndex("createTime")));
                    jsonObject.put("updateTime", cursor.getString(cursor.getColumnIndex("updateTime")));
                }
                db.setTransactionSuccessful();
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
            if (db.inTransaction()) {
                db.endTransaction();
            }
            db.close();
        }
    }

    public static DbResponse queryTravelNoteList(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                                 @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getReadableDatabase();
        try {
            Map<String, String> params = requestBean.getParams();
            if (params.containsKey("id")) {
                params.put("shopId", params.remove("id"));
            }
            db.beginTransaction();
            Cursor cursor = query(db, TRAVEL_NOTE_SHOP_TABLE_NAME, requestBean.getParams());
            try {
                JSONArray jsonArray = new JSONArray();
                while (cursor.moveToNext()) {
                    String travelNoteId = cursor.getString(cursor.getColumnIndex("travelNoteId"));
                    Cursor travelNoteCursor = query(db, TRAVEL_NOTE_TABLE_NAME, null,
                            "id=?", new String[]{travelNoteId}, null, null, null);
                    if (travelNoteCursor.moveToFirst()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id", travelNoteCursor.getString(travelNoteCursor.getColumnIndex("id")));
                        jsonObject.put("title", travelNoteCursor.getString(travelNoteCursor.getColumnIndex("title")));
                        jsonObject.put("author", travelNoteCursor.getString(travelNoteCursor.getColumnIndex("author")));
                        jsonObject.put("likeCount", travelNoteCursor.getInt(travelNoteCursor.getColumnIndex("likeCount")));
                        jsonObject.put("hot", travelNoteCursor.getInt(travelNoteCursor.getColumnIndex("hot")));
                        jsonObject.put("readCount", travelNoteCursor.getInt(travelNoteCursor.getColumnIndex("readCount")));
                        jsonObject.put("createTime", travelNoteCursor.getString(travelNoteCursor.getColumnIndex("createTime")));
                        jsonObject.put("updateTime", travelNoteCursor.getString(travelNoteCursor.getColumnIndex("updateTime")));
                        jsonArray.put(jsonObject);
                    }
                }
                db.setTransactionSuccessful();
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
            if (db.inTransaction()) {
                db.endTransaction();
            }
            db.close();
        }
    }

    public static DbResponse queryTravelNoteCommentList(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                                        @NonNull HashMap<String, String> cookies) {
        SQLiteDatabase db = new SQLiteDbHelper(context).getReadableDatabase();
        try {
            Map<String, String> params = requestBean.getParams();
            if (params.containsKey("id")) {
                params.put("travelNoteId", params.remove("id"));
            }
            Cursor cursor = query(db, TRAVEL_NOTE_COMMENT_TABLE_NAME, requestBean.getParams());
            try {
                JSONArray jsonArray = new JSONArray();
                while (cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", cursor.getString(cursor.getColumnIndex("id")));
                    jsonObject.put("content", cursor.getString(cursor.getColumnIndex("content")));
                    jsonObject.put("author", cursor.getString(cursor.getColumnIndex("author")));
                    jsonObject.put("headImgUrl", cursor.getString(cursor.getColumnIndex("headImgUrl")));
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
}
