package com.pine.db_server.sqlite.server;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.pine.base.request.database.DbRequestBean;
import com.pine.base.request.database.DbResponse;
import com.pine.db_server.sqlite.DbResponseGenerator;
import com.pine.db_server.sqlite.SQLiteDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.pine.db_server.DbConstants.TRAVEL_NOTE_COMMENT_TABLE_NAME;
import static com.pine.db_server.DbConstants.TRAVEL_NOTE_SHOP_TABLE_NAME;
import static com.pine.db_server.DbConstants.TRAVEL_NOTE_TABLE_NAME;

public class SQLiteTravelNoteServer extends SQLiteBaseServer {

    public static DbResponse addTravelNote(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                           @NonNull HashMap<String, HashMap<String, String>> header) {
        try {
            long id = insert(new SQLiteDbHelper(context).getWritableDatabase(),
                    TRAVEL_NOTE_TABLE_NAME, requestBean.getParams());
            if (id == -1) {
                return DbResponseGenerator.getBadArgsRep(requestBean, header);
            } else {
                return DbResponseGenerator.getSuccessRep(requestBean, header, "{'id':" + id + "}");
            }
        } catch (SQLException e) {
            return DbResponseGenerator.getExceptionRep(requestBean, header, e);
        }
    }

    public static DbResponse queryTravelNoteDetail(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                                   @NonNull HashMap<String, HashMap<String, String>> header) {
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
                    jsonObject.put("isLike", cursor.getString(cursor.getColumnIndex("isLike")));
                    jsonObject.put("headImgUrl", cursor.getString(cursor.getColumnIndex("headImgUrl")));
                    jsonObject.put("readCount", cursor.getInt(cursor.getColumnIndex("readCount")));
                    jsonObject.put("preface", cursor.getString(cursor.getColumnIndex("preface")));
                    String sql = "select t2.id,t2.name from travel_note_shop t1,shop t2 where t1.travelNoteId=? and t1.shopId=t2.id";
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
                return DbResponseGenerator.getSuccessRep(requestBean, header, jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return DbResponseGenerator.getExceptionRep(requestBean, header, e);
            } finally {
                cursor.close();
                db.endTransaction();
            }
        } catch (SQLException e) {
            db.endTransaction();
            return DbResponseGenerator.getExceptionRep(requestBean, header, e);
        }
    }

    public static DbResponse queryTravelNoteList(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                                 @NonNull HashMap<String, HashMap<String, String>> header) {
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
                        jsonObject.put("isLike", travelNoteCursor.getString(travelNoteCursor.getColumnIndex("isLike")));
                        jsonObject.put("readCount", travelNoteCursor.getInt(travelNoteCursor.getColumnIndex("readCount")));
                        jsonObject.put("createTime", travelNoteCursor.getString(travelNoteCursor.getColumnIndex("createTime")));
                        jsonObject.put("updateTime", travelNoteCursor.getString(travelNoteCursor.getColumnIndex("updateTime")));
                        jsonArray.put(jsonObject);
                    }
                }
                db.setTransactionSuccessful();
                return DbResponseGenerator.getSuccessRep(requestBean, header, jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return DbResponseGenerator.getExceptionRep(requestBean, header, e);
            } finally {
                cursor.close();
                db.endTransaction();
            }
        } catch (SQLException e) {
            db.endTransaction();
            return DbResponseGenerator.getExceptionRep(requestBean, header, e);
        }
    }

    public static DbResponse queryTravelNoteCommentList(@NonNull Context context, @NonNull DbRequestBean requestBean,
                                                        @NonNull HashMap<String, HashMap<String, String>> header) {
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
}
